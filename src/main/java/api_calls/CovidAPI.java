/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import models.CovidModel;
import models.ModelManager;
import utils.Config;

/**
 *
 * @author hasan
 */
public class CovidAPI extends APIManager {
    
    public CovidAPI() {
        super(600000);
    }
    
    @Override
    protected void fetch() throws IOException {
        String webaddress = Config.WEB_ADDRESS;
        if (webaddress != null && !webaddress.isEmpty()) {
            CovidModel covidModel = ModelManager.INST.getCovidModel();
            try {
                URL covidDataUrl = new URL("http://" + webaddress + "/api/covid");
                InputStream inMotionSensor = covidDataUrl.openStream();
                JsonObject covidData = Json.createReader(inMotionSensor).readObject();
                
                String totalCases = covidData.getJsonString("totalCases").getString();
                String totalDeaths = covidData.getJsonString("totalDeaths").getString();
                String totalRecovered = covidData.getJsonString("totalRecovered").getString();

                String totalCasesUSA = covidData.getJsonString("totalUsCases").getString();
                String totalDeathsUSA = covidData.getJsonString("totalUsDeaths").getString();
                String totalRecoveredUSA = covidData.getJsonString("totalUsRecovered").getString();
                
                covidModel.setTotalCases(totalCases);
                covidModel.setTotalDeaths(totalDeaths);
                covidModel.setTotalRecovered(totalRecovered);
                covidModel.setTotalCasesUSA(totalCasesUSA);
                covidModel.setTotalDeathsUSA(totalDeathsUSA);
                covidModel.setTotalRecoveredUSA(totalRecoveredUSA);
            }
            catch (ClassCastException | NullPointerException ex) {}
        }
    }
    
}
