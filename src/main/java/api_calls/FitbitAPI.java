package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import utils.Config;

/**
 *
 * @author Hasan Shami
 */
public class FitbitAPI extends API {

    public FitbitAPI() {
        super(600000);
    }

    @Override
    protected void fetch() throws IOException {
        String webaddress = Config.WEB_ADDRESS;
        if (webaddress != null && !webaddress.isEmpty()) {
            URL fitbitDataUrl = new URL("http://" + webaddress + "/fitbit-data");
            InputStream inFitbitData = fitbitDataUrl.openStream();
            JsonObject fitbitData = Json.createReader(inFitbitData).readObject();
            
            System.out.println("GOT DATA:");
            System.out.println(fitbitData);
        }
    }
}
