package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
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

/**
 *
 * @author saadshami
 */
public class WunderlistAPI extends API {

    public WunderlistAPI(){
        super(5000);
    }
    
    private void putWunderlistHeader(URLConnection connection) {
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("X-Access-Token", wunderlistKey.get());
        connection.setRequestProperty("X-Client-ID", wunderlistClientID.get());
        connection.setRequestProperty("Accept", "application/json");
    }

    // Get all tasks from Wunderlist
    @Override
    synchronized protected void fetch() throws IOException {
        WunderlistModel tasksModel = ModelManager.INST.getTasksModel();
        // Get the list name
        URLConnection connection = new URL("https://a.wunderlist.com/api/v1/lists/" + listID.get()).openConnection();
        putWunderlistHeader(connection);
        
        InputStream listIdResponse = connection.getInputStream();
        JsonReader rdr = Json.createReader(listIdResponse);
        JsonObject obj = rdr.readObject();
        rdr.close();
        tasksModel.setListName(obj.getJsonString("title").getString());
        
        // Get the reminders from this list
        connection = new URL("https://a.wunderlist.com/api/v1/tasks?list_id=" + listID.get()).openConnection();
        putWunderlistHeader(connection);

        InputStream response = connection.getInputStream(); 
       
        rdr = Json.createReader(response);   
        JsonArray jsonArray = rdr.readArray();
        rdr.close();

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
