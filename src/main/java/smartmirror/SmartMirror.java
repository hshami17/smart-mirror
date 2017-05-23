package smartmirror;

import api_calls.RandomFamousQuoteAPI;
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
    
    public static void main(String[] args) {
        for (String arg : args) {
            switch (arg){
                case "-jarRun":
                    Config.jarRun = true;
                    break;
                case "-fullscreen":
                    Config.fullscreen = true;
                    break;
                default:
                    System.out.println("UNKNOWN ARG: " + arg);
            }
        }
        System.out.println("JAR RUN: " + Config.jarRun);
        System.out.println("FULLSCREEN: " + Config.fullscreen);
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
        // (ONLY WHEN RUNNING FROM JAR)
        PCS.INST.addPropertyChangeListener(PCM.NEW_CONFIG, this);
        new Watcher().start();
        
        // QUOTE NOT MODULIZED YET 
        new RandomFamousQuoteAPI().start();
        
        mirrorStage.setScene(new Scene(root));
        mirrorStage.setFullScreen(Config.fullscreen);
        mirrorStage.setOnCloseRequest(event -> System.exit(0));
        mirrorStage.show();

        // Get mirror settings from XML
//        Config.configureMirror();
        // QUOTE NOT MODULIZED YET 
//        new RandomFamousQuoteAPI().start();
    }
    
    public void loadNewConfig(){
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
