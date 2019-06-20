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
import utils.Config;
import utils.PCM;

/**
 *
 * @author saadshami
 */
public class WunderlistAPI extends APIManager {
    
    private final WunderlistModel tasksModel;
    
    public WunderlistAPI(){
        super(ModuleName.WUNDERLIST, 5, PCM.PULL_TASKS);
        this.tasksModel = ModelManager.INST.getTasksModel();
    }

    // Get all tasks from Wunderlist
    @Override
    synchronized protected void fetch() throws IOException {
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
    }
}
