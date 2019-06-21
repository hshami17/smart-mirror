package module;

import api_calls.APIManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import models.ModelManager;


/**
 *
 * @author hasan
 * @param <T>
 */
public class Module<T> extends Region {

    private T controller;
    private String fxml;
    private APIManager api;
    private ModuleName name;
    private final BooleanProperty onMirror = new SimpleBooleanProperty(false);
    private final StringProperty position = new SimpleStringProperty("");
    
    public Module(String fxml, APIManager api, ModuleName name) {
        this.fxml = fxml;
        this.api = api;
        this.name = name;
        init();
    }
    
    public Module(String path, ModuleName name){
        this(path, null, name);
    }
    
    private void init() {
        try {
            if (api != null) {
                api.setModule(this);
            }
            FXMLLoader loader = new FXMLLoader(Module.class.getResource(fxml));
            this.getChildren().setAll((Node) loader.load());
            controller = loader.getController();
            if (controller instanceof ModuleController){
                ((ModuleController) controller).setModel(ModelManager.INST);
                addListener();
            }
        } 
        catch (IOException ex) {
            Logger.getLogger(Module.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addListener(){
        onMirror.addListener((observable,  oldValue,  newValue) -> {
            if (newValue){
                ((ModuleController) controller).displayingModule();
                if (api != null) api.start();
            }
            else{
                ((ModuleController) controller).removingModule();
                if (api != null) api.stop();
            }
        });

        position.addListener((observable, oldValue, newValue) -> {
            ((ModuleController) controller).align(newValue.contains("Left"));
        });
    }
    
    public void fadeOut(){
        FadeTransition fadeTransition = new FadeTransition(new Duration(500), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

    public void fadeIn(){
        FadeTransition fadeTransition = new FadeTransition(new Duration(1000), this);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    public T getController(){
        return controller;
    }
    
    public void update() {
        ((ModuleController) controller).update();
    }
    
    public ModuleName getName(){
        return name;
    }

    public BooleanProperty onMirrorProperty(){
        return onMirror;
    }
    
    public boolean isOnMirror(){
        return onMirror.getValue();
    }
    
    public void setOnMirror(boolean onMirror){
        this.onMirror.setValue(onMirror);
    }

    public void setPosition(String position) {
        this.position.setValue(position);
    }
}
