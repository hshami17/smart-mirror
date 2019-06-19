package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import models.DarkSkyModel;
import models.ModelManager;
import models.datatypes.Forecast;
import utils.Config;
import utils.PCM;


/**
 *
 * @author hasan
 */
public class DarkSkyAPI extends APIManager {
    
    private final DarkSkyModel weatherModel;
    
    public DarkSkyAPI() {
        super(ModuleName.DARK_SKY, 600000, PCM.PULL_WEATHER);
        this.weatherModel = ModelManager.INST.getWeatherModel();
    }

    @Override
    synchronized protected void fetch() throws IOException {
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
        switch (current.getString("icon")) {
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
        for (int i = 0; i < weeklyForecast.size(); i++) {
            weatherModel.getForecastList().add(new Forecast(weeklyForecast.getJsonObject(i)));
        }
    }
}
