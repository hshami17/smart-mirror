/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import utils.PCM;
import utils.PCS;

/**
 *
 * @author hasan
 */
public class HueMotionSensorAPI extends APIManager {

    public HueMotionSensorAPI() {
        super(500);
    }
    
    boolean lastPresence = true;
    @Override
    protected void fetch() throws IOException {
        try {
            String webaddress = System.getenv("WEBADDRESS");
            if (webaddress != null && !webaddress.isEmpty()) {
                URL hueMotionSensorDataUrl = new URL("http://" + webaddress + "/api/motionsensor");
                InputStream inMotionSensor = hueMotionSensorDataUrl.openStream();
                JsonObject sensorData = Json.createReader(inMotionSensor).readObject();

                boolean on = sensorData.getJsonObject("config").getBoolean("on");
                if (on) {
                    boolean presence = sensorData.getJsonObject("state").getBoolean("presence");
                    boolean minimalMode = !presence;
                    System.out.println("Presence: " + presence + " MINIMAL MODE: " + minimalMode);
                    PCS.INST.firePropertyChange(PCM.MINIMAL_MODE, minimalMode);
//                    lastPresence = presence;
                }
                else if (!lastPresence){
                    PCS.INST.firePropertyChange(PCM.MINIMAL_MODE, false);
                    lastPresence = true;
                }
            }
        }
        catch (NullPointerException ex) {}
    }
}
