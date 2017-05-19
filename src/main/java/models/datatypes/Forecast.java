/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.datatypes;

import java.text.SimpleDateFormat;
import javax.json.JsonObject;
import java.util.Date;
import models.DarkSkyModel.Icon;

/**
 *
 * @author saadshami
 */
public class Forecast {

    private final String day;
    private final Icon icon;
    private final Integer high;
    private final Integer low;
        
    public Forecast(JsonObject weekData) {
        // Get daily forecast json object for specific day and set the variables       
        day = Forecast.getDay(weekData.getInt("time"));
        high = weekData.getJsonNumber("temperatureMax").intValue();
        low = weekData.getJsonNumber("temperatureMin").intValue();
        switch (weekData.getString("icon")){
            case "clear-day":
                icon = Icon.CLEAR_DAY;
                break;
            case "clear-night":
                icon = Icon.CLEAR_NIGHT;
                break;
            case "rain":
                icon = Icon.RAIN;
                break;
            case "snow":
                icon = Icon.SNOW;
                break;
            case "sleet":
                icon = Icon.SLEET;
                break;
            case "wind":
                icon = Icon.WIND;
                break;
            case "fog":
                icon = Icon.FOG;
                break;
            case "cloudy":
                icon = Icon.CLOUDY;
                break;
            case "partly-cloudy-day":
                icon = Icon.PARTLY_CLOUDY_DAY;
                break;
            case "partly-cloudy-night":
                icon = Icon.PARTLY_CLOUDY_NIGHT;
                break;
            default:
                icon = Icon.DEFAULT;
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

    public Icon getIcon() {
        return icon;
    }

    public Integer getHigh() {
        return high;
    }

    public Integer getLow() {
        return low;
    }
}
