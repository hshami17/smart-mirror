package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
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
    
    public static void putWunderlistHeader(URLConnection connection) {
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("X-Access-Token", wunderlistKey.get());
        connection.setRequestProperty("X-Client-ID", wunderlistClientID.get());
        connection.setRequestProperty("Accept", "application/json");
    }

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

        JsonObject task;
        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0); 
        tomorrow.set(Calendar.HOUR_OF_DAY, 0); 
        tomorrow.add(Calendar.DATE, 1);

        Calendar due_date = Calendar.getInstance();

        tasksModel.clearTaskLists();
        for (int i=0; i < jsonArray.size(); i++){
            task = jsonArray.getJsonObject(i);
            tasksModel.getTaskList().add(new Task(task.getJsonNumber("id").longValueExact(), task.getString("title"), task.getBoolean("completed")));
//            if (task.containsKey("due_date")){
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

//            }
        }
        
        System.out.println(tasksModel.getListName() + ":");
        System.out.println(tasksModel.toString());
    }
}
