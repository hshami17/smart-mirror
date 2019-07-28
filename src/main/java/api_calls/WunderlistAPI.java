package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
import models.ModelManager;
import models.WunderlistModel;
import models.datatypes.Task;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import static utils.ConfigElements.listID;
import static utils.ConfigElements.wunderlistClientID;
import static utils.ConfigElements.wunderlistKey;
import utils.PCM;

/**
 *
 * @author saadshami
 */
public class WunderlistAPI extends APIManager {
    
    private final WunderlistModel tasksModel;
    
    public WunderlistAPI(){
        super(5000, PCM.FETCH_WUNDERLIST);
        this.tasksModel = ModelManager.INST.getTasksModel();
    }
    
    private void putWunderlistHeader(URLConnection connection) {
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("X-Access-Token", wunderlistKey.get());
        connection.setRequestProperty("X-Client-ID", wunderlistClientID.get());
        connection.setRequestProperty("Accept", "application/json");
    }
    
    // API call to pull time (time) for task
//    private  void pullTime(Task task){
//        try{
//            URLConnection connection = new URL("https://a.wunderlist.com/api/v1/reminders?task_id=" + task.getId()).openConnection();
//            putWunderlistHeader(connection);
//
//            InputStream response = connection.getInputStream(); 
//
//            JsonReader rdr = Json.createReader(response);   
//            JsonArray obj = rdr.readArray();
//
//            if (obj.isEmpty()){
//                setTime("");
//            } 
//            else {
//                JsonObject task_reminder;
//                task_reminder = obj.getJsonObject(0);
//                
//                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC();
//                DateTime dt = dtf.parseDateTime(task_reminder.getString("date"));
//                Date date = dt.toDate();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
//                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
//                String dateStr = dateFormat.format(Date.parse(date.toString()));
//                String time = timeFormat.format(Date.parse(date.toString()));
//                this.time = dateStr + " @ " + time;
//            }
//            
//        } catch (IOException ex) {
//            Logger.getLogger(RandomFamousQuoteAPI.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    // Get all tasks from Wunderlist
    @Override
    synchronized protected void fetch() throws IOException {
        // Get the list name
        URLConnection connection = new URL("https://a.wunderlist.com/api/v1/lists/" + listID.get()).openConnection();
        putWunderlistHeader(connection);
        
        InputStream listIdResponse = connection.getInputStream();
        JsonReader rdr = Json.createReader(listIdResponse);
        JsonObject obj = rdr.readObject();
        tasksModel.setListName(obj.getJsonString("title").getString());
        
        // Get the reminders from this list
        connection = new URL("https://a.wunderlist.com/api/v1/tasks?list_id=" + listID.get()).openConnection();
        putWunderlistHeader(connection);

        InputStream response = connection.getInputStream(); 
       
        rdr = Json.createReader(response);   
        JsonArray jsonArray = rdr.readArray();

        JsonObject taskObj;

        tasksModel.clearTaskLists();
        for (int i=0; i < jsonArray.size(); i++){
            taskObj = jsonArray.getJsonObject(i);
            
            Task task = new Task(taskObj.getString("title"));
            if (taskObj.containsKey("due_date")){
                // Check if the due date is today or tomorrow and add to appropriate list
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                DateTime dt = formatter.parseDateTime(taskObj.getString("due_date"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
                String dueDate = dateFormat.format(dt.toDate());
                task.setDueDate(dueDate);
            }
            
            tasksModel.getTaskList().add(task);
        }
    }
}
