package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import models.ModelManager;
import models.NewsAPIModel;
import static utils.ConfigElements.newsKey;
import static utils.ConfigElements.newsSortBy;
import static utils.ConfigElements.newsSource;

/**
 *
 * @author saadshami
 */
public class News extends API {

    public News(){
        super(300000);
    }

    @Override
    synchronized protected void fetch() throws IOException {
        NewsAPIModel newsModel = ModelManager.INST.getNewsModel();
        URL news_api_url = new URL("https://newsapi.org/v1/articles?"
                + "source=" + newsSource.get() + 
                "&sort_by=" + newsSortBy.get() + 
                "&apiKey=" + newsKey.get());
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
