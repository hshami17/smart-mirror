package module;

import api_calls.APIManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private Position position = Position.NONE;
    
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
            if (hasModuleController()) moduleController().setModel(ModelManager.INST);
            addListeners();
        } 
        catch (IOException ex) {
            Logger.getLogger(Module.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addListeners(){
        onMirror.addListener((observable,  oldValue,  newValue) -> {
            if (newValue){
                if (hasModuleController()) moduleController().displayingModule();
                if (hasApi()) api.start();
            }
            else{
                if (hasModuleController()) moduleController().removingModule();
                if (hasApi()) api.stop();
            }
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
    
    public ModuleController moduleController() {
        return hasModuleController() ? (ModuleController) controller : null;
    }
    
    public void update() {
        moduleController().update();
    }
    
    public ModuleName getName(){
        return name;
    }
    
    public boolean isApiRunning() {
        return (hasApi() && api.isRunning());
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
            moduleController().align(position.toString().contains("LEFT"));
        }
    }
    
    public Position getPosition() {
        return position;
    }
}
