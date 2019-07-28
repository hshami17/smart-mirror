package models;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.datatypes.Task;

/**
 *
 * @author saadshami
 */
public class WunderlistModel {
    
    private String listName;
    private final ObservableList<Task> taskList;
    
    public WunderlistModel(){
        taskList = FXCollections.observableArrayList();
    }
    
    public void setListName(String listName) {
        this.listName = listName;
    }
    
    public String getListName() {
        return listName;
    }
    
    public ObservableList<Task> getTaskList() {
        return taskList;
    }
    
    public void clearTaskLists(){
        taskList.clear();
    }
    
    @Override
    public String toString(){
        StringBuilder tasks = new StringBuilder();
        taskList.forEach((task) -> {
            System.out.println(task.toString());
        });

        return tasks.toString();
    }
}
