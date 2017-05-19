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
    private final DarkSkyModel weatherModel;
    private final NewsAPIModel newsModel;
    private final WunderlistModel tasksModel;
    
    private ModelManager(){
        quoteOfDayModel = new RandomFamousQuoteModel();
        weatherModel = new DarkSkyModel();
        newsModel = new NewsAPIModel();
        tasksModel = new WunderlistModel();
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
    
    public static ModelManager INST = new ModelManager();
}
