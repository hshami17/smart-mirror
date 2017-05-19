package models;

import java.util.ArrayList;

/**
 *
 * @author hasan
 */
public class NewsAPIModel {
    
    private final ArrayList<String> headlines;
    
    public NewsAPIModel(){
        headlines = new ArrayList<>();
    }
    
    public ArrayList<String> getHeadlines() {
        return headlines;
    }
    
    public void clearNews(){
        headlines.clear();
    }
    
    @Override
    public String toString(){
        String text = "";
        for (String value : headlines){
            text += value + "\n";
        }
        return text;
    }
}
