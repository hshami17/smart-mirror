package api_calls;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.DarkSkyModel;
import javax.json.*;
import models.ModelManager;
import models.datatypes.Forecast;
import utils.Config;
import utils.PCM;
import utils.PCS;


/**
 *
 * @author hasan
 */
public class DarkSkyAPI extends Thread implements PropertyChangeListener {
    
    private final DarkSkyModel weatherModel;
    private boolean stop = true;
    private final long sleepTime = 600000;
    
    public DarkSkyAPI() {
        this.weatherModel = ModelManager.INST.getWeatherModel();
        PCS.INST.addPropertyChangeListener(PCM.STOP_WEATHER_API, this);
        PCS.INST.addPropertyChangeListener(PCM.PULL_WEATHER, this);
    }
    
    @Override
    public void run(){
        stop = false;
        while(!stop){
            try {
                getWeather();
                Thread.sleep(sleepTime);
            } 
            catch (InterruptedException ex) {
                break;
            }
        }
    }
    
    public void getWeather(){
        try {
            URL zip_api_url = new URL("http://www.zipcodeapi.com/rest/" + Config.getZipcodeKey() + "/"
                    + "info.json/" + Config.getZipCode() + "/degrees");
            InputStream is_zip_api = zip_api_url.openStream();
            JsonReader rdr_zip_api = Json.createReader(is_zip_api);   
            JsonObject obj_zip_api = rdr_zip_api.readObject();
            
            weatherModel.setLocation(obj_zip_api.getString("city"), obj_zip_api.getString("state"));
            
            
            String lat = obj_zip_api.getJsonNumber("lat").toString();
            String lon = obj_zip_api.getJsonNumber("lng").toString();
            
            URL url = new URL("https://api.darksky.net/forecast/" + Config.getWeatherKey() + "/" + lat + "," + lon);
            InputStream is = url.openStream();
            JsonReader rdr = Json.createReader(is);

            JsonObject obj = rdr.readObject();
            JsonObject current = obj.getJsonObject("currently");

            weatherModel.setCurrentTemp(current.getJsonNumber("temperature").intValue());
            weatherModel.setCurrentDay(Forecast.getDay(current.getInt("time")));
            weatherModel.setCurrentSummary(current.getString("summary"));
            switch (current.getString("icon")){
                case "clear-day":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.CLEAR_DAY);
                    break;
                case "clear-night":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.CLEAR_NIGHT);
                    break;
                case "rain":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.RAIN);
                    break;
                case "snow":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.SNOW);
                    break;
                case "sleet":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.SLEET);
                    break;
                case "wind":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.WIND);
                    break;
                case "fog":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.FOG);
                    break;
                case "cloudy":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.CLOUDY);
                    break;
                case "partly-cloudy-day":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.PARTLY_CLOUDY_DAY);
                    break;
                case "partly-cloudy-night":
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.PARTLY_CLOUDY_NIGHT);
                    break;
                default:
                    weatherModel.setCurrentIcon(DarkSkyModel.Icon.PARTLY_CLOUDY_DAY);
                    break; 
            }

            JsonObject week = obj.getJsonObject("daily");
            
            weatherModel.setWeeklySummary(week.getString("summary"));
            
            JsonArray weeklyForecast = week.getJsonArray("data");
            
            weatherModel.getForecastList().clear();
            for (int i=1; i<weeklyForecast.size(); i++){
                weatherModel.getForecastList().add(new Forecast(weeklyForecast.getJsonObject(i)));
            }
            
            PCS.INST.firePropertyChange(PCM.WEATHER_UPDATE);
        } 
        catch (IOException ex) {
            PCS.INST.firePropertyChange(PCM.ALERT, "THERE WAS AN ISSUE PULLING FROM THE DARK SKY API");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case PCM.STOP_WEATHER_API:
                stop = true;
                this.interrupt();
                break;
            case PCM.PULL_WEATHER:
                if (!stop){
                    getWeather();
                }
                break;
        }
    }
}
