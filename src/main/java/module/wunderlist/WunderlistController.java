/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.wunderlist;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import models.ModelManager;
import models.WunderlistModel;
import models.datatypes.Task;
import module.ModuleController;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class WunderlistController implements Initializable, ModuleController {
    
    @FXML
    private Label listTitle;
    @FXML
    private VBox taskList;
    @FXML
    private Pane container;

    private WunderlistModel wunderlistModel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    

    @Override
    public void setModel(ModelManager modelManager) {
        this.wunderlistModel = modelManager.getTasksModel();
    }
    
    @Override
    public void update() {
        Platform.runLater(() -> {
            taskList.getChildren().clear();
            listTitle.setText(wunderlistModel.getListName().toUpperCase());

            if (wunderlistModel.getTaskList().isEmpty()){
                Label message = new Label("Nothing to do ☺");
                message.getStyleClass().add("task-title");
                taskList.getChildren().addAll(message);
            }
            else {
                // Get tasks with due dates
                wunderlistModel.getTaskList().filtered((task) -> !task.getDueDate().isEmpty()).forEach((task) -> {
                    addTask(task);
                });

                // Get tasks without due dates
                wunderlistModel.getTaskList().filtered((task) -> task.getDueDate().isEmpty()).forEach((task) -> {
                    addTask(task);
                });

            }
        });
    }
    
    private void addTask(Task task) {
        ImageView bullet = new ImageView(new Image("/images/bullet.png"));
        bullet.setFitHeight(8);
        bullet.setFitWidth(8);
        Label title = new Label(task.getTask(), bullet);
        title.setGraphicTextGap(10);
        title.getStyleClass().add("task-title");

        taskList.getChildren().add(title);
        if (!task.getDueDate().isEmpty()) {
            Label dueDate = new Label(task.getDueDate());
            dueDate.getStyleClass().add("task-time");
            dueDate.setPadding(new Insets(0, 0, 8, 20));
            taskList.getChildren().add(dueDate);
        }
        else {
            title.setPadding(new Insets(0, 0, 8, 0));
        }
    }

    @Override
    public void align(boolean left){
        container.setNodeOrientation(left ? NodeOrientation.LEFT_TO_RIGHT : NodeOrientation.RIGHT_TO_LEFT);
    }
}
