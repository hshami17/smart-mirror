package smartmirror;

import api_calls.HueMotionSensorAPI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
    private final AtomicBoolean minimalModeActive = new AtomicBoolean(false);
    private MirrorViewController mirrorViewController;
    private Scene mirrorView;
  
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
        // Get mirror settings from XML
        Config.configureMirror();
        
        // Load mirror view
        FXMLLoader loader = new FXMLLoader(SmartMirror.class.getResource("/fxml/MirrorView.fxml"));
        Parent mirrorRoot = loader.load();
        mirrorViewController = loader.getController();
        mirrorView = new Scene(mirrorRoot);
        
        mirrorViewController.getMirrorPane().setOpacity(1.0);
        mirrorViewController.getMinimalPane().setOpacity(0.0);
        mirrorViewController.getMirrorPane().setVisible(true);
        mirrorViewController.getMinimalPane().setVisible(true);

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
    
    synchronized private void goToMinimalMode(boolean minimalMode){
        // Ignore if we are in minimal mode already
        if (minimalMode == minimalModeActive.get()) {
            return;
        }
        
        Duration duration = new Duration(300);
        Pane fadingOutPane = minimalMode ? mirrorViewController.getMirrorPane() : mirrorViewController.getMinimalPane();
        Pane fadingInPane = minimalMode ? mirrorViewController.getMinimalPane() : mirrorViewController.getMirrorPane();
        
        FadeTransition viewFadeOut = new FadeTransition(duration, fadingOutPane);
        viewFadeOut.setFromValue(fadingOutPane.getOpacity());
        viewFadeOut.setToValue(0.0);  
        
        FadeTransition viewFadeIn = new FadeTransition(duration, fadingInPane);
        viewFadeIn.setFromValue(fadingInPane.getOpacity());
        viewFadeIn.setToValue(1.0);
        
        viewFadeOut.setOnFinished(e -> viewFadeIn.play());        
        viewFadeOut.play();
        
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
