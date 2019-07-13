/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author hasan
 */
public class ModelManager {
    
    private final RandomFamousQuoteModel quoteOfDayModel;
    private final RandomUselessFactsModel uselessFactModel;
    private final DarkSkyModel weatherModel;
    private final NewsAPIModel newsModel;
    private final WunderlistModel tasksModel;
    private final SpotifyTrackModel spotifyTrackModel;
    
    private ModelManager(){
        quoteOfDayModel = new RandomFamousQuoteModel();
        uselessFactModel = new RandomUselessFactsModel();
        weatherModel = new DarkSkyModel();
        newsModel = new NewsAPIModel();
        tasksModel = new WunderlistModel();
        spotifyTrackModel = new SpotifyTrackModel();
    }
    
    public RandomFamousQuoteModel getQuoteModel(){
        return quoteOfDayModel;
    }
    
    public DarkSkyModel getWeatherModel(){
        return weatherModel;
    }
    
    public NewsAPIModel getNewsModel(){
        return newsModel;
    }
    
    public WunderlistModel getTasksModel(){
        return tasksModel;
    }
    
    public RandomUselessFactsModel getRandomUselessFactsModel() {
        return uselessFactModel;
    }
    
    public SpotifyTrackModel getSpotifyTrackModel() {
        return spotifyTrackModel;
    }
    
    public static ModelManager INST = new ModelManager();
}
