package models;

import java.util.ArrayList;
import models.datatypes.Task;

/**
 *
 * @author saadshami
 */
public class WunderlistModel {
    
    private final ArrayList<Task> today_task_list;
    private final ArrayList<Task> tomorrow_task_list;
    
    public WunderlistModel(){
        today_task_list = new ArrayList<>();
        tomorrow_task_list = new ArrayList<>();
    }
    
    public ArrayList<Task> getTodayTaskList() {
        return today_task_list;
    }

    public ArrayList<Task> getTomorrowTaskList() {
        return tomorrow_task_list;
    }
    
    public void clearTaskLists(){
        today_task_list.clear();
        tomorrow_task_list.clear();
    }
    
    @Override
    public String toString(){
        String text = "---TODAY--- \n";
        for (Task task : today_task_list){
            text += task.toString() + "\n";
        }
        text += "---TOMORROW--- \n";
        for (Task task : tomorrow_task_list){
            text += task.toString() + "\n";
        }  
        return text;
    }
}
