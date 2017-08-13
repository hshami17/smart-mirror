/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.tasks;

import api_calls.WunderlistAPI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.ModelManager;
import models.WunderlistModel;
import models.datatypes.Task;
import utils.PCM;
import utils.PCS;
import module.ModuleControl;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class WunderlistController implements Initializable, ModuleControl, 
        PropertyChangeListener {
    
    @FXML
    private Label todayTitle;
    @FXML
    private Label tomorrowTitle;
    @FXML
    private VBox container;
    @FXML
    private VBox todayTasks;
    @FXML
    private VBox tomorrowTasks;
    
    private WunderlistModel wunderlistModel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PCS.INST.addPropertyChangeListener(PCM.TASK_UPDATE, this);
    }    

    @Override
    public void setModel(ModelManager modelManager) {
        this.wunderlistModel = modelManager.getTasksModel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            todayTasks.getChildren().clear();
            tomorrowTasks.getChildren().clear();
            
            if (wunderlistModel.getTodayTaskList().isEmpty()){
                Label message = new Label("Nothing for today ☺");
                message.getStyleClass().add("task-title");
                todayTasks.getChildren().addAll(message);
            }
            
            if (wunderlistModel.getTomorrowTaskList().isEmpty()){
                Label message = new Label("Nothing for tomorrow ☺");
                message.getStyleClass().add("task-title");
                tomorrowTasks.getChildren().addAll(message);
            }

            // Get todays task
            for (Task task : wunderlistModel.getTodayTaskList()){
                Label title = new Label(task.getTask());
                Label time = new Label(task.getTime());

                title.getStyleClass().add("task-title");
                time.getStyleClass().add("task-time");

                todayTasks.getChildren().addAll(title, time);
            }

            // Get tomorrows task
            for (Task task : wunderlistModel.getTomorrowTaskList()){
                Label title = new Label(task.getTask());
                Label time = new Label(task.getTime());

                title.getStyleClass().add("task-title");
                time.getStyleClass().add("task-time");

                tomorrowTasks.getChildren().addAll(title, time);
            }
        });
    }

    @Override
    public void align(boolean left){
        container.setAlignment(left ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
        todayTasks.setAlignment(left ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
        tomorrowTasks.setAlignment(left ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
    }

    private boolean apiRunning = false;
    
    @Override
    public void startModule() {
        if (!apiRunning){
            new WunderlistAPI().start();
            apiRunning = true;
        }
    }

    @Override
    public void stopModule() {
        PCS.INST.firePropertyChange(PCM.STOP_TASKS_API);
        apiRunning = false;
    }
}
