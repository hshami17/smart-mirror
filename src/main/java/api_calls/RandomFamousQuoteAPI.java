package api_calls;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import models.RandomFamousQuoteModel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import models.ModelManager;
import smartmirror.MirrorViewController;
import utils.Config;
import utils.PCM;
import utils.PCS;

/**
 *
 * @author saadshami
 */
public class RandomFamousQuoteAPI extends Thread implements PropertyChangeListener {

    private final RandomFamousQuoteModel quoteModel;
    private boolean stop = true;
    private final long sleepTime = 60000;
    
    public RandomFamousQuoteAPI() {     
        this.quoteModel = ModelManager.INST.getQuoteModel(); 
        PCS.INST.addPropertyChangeListener(PCM.STOP_QUOTE_API, this);
        PCS.INST.addPropertyChangeListener(PCM.PULL_QUOTE, this);
    }
    
    @Override
    public void run(){
        stop = false;
        while(!stop){
            try {
                PCS.INST.firePropertyChange(PCM.FADE_OUT_QUOTE);
                getQuote();
                Thread.sleep(sleepTime);
            } 
            catch (InterruptedException ex) {
                break;
            }
        }
    }
    
    synchronized public void getQuote(){
        try {
            URLConnection connection = new URL("https://andruxnet-random-famous-quotes.p.mashape.com/?cat=" + Config.getQuoteCategory()).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("X-Mashape-Key", Config.getQuoteKey());
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            
            //OutputStream output = connection.getOutputStream();
            InputStream response = connection.getInputStream();
            
            JsonReader rdr = Json.createReader(response);   
            JsonObject obj = rdr.readObject();
            
            quoteModel.setQuote(obj.getString("quote"), obj.getString("author"));

            if (quoteModel.getQuote().length() > 112){
                getQuote();
            }
            else{
                PCS.INST.firePropertyChange(PCM.QUOTE_UPDATE);
            }
        } catch (IOException ex) {
            try {
                MirrorViewController.alerts.put("THERE WAS AN ISSUE PULLING FROM THE RANDOM FAMOUS QUOTES API");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case PCM.STOP_QUOTE_API:
                stop = true;
                this.interrupt();
                break;
            case PCM.PULL_QUOTE:
                if (!stop){
                    getQuote();
                }
                break;
        }
    }
}
