package smartmirror;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import module.Module;
import utils.Config;
import utils.Position;

/**
 * Mirror View controller class
 *
 * @author hasan
 */
public class MirrorViewController implements Initializable {
    
    public static boolean modulesHidden = false;
    private static final BlockingQueue<String> alerts = new LinkedBlockingQueue<>();
    private String lastAlertMsg = "";
    private long lastAlertTimestamp = 0;
    private final List<Module> modulesOnMirror = new ArrayList<>();
    private final Map<Position, Pane> spaces = new HashMap<>();
    
    // Module containers
    @FXML
    private Pane topRight;
    @FXML
    private Pane topLeft;
    @FXML
    private Pane bottomLeft;
    @FXML
    private Pane bottomRight;
    @FXML
    private Pane bottom;
    @FXML
    private HBox top;
    
    @FXML
    private TextArea alertPrompt;
    @FXML
    private Button hideButton;
    @FXML
    private Label webServiceAddr;
    
    private FadeTransition buttonFadeInOut;

    @FXML
    private Separator separator;
    
            
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Put spaces in the map
        spaces.put(Position.TOP, top);
        spaces.put(Position.TOPLEFT, topLeft);
        spaces.put(Position.TOPRIGHT, topRight);
        spaces.put(Position.BOTTOMLEFT, bottomLeft);
        spaces.put(Position.BOTTOMRIGHT, bottomRight);
        spaces.put(Position.BOTTOM, bottom);
        
        alertPrompt.setOpacity(0);
        alertProcessing();
        buttonFadeInOut = new FadeTransition(Duration.millis(1000), hideButton);
        placeModules();
        String webAddress = System.getenv("WEBADDRESS");
        if (webAddress == null || webAddress.isEmpty()){
            webAddress = "Web service not running";
        }
        webServiceAddr.setText(webAddress);
    }
    
    @FXML
    private void hideShowButtonPressed(ActionEvent event){
        toggleMirrorDisplay();
    }
    
    public static void putAlert(String msg) {
        try {
            alerts.put(msg);
        } catch (InterruptedException ex) {
            Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void alertProcessing(){
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (true) {
                try {
                    String message = alerts.take();
                    long timestamp = System.currentTimeMillis() / 1000;
                   
                    // Avoid same alerts being spammed
                    if (!(message.equals(lastAlertMsg) && (timestamp - lastAlertTimestamp) < 10)) { 
                        displayAlert(message);
                    }
                    lastAlertTimestamp = timestamp;
                    lastAlertMsg = message;  
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
    
    private void displayAlert(String message) {
        new Thread(() -> {
            alertPrompt.setText(message);
            
            FadeTransition promptFade = new FadeTransition(new Duration(500), alertPrompt);
            Platform.runLater(() -> {
                promptFade.setFromValue(alertPrompt.getOpacity() == 1.0 ? 1.0 : 0.0);
                promptFade.setToValue(alertPrompt.getOpacity() == 1.0 ? 0.0 : 1.0);
                promptFade.play();
            });
            
            try {
                Thread.sleep(8000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Platform.runLater(() -> {
                promptFade.setFromValue(alertPrompt.getOpacity() == 1.0 ? 1.0 : 0.0);
                promptFade.setToValue(alertPrompt.getOpacity() == 1.0 ? 0.0 : 1.0);
                promptFade.play(); 
            });

            while(alertPrompt.getOpacity() != 0.0){/*wait*/}    
        }).start();
    }
    
    private void toggleMirrorDisplay(){
        Duration duration = new Duration(!modulesHidden ? 500 : 1000);

        FadeTransition sepFade = new FadeTransition(duration, separator);
        sepFade.setFromValue(!modulesHidden ? 1.0 : 0.0);
        sepFade.setToValue(!modulesHidden ? 0.0 : 1.0);

        FadeTransition webLabelFade = new FadeTransition(duration, webServiceAddr);
        webLabelFade.setFromValue(!modulesHidden ? 0.45 : 0.0);
        webLabelFade.setToValue(!modulesHidden ? 0.0 : 0.45);

        sepFade.play();
        webLabelFade.play();

        modulesOnMirror.forEach((module) -> {
            if (modulesHidden){
                module.fadeIn();
            }
            else{
                module.fadeOut();
            }
        });

        buttonFadeInOut.setFromValue(1.0);
        buttonFadeInOut.setToValue(0.1);
        buttonFadeInOut.setCycleCount(FadeTransition.INDEFINITE);
        buttonFadeInOut.setAutoReverse(true);

        modulesHidden = !modulesHidden;

        if (modulesHidden){
            buttonFadeInOut.play();
        }
        else{
            buttonFadeInOut.stop();
            buttonFadeInOut.setFromValue(hideButton.getOpacity());
            buttonFadeInOut.setToValue(1.0);
            buttonFadeInOut.setCycleCount(1);
            buttonFadeInOut.setAutoReverse(false);
            buttonFadeInOut.play();
        }
    }
    
    void clearAllContainers(){
        Platform.runLater(() -> {
            spaces.forEach((k, space) -> {
               space.getChildren().clear();
            });
            modulesOnMirror.clear();
        });
    }
    
    void placeModules(){
        Platform.runLater(() -> {
            for (Position position : Position.values()) {
                Module module = Config.getModuleAt(position);
                if (module != null && module.getPosition() != Position.NONE) {
                    Pane space = spaces.get(position);
                    space.getChildren().setAll(module);
                    modulesOnMirror.add(module);
                    if (modulesHidden) {
                        module.setOpacity(0);
                    }
                }
                else {
                    modulesOnMirror.remove(module);
                }
            }
        });
    }
}
