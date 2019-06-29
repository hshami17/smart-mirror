package api_calls;

import module.ModuleName;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import models.RandomFamousQuoteModel;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import models.ModelManager;
import utils.Config;
import utils.PCM;

/**
 *
 * @author saadshami
 */
public class RandomFamousQuoteAPI extends APIManager {

    private final RandomFamousQuoteModel quoteModel;

    public RandomFamousQuoteAPI() {
        super(60000, PCM.PULL_QUOTE);
        this.quoteModel = ModelManager.INST.getQuoteModel();
    }

    @Override
    synchronized protected void fetch() throws IOException {
        URLConnection connection = new URL("https://andruxnet-random-famous-quotes.p.mashape.com/?cat=" + Config.getQuoteCategory()).openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("X-Mashape-Key", Config.getQuoteKey());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/json");

        //OutputStream output = connection.getOutputStream();
        InputStream response = connection.getInputStream();

        JsonReader rdr = Json.createReader(response);   
        JsonArray arr = rdr.readArray();
        if (!arr.isEmpty()) {
            JsonObject obj = arr.getJsonObject(0);

            quoteModel.setQuote(obj.getString("quote"), obj.getString("author"));

            if (quoteModel.getQuote().length() > 112){
                this.fetch();
            }
        }
        else {
            this.fetch();
        }
    }
}
