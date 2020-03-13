package smartmirror;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import module.Module;
import module.ModuleName;
import utils.Config;
import utils.Position;

/**
 * Mirror View controller class
 *
 * @author hasan
 */
public class MirrorViewController implements Initializable {
    
    private static final BlockingQueue<String> alerts = new LinkedBlockingQueue<>();
    private String lastAlertMsg = "";
    private long lastAlertTimestamp = 0;
    private final List<Module> modulesOnMirror = new ArrayList<>();
    private final Map<Position, Pane> spaces = new HashMap<>();
    
    
    @FXML
    private AnchorPane mirrorViewPane;
    @FXML
    private AnchorPane minimalViewPane;
    
    // Mirror view module containers
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
    private Label webServiceAddr;
    @FXML
    private ImageView imgQrCode;
    
    // Minimal view module containers
    @FXML
    private Pane minimalWeather;
    @FXML
    private Pane minimalClock;
    @FXML
    private Pane minimalSpotify;

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
        placeModules();
        String webAddress = Config.WEB_ADDRESS;
        if (webAddress == null || webAddress.isEmpty()){
            webAddress = "Web service address not found";
        }
        webServiceAddr.setText(webAddress);
        setupQrCode();
        setupMinimalView();
    }
    
    private void setupQrCode() {
        String webAddress = Config.WEB_ADDRESS;
        if (webAddress != null && !webAddress.isEmpty()) {
            try {
                URL qrCodeUrl = new URL("http://" + webAddress + "/api/genqrcode");
                InputStream qrCodeIn = qrCodeUrl.openStream();
                byte[] stringBuff = new byte[255];
                qrCodeIn.read(stringBuff);
                String qrCodeImgUrl = new String(stringBuff);
                Platform.runLater(() -> {
                    imgQrCode.setImage(new Image(qrCodeImgUrl));
                    imgQrCode.setOpacity(0.30);
                });
            } 
            catch (MalformedURLException ex) {
                Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (IOException ex) {
                Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void setupMinimalView() {
        minimalWeather.getChildren().add(Config.getModule(ModuleName.DARKSKY_MINIMAL));
        minimalClock.getChildren().add(Config.getModule(ModuleName.CLOCK_MINIMAL));
        minimalSpotify.getChildren().add(Config.getModule(ModuleName.SPOTIFY_MINIMAL));
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
                Thread.sleep(5000);
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
        
    public void clearAllContainers(){
        Platform.runLater(() -> {
            spaces.forEach((k, space) -> {
               space.getChildren().clear();
            });
            modulesOnMirror.clear();
        });
    }
    
    public void placeModules(){
        Platform.runLater(() -> {
            for (Position position : Position.values()) {
                Module module = Config.getModuleAt(position);
                if (module != null && module.getPosition() != Position.NONE) {
                    Pane space = spaces.get(position);
                    space.getChildren().setAll(module);
                    modulesOnMirror.add(module);
                }
                else {
                    modulesOnMirror.remove(module);
                }
            }
        });
    }
    
    public AnchorPane getMirrorPane() {
        return mirrorViewPane;
    }
    
    public AnchorPane getMinimalPane() {
        return minimalViewPane;
    }
}
