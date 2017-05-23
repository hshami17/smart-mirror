package module;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import models.ModelManager;


/**
 *
 * @author hasan
 * @param <T>
 */
public class Module<T> extends Pane {
    
    public static final String WEATHER = "WEATHER";
    public static final String CLOCK = "CLOCK";
    public static final String NEWS = "NEWS";
    public static final String TASKS = "TASKS";
    public static final String QUOTE = "QUOTE";
    
    private T controller;
    private BooleanProperty onMirror = new SimpleBooleanProperty(false);
    private String type;
    private double[] dimensions = new double[2];
    
    public Module(String path, String type){
        try {
            this.type = type;
            setDimensions();
            FXMLLoader loader = new FXMLLoader(Module.class.getResource(path));
            this.getChildren().setAll((Node) loader.load());
            controller = loader.getController();
            if (controller instanceof ModuleControl){
                ((ModuleControl) controller).setModel(ModelManager.INST);
                addListener();
            }
        } catch (IOException ex) {
            Logger.getLogger(Module.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addListener(){
        onMirror.addListener((ObservableValue<? extends Boolean> observable, 
                Boolean oldValue, Boolean newValue) -> {
            if (!oldValue){
                ((ModuleControl) controller).startAPI();
            }
            else{
                ((ModuleControl) controller).stopAPI();
            }
        });
    }
    
    private void setDimensions(){
        switch(type){
            case WEATHER:
                dimensions[0] = 326d;
                dimensions[1] = 518d;
                break;
            case CLOCK:
                dimensions[0] = 273d;
                dimensions[1] = 518d;
                break;
            case TASKS:
                dimensions[0] = 515d;
                dimensions[1] = 110d;
                break;
            case NEWS:
                break;
        }
    }
    
    public T getController(){
        return controller;
    }
    
    public String getType(){
        return type;
    }
    
    public double[] getDimensions(){
        return dimensions;
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
}
