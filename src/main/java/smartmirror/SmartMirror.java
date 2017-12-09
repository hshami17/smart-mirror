package smartmirror;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Config;
import utils.PCM;
import utils.PCS;
import utils.Watcher;

/**
 * 
 * @author hasan
 */
public class SmartMirror extends Application implements PropertyChangeListener {
    
    private MirrorViewController controller;
    private static boolean fullscreen = false;
    
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
        
        FXMLLoader loader = new FXMLLoader(SmartMirror.class.getResource("/fxml/MirrorView.fxml"));
        Parent root = loader.load();
        
        // Get controller for mirror view fxml
        controller = loader.getController();

        // Watcher to watch for config file changes
        PCS.INST.addPropertyChangeListener(PCM.NEW_CONFIG, this);
        new Watcher().start();

        mirrorStage.setScene(new Scene(root));
        mirrorStage.setFullScreen(fullscreen);
        mirrorStage.setOnCloseRequest(event -> System.exit(0));
        mirrorStage.show();
    }
    
    private void loadNewConfig(){
        controller.clearAllContainers();
        Config.configureMirror();
        controller.placeModules();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase(PCM.NEW_CONFIG)){
            loadNewConfig();
        }
    }
}
