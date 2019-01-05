package smartmirror;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Mirror View controller class
 *
 * @author hasan
 */
public class MirrorViewController implements Initializable {
    
    public static boolean modulesHidden = false;
    private static final BlockingQueue<String> alerts = new LinkedBlockingQueue<>();
    private final List<Module> modulesOnMirror = new ArrayList<>();
    
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

    @FXML
    private void hideShowButtonPressed(ActionEvent event){
        hideShowAllModules();
    }
    
    public static void putAlert(String msg) {
        if (alerts.size() < 5) {
            try {
                alerts.put(msg);
            } catch (InterruptedException ex) {
                Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void alertProcessing(){
        new Thread(() -> {
            while (true) {
                try{
                    String message = alerts.take();

                    alertPrompt.setText(message);

                    FadeTransition promptFade = new FadeTransition(new Duration(500), alertPrompt);
                    promptFade.setFromValue(alertPrompt.getOpacity() == 1.0 ? 1.0 : 0.0);
                    promptFade.setToValue(alertPrompt.getOpacity() == 1.0 ? 0.0 : 1.0);
                    promptFade.play();

                    Thread.sleep(8000);

                    promptFade.setFromValue(alertPrompt.getOpacity() == 1.0 ? 1.0 : 0.0);
                    promptFade.setToValue(alertPrompt.getOpacity() == 1.0 ? 0.0 : 1.0);
                    promptFade.play();

                    while(alertPrompt.getOpacity() != 0.0){/*wait*/}
                }
                catch(InterruptedException ex){
                    Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
    
    private void hideShowAllModules(){
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
            top.getChildren().clear();
            topRight.getChildren().clear();
            topLeft.getChildren().clear();
            bottomRight.getChildren().clear();
            bottomLeft.getChildren().clear();
            bottom.getChildren().clear();
            modulesOnMirror.clear();
        });
    }

    @FXML
    private Pane spotifyPlayer;
    
    void placeModules(){
        Platform.runLater(() -> {
            if (Config.getTopMod() != null){
                top.getChildren().addAll(Config.getTopMod());
                modulesOnMirror.add(Config.getTopMod());
            }
            if (Config.getTopRightMod() != null) {
                topRight.getChildren().setAll(Config.getTopRightMod());
                modulesOnMirror.add(Config.getTopRightMod());
            }
            if (Config.getTopLeftMod() != null) {
                topLeft.getChildren().setAll(Config.getTopLeftMod());
                modulesOnMirror.add(Config.getTopLeftMod());
            }
            if (Config.getBottomRightMod() != null) {
                bottomRight.getChildren().setAll(Config.getBottomRightMod());
                modulesOnMirror.add(Config.getBottomRightMod());
            }
            if (Config.getBottomLeftMod() != null) {
                bottomLeft.getChildren().setAll(Config.getBottomLeftMod());
                modulesOnMirror.add(Config.getBottomLeftMod());
            }
            if (Config.getBottomMod() != null) {
                bottom.getChildren().setAll(Config.getBottomMod());
                modulesOnMirror.add(Config.getBottomMod());
            }
            // TEMPORARY
//            Module spotify = new Module("/fxml/Spotify.fxml");
//            bottomRight.getChildren().addAll(spotify);
//            modulesOnMirror.add(spotify);
        });
    }
        
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        alertPrompt.setOpacity(0);
        alertProcessing();
        buttonFadeInOut = new FadeTransition(Duration.millis(1000), hideButton);
        placeModules();
        String webAddress = System.getenv("WEBADDRESS");
        if (webAddress == null || webAddress.isEmpty()){
            webAddress = "Web service address not found";
        }
        webServiceAddr.setText(webAddress);
    }
}
