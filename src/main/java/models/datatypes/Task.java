package models.datatypes;
import api_calls.RandomFamousQuoteAPI;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import utils.Config;


/**
 *
 * @author saadshami
 */
public class Task {
   
    private Long task_id;
    private String task;
    private String time;
    
    public Task(Long task_id, String task, boolean completed){
        this.task_id = task_id;
        this.task = task;
        pullTime();   
    }
   
    public void setId(Long id) {
        this.task_id = id;
    }
    
    public void setTask(String task) {
        this.task = task;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTask() {
        return task;
    }

    public String getTime() {
        return time;
    }
    
    // API call to pull time (time) for task
    public final void pullTime(){
        try{
            URLConnection connection = new URL("https://a.wunderlist.com/api/v1/reminders?task_id=" + this.task_id).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("X-Access-Token", Config.getTaskKey());
            connection.setRequestProperty("X-Client-ID", Config.getTaskClientID());
            connection.setRequestProperty("Accept", "application/json");

            InputStream response = connection.getInputStream(); 

            JsonReader rdr = Json.createReader(response);   
            JsonArray obj = rdr.readArray();

            if (obj.isEmpty()){
                setTime("");
            } else {
                JsonObject task_reminder;
                task_reminder = obj.getJsonObject(0);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC();
                DateTime dt = dtf.parseDateTime(task_reminder.getString("date"));
                Date date = dt.toDate();
                SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
                String time = formatter.format(Date.parse(date.toString()));
                this.time = time;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(RandomFamousQuoteAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @Override
    public String toString(){
        return "Time: " + time + "\n" + "Task: " + task;
    }
}
