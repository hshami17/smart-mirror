package api_calls;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.sun.jdi.Mirror;
import models.ModelManager;
import models.NewsAPIModel;
import smartmirror.MirrorViewController;
import utils.Config;
import utils.PCM;
import utils.PCS;

/**
 *
 * @author saadshami
 */
public class NewsAPI extends Thread implements PropertyChangeListener {
    
    private final NewsAPIModel newsModel;
    private boolean stop = true;
    private final long sleepTime = 60000;
    
    public NewsAPI(){
        this.newsModel = ModelManager.INST.getNewsModel();
        PCS.INST.addPropertyChangeListener(PCM.STOP_NEWS_API, this);
        PCS.INST.addPropertyChangeListener(PCM.PULL_NEWS, this);
    }
    
    @Override
    public void run(){
        stop = false;
        while(!stop){
            try {
                getNews();
                Thread.sleep(sleepTime);
            } 
            catch (InterruptedException ex) {
                break;
            }
        }
    }
    
    synchronized public void getNews(){
        try {
            URL news_api_url = new URL("https://newsapi.org/v1/articles?"
                    + "source=" + Config.getNewsSource() + 
                    "&sort_by=" + Config.getNewsSortBy() + 
                    "&apiKey=" + Config.getNewsKey());
            InputStream is_news_api = news_api_url.openStream();
            JsonReader rdr_news_api = Json.createReader(is_news_api);   
            JsonObject obj_news_api = rdr_news_api.readObject();
            
            JsonArray articles = obj_news_api.getJsonArray("articles");
            
            newsModel.clearNews();
            for (int i=0; i<articles.size(); i++){
                newsModel.getHeadlines().add(articles.getJsonObject(i).getString("title"));
            }
            
            PCS.INST.firePropertyChange(PCM.NEWS_UPDATE);
            
        } catch (IOException ex){
            try {
                MirrorViewController.alerts.put("THERE WAS AN ISSUE PULLING FROM THE NEWS API");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }  

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case PCM.STOP_NEWS_API:
                stop = true;
                this.interrupt();
                break;
            case PCM.PULL_NEWS:
                if (!stop){
                    getNews();
                }
                break;
        }
    }
}
