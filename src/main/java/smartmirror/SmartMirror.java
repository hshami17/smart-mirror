package smartmirror;

import api_calls.HueMotionSensorAPI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Config;
import utils.PCM;
import utils.PCS;
import utils.Watcher;

/**
 * 
 * @author hasan
 */
public class SmartMirror extends Application implements PropertyChangeListener {
    
    private static boolean fullscreen = false;
    
    private Stage mirrorStage;
    private MirrorViewController mirrorViewController;
    private MinimalViewController minimalViewController;
    private Scene mirrorView;
    private Scene minimalView;
    
    public static void main(String[] args) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        String configPath = System.getenv("CONFIGPATH");
        String watchPath = System.getenv("WATCHPATH");
        Config.CONFIG_PATH = configPath != null ? configPath : "src/main/resources/mirror_config.xml";
        Config.WATCH_PATH = watchPath != null ? watchPath : "src/main/resources";

        for (String arg : args) {
            switch (arg) {
                case "-fullscreen":
                    fullscreen = true;
                    break;
                default:
                    System.out.println("UNKNOWN ARG: " + arg);
                    break;
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage mirrorStage) throws Exception {
        this.mirrorStage = mirrorStage;
        // Get mirror settings from XML
        Config.configureMirror();
        
        FXMLLoader loader = new FXMLLoader(SmartMirror.class.getResource("/fxml/MirrorView.fxml"));
        Parent mirrorRoot = loader.load();
        mirrorViewController = loader.getController();
        
        loader = new FXMLLoader(SmartMirror.class.getResource("/fxml/MinimalView.fxml"));
        Parent minimalRoot = loader.load();
        minimalViewController = loader.getController();
        
        mirrorView = new Scene(mirrorRoot);
        minimalView = new Scene(minimalRoot);

        // Watcher to watch for config file changes
        PCS.INST.addPropertyChangeListener(PCM.NEW_CONFIG, this);
        new Watcher().start();
        
        // TODO: For testing only
        mirrorStage.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            boolean show = false;
            @Override
            public void handle(MouseEvent event) {
                show = !show;
                goToMinimalMode(show);
            }
        });
        
        mirrorStage.setScene(mirrorView);
        mirrorStage.setFullScreen(fullscreen);
        mirrorStage.setOnCloseRequest(e -> System.exit(0));
        mirrorStage.show();
        
        PCS.INST.addPropertyChangeListener(PCM.MINIMAL_MODE, this);
        new HueMotionSensorAPI().start();
    }
    
    AtomicBoolean doingTransition = new AtomicBoolean(false);
    AtomicBoolean minimalModeActive = new AtomicBoolean(false);
    synchronized private void goToMinimalMode(boolean minimalMode){
        // Ignore if there is a current transition or we are in minimal mode already
        if (doingTransition.get() || minimalMode == minimalModeActive.get()) {
            return;
        }
        
        Duration duration = new Duration(800);
        FadeTransition fadeTransition = new FadeTransition(duration, (minimalMode ? mirrorView.getRoot() : minimalView.getRoot()));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();

        doingTransition.set(true);
        
        fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread(() -> {
                    // Go to minimal mode or back to mirror mode after transition finished
                    Platform.runLater(() -> {
                        Scene currentScene = minimalMode ? minimalView : mirrorView;
                        currentScene.getRoot().setOpacity(1.0);
                        mirrorStage.setScene(currentScene);
                    });
                    try {
                        Thread.sleep(1000);
                        doingTransition.set(false);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
            }
        });
        
        minimalModeActive.set(minimalMode);
    }
    
    private void loadNewConfig(){
        mirrorViewController.clearAllContainers();
        Config.configureMirror();
        mirrorViewController.placeModules();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase(PCM.NEW_CONFIG)){
            loadNewConfig();
        }
        else if (evt.getPropertyName().equals(PCM.MINIMAL_MODE)) {
            boolean minimalMode = (boolean)evt.getNewValue();
            Platform.runLater(() -> goToMinimalMode(minimalMode));
        }
    }
}
