package models;

/**
 *
 * @author saadshami
 */
public class RandomFamousQuoteModel {
    
    private String quote;
    
    public RandomFamousQuoteModel(){
        
    }
    
    public String getQuote() {
        return quote;
    }
    
    public void setQuote(String quote, String author) {
        this.quote = "\"" + quote + "\"" + " - " + author;
    }
 
    @Override
    public String toString(){
        return quote;
    }
    
}
