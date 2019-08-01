package models;

import models.datatypes.Forecast;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

/**
 *
 * @author hasan
 */
public class DarkSkyModel {
    
    private String location;
    private Integer currentTemp;
    private Image currentIcon;
    private String currentSummary;
    private String weeklySummary;
    private String currentDay;
    private final List<Forecast> forecastList;
    
    public DarkSkyModel(){
        forecastList = new ArrayList<>();
    }
    
    public String getLocation() {
        return location;
    }
   
    public Integer getCurrentTemp() {
        return currentTemp;
    }

    public Image getCurrentIcon() {
        return currentIcon;
    }

    public String getCurrentSummary() {
        return currentSummary;
    }
    
    public String getWeeklySummary(){
        return weeklySummary;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public List<Forecast> getForecastList() {
        return forecastList;
    }
 
    public void setLocation(String city, String state) {
        this.location = city + ", " + state;
    }
    
    public void setCurrentTemp(Integer currentTemp) {
        this.currentTemp = currentTemp;
    }

    public void setCurrentIcon(Image currentIcon) {
        this.currentIcon = currentIcon;
    }

    public void setCurrentSummary(String currentSummary) {
        this.currentSummary = currentSummary;
    }
    
    public void setWeeklySummary(String weeklySummary){
        this.weeklySummary = weeklySummary;
    }

    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
    }
    
    public static class WeatherImage {
        public static Image CLEAR_DAY = new Image("/images/clear_day.png");
        public static Image CLEAR_NIGHT = new Image("/images/clear_night.png");
        public static Image RAIN = new Image("/images/rain.png");
        public static Image SNOW = new Image("/images/snow.png");
        public static Image SLEET = new Image("/images/sleet.png");
        public static Image WIND = new Image("/images/wind.png");
        public static Image FOG = new Image("/images/fog.png");
        public static Image CLOUDY = new Image("/images/cloud.png");
        public static Image PARTLY_CLOUDY_DAY = new Image("/images/partly_cloudy_day.png");
        public static Image PARTLY_CLOUDY_NIGHT = new Image("/images/partly_cloudy_night.png");
//        public static Image DEFAULT = new Image("/images/default.png");
    }
}
