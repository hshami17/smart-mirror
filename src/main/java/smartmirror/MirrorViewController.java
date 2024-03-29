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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import module.MirrorModule;
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
    
    private final double MAX_TOP_CONTAINER_HEIGHT = 61.0;
    
    
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
    @FXML
    private Pane minimalCovid;

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
        
        webServiceAddr.setText(Config.WEB_ADDRESS);
        getQrCode();
        setupMinimalView();
        
        // Make sure top container does not overlap modules below it
        top.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.doubleValue() > MAX_TOP_CONTAINER_HEIGHT) {
                    Module module = Config.getModuleAt(Position.TOP);
                    if (module != null) {
                        module.getApi().pullApi();
                    }
                }
            }
        });
    }
    
    private void getQrCode() {
        String webAddress = Config.WEB_ADDRESS;
        if (webAddress != null && !webAddress.isEmpty()) {
            InputStream qrCodeIn = null;
            try {
                URL qrCodeGenUrl = new URL("http://" + webAddress + "/api/genqrcode");
                qrCodeIn = qrCodeGenUrl.openStream();
                byte[] imgUrlBytes = new byte[64];
                qrCodeIn.read(imgUrlBytes);
                qrCodeIn.close();
                String qrCodeImgUrl = new String(imgUrlBytes);
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
            finally {
                if (qrCodeIn != null) {
                    try {
                        qrCodeIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private void setupMinimalView() {
        minimalWeather.getChildren().add(Config.getModule(MirrorModule.DARKSKY_MINIMAL));
        minimalClock.getChildren().add(Config.getModule(MirrorModule.CLOCK_MINIMAL));
        minimalSpotify.getChildren().add(Config.getModule(MirrorModule.SPOTIFY_MINIMAL));
//        minimalCovid.getChildren().add(Config.getModule(ModuleName.COVID_MINIMAL));
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
//                        displayAlert(message);
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
            
            // Force COVID module to botttom left
//            Pane bottomLeft = spaces.get(Position.BOTTOMLEFT);
//            bottomLeft.getChildren().clear();
//            Module covid = Config.getModule(ModuleName.COVID);
//            covid.setPosition(Position.BOTTOMLEFT);
//            bottomLeft.getChildren().add(covid);
            
        });
    }
    
    public AnchorPane getMirrorPane() {
        return mirrorViewPane;
    }
    
    public AnchorPane getMinimalPane() {
        return minimalViewPane;
    }
}
