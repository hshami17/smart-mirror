package api_calls;

import module.ModuleName;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import models.ModelManager;
import models.NewsAPIModel;
import utils.Config;
import utils.PCM;

/**
 *
 * @author saadshami
 */
public class NewsAPI extends APIManager {
    
    private final NewsAPIModel newsModel;
    
    public NewsAPI(){
        super(300000, PCM.FETCH_NEWS);
        this.newsModel = ModelManager.INST.getNewsModel();
    }

    @Override
    synchronized protected void fetch() throws IOException {
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
    }
}
