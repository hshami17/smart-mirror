package api_calls;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
import models.ModelManager;
import models.WunderlistModel;
import models.datatypes.Task;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import smartmirror.MirrorViewController;
import utils.Config;
import utils.PCM;
import utils.PCS;

/**
 *
 * @author saadshami
 */
public class WunderlistAPI extends Thread implements PropertyChangeListener{
    
    private final WunderlistModel tasksModel;
    private boolean stop = true;
    private final long sleepTime = 2000;
    
    public WunderlistAPI(){
        this.tasksModel = ModelManager.INST.getTasksModel();
        PCS.INST.addPropertyChangeListener(PCM.STOP_TASKS_API, this);
        PCS.INST.addPropertyChangeListener(PCM.PULL_TASKS, this);
    }
    
    @Override
    public void run(){
        stop = false;
        while(!stop){
            try {
                getTasks();
                Thread.sleep(sleepTime);
            } 
            catch (InterruptedException ex) {
                break;
            }
        }
    }
    
    // Get all tasks from Wunderlist
    synchronized public void getTasks(){
        try {
            URLConnection connection = new URL("https://a.wunderlist.com/api/v1/tasks?list_id=" + Config.getListID()).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("X-Access-Token", Config.getTaskKey());
            connection.setRequestProperty("X-Client-ID", Config.getTaskClientID());
            connection.setRequestProperty("Accept", "application/json");

            InputStream response = connection.getInputStream(); 
            
            JsonReader rdr = Json.createReader(response);   
            JsonArray obj = rdr.readArray();
            
            JsonObject task;
            Calendar today = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0); 
            tomorrow.set(Calendar.HOUR_OF_DAY, 0); 
            tomorrow.add(Calendar.DATE, 1);
  
            Calendar due_date = Calendar.getInstance();
            
            tasksModel.clearTaskLists();
            for (int i=0; i<obj.size(); i++){
                task = obj.getJsonObject(i);
                if (task.containsKey("due_date")){
                    // Check if the due date is today or tomorrow and add to appropriate list
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                    DateTime dt = formatter.parseDateTime(task.getString("due_date"));
                    due_date.setTime(dt.toDate());
                    
                    boolean sameDay = today.get(Calendar.YEAR) == due_date.get(Calendar.YEAR) &&
                                      today.get(Calendar.DAY_OF_YEAR) == due_date.get(Calendar.DAY_OF_YEAR);
                    
                    boolean nextDay = tomorrow.get(Calendar.YEAR) == due_date.get(Calendar.YEAR) &&
                                      tomorrow.get(Calendar.DAY_OF_YEAR) == due_date.get(Calendar.DAY_OF_YEAR);
                    
                    if (sameDay){
                        tasksModel.getTodayTaskList().add(new Task(task.getJsonNumber("id").longValueExact(), task.getString("title"), task.getBoolean("completed")));
                    } else if (nextDay) {
                        tasksModel.getTomorrowTaskList().add(new Task(task.getJsonNumber("id").longValueExact(), task.getString("title"), task.getBoolean("completed")));
                    }
                    
                }
            }
            
            PCS.INST.firePropertyChange(PCM.TASK_UPDATE);
            
        } catch (IOException ex) {
            try {
                MirrorViewController.alerts.put("THERE WAS AN ISSUE PULLING FROM THE WUNDERLIST API");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case PCM.STOP_TASKS_API:
                stop = true;
                this.interrupt();
                break;

        }
    }
}
