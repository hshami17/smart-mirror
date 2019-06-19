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
import utils.PCM;

/**
 *
 * @author hasan
 */
public class RandomUselessFacts extends APIManager {
    
    public RandomUselessFacts() {
        super(ModuleName.RANDOM_USELESS_FACTS, 60000, PCM.PULL_FACTS);
    }

    @Override
    synchronized protected void fetch() throws IOException {
        URLConnection connection = new URL("http://randomuselessfact.appspot.com/random.json?language=en").openConnection();
        connection.setDoOutput(true);

        JsonReader rdr = Json.createReader(connection.getInputStream());
        JsonObject obj = rdr.readObject();
        
        String uselessFact = obj.getString("text");
        ModelManager.INST.getRandomUselessFactsModel().setUselessFact(uselessFact);
    }
}
