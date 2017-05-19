package models;

import models.datatypes.Forecast;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hasan
 */
public class DarkSkyModel {
    
    private String location;
    private Integer currentTemp;
    private Icon currentIcon;
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

    public Icon getCurrentIcon() {
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

    public void setCurrentIcon(Icon currentIcon) {
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
    
    @Override
    public String toString(){
        String test = "Location: " + location + "\n" + 
                "Current Temp: " + currentTemp + "\n" +
                "Current Icon: " + currentIcon + "\n" + 
                "Current Summary: " + currentSummary + "\n" + 
                "Current Day: " + currentDay + "\n\n";
        for (Forecast value : forecastList){
            test += "Day: " + value.getDay() + "\n";
            test += "Icon: " + value.getIcon() + "\n";
            test += "High: " + value.getHigh() + "\n";
            test += "Low: " + value.getLow() + "\n\n";
        }
        return test;
    }
    
    public enum Icon{
        CLEAR_DAY("/images/clear_day.png"),
        CLEAR_NIGHT("/images/clear_night.png"),
        RAIN("/images/rain.png"),
        SNOW("/images/snow.png"),
        SLEET("/images/sleet.png"),
        WIND("/images/wind.png"),
        FOG("/images/fog.png"),
        CLOUDY("/images/cloud.png"),
        PARTLY_CLOUDY_DAY("/images/partly_cloudy_day.png"),
        PARTLY_CLOUDY_NIGHT("/images/partly_cloudy_night.png"),
        DEFAULT("/images/default.png");
        
        String path;
        
        Icon(String path){
            this.path = path;
        }
        
        public String getPath(){
            return path;
        }
    }
}
