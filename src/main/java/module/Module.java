package module;

import api_calls.APIManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import utils.Position;


/**
 *
 * @author hasan
 * @param <T>
 */
public class Module<T> extends Region {

    private T controller;
    private final String fxml;
    private final APIManager api;
    private final ModuleName name;
    private final BooleanProperty onMirror = new SimpleBooleanProperty(false);
    private final StringProperty positionOld = new SimpleStringProperty("");
    
    private Position position = Position.NONE;
    private final Map<String, String> moduleConfigs = new HashMap<>();
    
    public Module(ModuleName name) {
        this.fxml = name.getFxml();
        this.api = name.getApi();
        this.name = name;
        init();
    }
    
    private void init() {
        try {
            if (hasApi()) {
                api.setModule(this);
            }
            FXMLLoader loader = new FXMLLoader(Module.class.getResource(fxml));
            this.getChildren().setAll((Node) loader.load());
            controller = loader.getController();
            if (hasModuleController()) ((ModuleController) controller).setModel(ModelManager.INST);
            addListeners();
        } 
        catch (IOException ex) {
            Logger.getLogger(Module.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addListeners(){
        onMirror.addListener((observable,  oldValue,  newValue) -> {
            if (newValue){
                if (hasModuleController()) ((ModuleController) controller).displayingModule();
                if (hasApi()) api.start();
                }
            else{
                if (hasModuleController()) ((ModuleController) controller).removingModule();
                if (hasApi()) api.stop();
            }
        });

        positionOld.addListener((observable, oldValue, newValue) -> {
            if (hasModuleController()) ((ModuleController) controller).align(newValue.contains("Left"));
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
    
    public boolean hasApi() {
        return api != null;
    }
    
    public boolean hasModuleController() {
        return controller instanceof ModuleController;
    }
    
    public void setOnMirror(boolean onMirror){
        this.onMirror.setValue(onMirror);
    }

    public void setPosition(Position position) {
        this.position = position;
        setOnMirror(position != Position.NONE);
        if (hasModuleController()) {
            ((ModuleController) controller).align(position.toString().contains("LEFT"));
        }
    }
    
    public Position getPosition() {
        return position;
    }
}
