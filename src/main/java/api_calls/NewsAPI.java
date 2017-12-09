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
public class NewsAPI extends APIManager {
    
    private final NewsAPIModel newsModel;
    
    public NewsAPI(){
        super(60000, PCM.PULL_NEWS, PCM.STOP_NEWS_API);
        this.newsModel = ModelManager.INST.getNewsModel();
    }

    @Override
    synchronized public void fetch(){
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
}
