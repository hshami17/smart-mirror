/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_calls;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import models.ModelManager;
import models.UselessFactsModel;

/**
 *
 * @author hasan
 */
public class UselessFactsAPI extends APIManager {
    
    public UselessFactsAPI() {
        super(30000);
    }

    @Override
    synchronized protected void fetch() throws IOException {
        UselessFactsModel uselessFactsModel = ModelManager.INST.getUselessFactsModel();
        URLConnection connection = new URL("https://uselessfacts.jsph.pl/random.json?language=en").openConnection();
        connection.setDoOutput(true);

        JsonReader rdr = Json.createReader(connection.getInputStream());
        JsonObject obj = rdr.readObject();
        
        String uselessFact = obj.getString("text");
        uselessFactsModel.setUselessFact(uselessFact);
    }
}
