/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.datatypes;

import java.text.SimpleDateFormat;
import javax.json.JsonObject;
import java.util.Date;
import javafx.scene.image.Image;
import models.DarkSkyModel.WeatherImage;

/**
 *
 * @author saadshami
 */
public class Forecast {

    private final String day;
    private final Image icon;
    private final Integer high;
    private final Integer low;
        
    public Forecast(JsonObject weekData) {
        // Get daily forecast json object for specific day and set the variables       
        day = Forecast.getDay(weekData.getInt("time"));
        high = weekData.getJsonNumber("temperatureMax").intValue();
        low = weekData.getJsonNumber("temperatureMin").intValue();
        switch (weekData.getString("icon")){
            case "clear-day":
                icon = WeatherImage.CLEAR_DAY;
                break;
            case "clear-night":
                icon = WeatherImage.CLEAR_NIGHT;
                break;
            case "rain":
                icon = WeatherImage.RAIN;
                break;
            case "snow":
                icon = WeatherImage.SNOW;
                break;
            case "sleet":
                icon = WeatherImage.SLEET;
                break;
            case "wind":
                icon = WeatherImage.WIND;
                break;
            case "fog":
                icon = WeatherImage.FOG;
                break;
            case "cloudy":
                icon = WeatherImage.CLOUDY;
                break;
            case "partly-cloudy-day":
                icon = WeatherImage.PARTLY_CLOUDY_DAY;
                break;
            case "partly-cloudy-night":
                icon = WeatherImage.PARTLY_CLOUDY_NIGHT;
                break;
            default:
                icon = null;
                break;   
        }
    }
    
    public static String getDay(int date){
        Date dateObj = new java.util.Date((long)date*1000);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE");
        String dateDay = formatter.format(Date.parse(dateObj.toString()));
        return dateDay;
    }
    
    public String getDay() {
        return day;
    }

    public Image getIcon() {
        return icon;
    }

    public Integer getHigh() {
        return high;
    }

    public Integer getLow() {
        return low;
    }
}
