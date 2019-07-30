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
import static utils.ConfigElements.darkskyKey;
import static utils.ConfigElements.zipcode;
import static utils.ConfigElements.zipcodeKey;

/**
 *
 * @author hasan
 */
public class DarkSkyAPI extends APIManager {
    
    private final DarkSkyModel darkSkyModel;
    
    public DarkSkyAPI() {
        super(600000);
        this.darkSkyModel = ModelManager.INST.getWeatherModel();
    }

    @Override
    synchronized protected void fetch() throws IOException {
        URL zip_api_url = new URL("http://www.zipcodeapi.com/rest/" + zipcodeKey.get() + "/"
                + "info.json/" + zipcode.get() + "/degrees");
        InputStream is_zip_api = zip_api_url.openStream();
        JsonReader rdr_zip_api = Json.createReader(is_zip_api);
        JsonObject obj_zip_api = rdr_zip_api.readObject();

        darkSkyModel.setLocation(obj_zip_api.getString("city"), obj_zip_api.getString("state"));


        String lat = obj_zip_api.getJsonNumber("lat").toString();
        String lon = obj_zip_api.getJsonNumber("lng").toString();

        URL url = new URL("https://api.darksky.net/forecast/" + darkskyKey.get() + "/" + lat + "," + lon);
        InputStream is = url.openStream();
        JsonReader rdr = Json.createReader(is);

        JsonObject obj = rdr.readObject();
        JsonObject current = obj.getJsonObject("currently");

        darkSkyModel.setCurrentTemp(current.getJsonNumber("temperature").intValue());
        darkSkyModel.setCurrentDay(Forecast.getDay(current.getInt("time")));
        darkSkyModel.setCurrentSummary(current.getString("summary"));
        switch (current.getString("icon")) {
            case "clear-day":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.CLEAR_DAY);
                break;
            case "clear-night":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.CLEAR_NIGHT);
                break;
            case "rain":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.RAIN);
                break;
            case "snow":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.SNOW);
                break;
            case "sleet":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.SLEET);
                break;
            case "wind":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.WIND);
                break;
            case "fog":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.FOG);
                break;
            case "cloudy":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.CLOUDY);
                break;
            case "partly-cloudy-day":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.PARTLY_CLOUDY_DAY);
                break;
            case "partly-cloudy-night":
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.PARTLY_CLOUDY_NIGHT);
                break;
            default:
                darkSkyModel.setCurrentIcon(DarkSkyModel.Icon.PARTLY_CLOUDY_DAY);
                break;
        }

        JsonObject week = obj.getJsonObject("daily");

        darkSkyModel.setWeeklySummary(week.getString("summary"));

        JsonArray weeklyForecast = week.getJsonArray("data");

        darkSkyModel.getForecastList().clear();
        for (int i = 0; i < weeklyForecast.size(); i++) {
            darkSkyModel.getForecastList().add(new Forecast(weeklyForecast.getJsonObject(i)));
        }
    }
}
