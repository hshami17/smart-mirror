package models;

import java.util.ArrayList;
import models.datatypes.Task;

/**
 *
 * @author saadshami
 */
public class WunderlistModel {
    
    private String listName;
    private final ArrayList<Task> taskList;
    
    private final ArrayList<Task> today_task_list;
    private final ArrayList<Task> tomorrow_task_list;
    
    public WunderlistModel(){
        taskList = new ArrayList<>();
        today_task_list = new ArrayList<>();
        tomorrow_task_list = new ArrayList<>();
    }
    
    public void setListName(String listName) {
        this.listName = listName;
    }
    
    public String getListName() {
        return listName;
    }
    
    public ArrayList<Task> getTaskList() {
        return taskList;
    }
    
    public ArrayList<Task> getTodayTaskList() {
        return today_task_list;
    }

    public ArrayList<Task> getTomorrowTaskList() {
        return tomorrow_task_list;
    }
    
    public void clearTaskLists(){
        taskList.clear();
        today_task_list.clear();
        tomorrow_task_list.clear();
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
