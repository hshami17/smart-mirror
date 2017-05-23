package utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import module.Module;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author hasan
 */
public class Config {
    
    public static boolean jarRun = false;
    public static boolean fullscreen = false;
    
    private static Module clock;
    private static Module weather;
    private static Module tasks;
    private static Module news;
    
    private static Module topRightMod;
    private static Module topLeftMod;
    private static Module bottomRightMod;
    private static Module bottomLeftMod;
    private static Module bottom;
    private static Module top;
    
    // Weather
    private static final StringProperty weatherKey = new SimpleStringProperty("");
    private static final StringProperty zipcodeKey = new SimpleStringProperty("");
    private static final StringProperty zipcode = new SimpleStringProperty("");
    
    // Quotes
    private static final StringProperty quoteKey = new SimpleStringProperty(""); 
    private static final StringProperty category = new SimpleStringProperty("");
    
    // Tasks
    private static final StringProperty taskKey = new SimpleStringProperty("");
    private static final StringProperty taskClientID = new SimpleStringProperty("");
    private static final StringProperty listID = new SimpleStringProperty("");
    
    // News
    private static final StringProperty newsKey = new SimpleStringProperty("");
    private static final StringProperty newsSource = new SimpleStringProperty("");
    private static final StringProperty newsSortBy = new SimpleStringProperty("");
    
    private static boolean initialized = false;
    
    public static void configureMirror(){
        
        try {
            if (!initialized){
                clock = new Module("/fxml/Clock.fxml", Module.CLOCK);
                weather = new Module("/fxml/DarkSky.fxml", Module.WEATHER);
                tasks = new Module("/fxml/Wunderlist.fxml", Module.TASKS);
                news = new Module("/fxml/NewsAPI.fxml", Module.NEWS);
                getConfigurations();
                // Weather prop listeners
                addPropertyListeners(PCM.PULL_WEATHER, 
                        weatherKey, 
                        zipcodeKey, 
                        zipcode);
                // Quote prop listeners
                addPropertyListeners(PCM.PULL_QUOTE, 
                        quoteKey,
                        category);
                // News prop listeners
                addPropertyListeners(PCM.PULL_NEWS,
                        newsKey,
                        newsSource,
                        newsSortBy);
                initialized = true;
            }
            else{
                topRightMod = null;
                topLeftMod = null;
                bottomRightMod = null;
                bottomLeftMod = null;
                bottom = null;
                top = null;
                getConfigurations();
            }
        }
        catch (IOException ex){
            System.err.print("FILE ERROR: mirror_config.xml\n");
            ex.printStackTrace();
            System.exit(0);
        }
        catch (ParserConfigurationException | DOMException | SAXException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    
    private static void addPropertyListeners(String propName, StringProperty... properties){
        for (StringProperty property : properties){
            property.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                new Thread(() -> {
                    PCS.INST.firePropertyChange(propName);
                }).start();
            }); 
        }
    }
    
    private static void getConfigurations() throws IOException, ParserConfigurationException,
                                            DOMException, SAXException{
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;
        // Check if running from JAR
        if (jarRun){
            doc = dBuilder.parse(new File("resources/mirror_config.xml"));
        }
        else{
            doc = dBuilder.parse(new File("src/main/resources/mirror_config.xml"));
        }
        doc.getDocumentElement().normalize();

        NodeList moduleConfigs = doc.getElementsByTagName("module");

        // Get API keys
        for (int i=0; i<moduleConfigs.getLength(); i++){
            Node module = moduleConfigs.item(i);
            if (module.getNodeType() == Node.ELEMENT_NODE){
                Element modElement = (Element) module;
                String key = modElement.getElementsByTagName("key")
                        .item(0).getTextContent();
                String position = modElement.getElementsByTagName("position")
                        .item(0).getTextContent();
                switch (modElement.getAttribute("name").toUpperCase()){
                    case Module.WEATHER:
                        weatherKey.setValue(key);
                        zipcodeKey.setValue(modElement.getElementsByTagName("zipcodekey")
                                .item(0).getTextContent());
                        zipcode.setValue(modElement.getElementsByTagName("zipcode")
                                .item(0).getTextContent());
                        setModulePosition(position, weather);
                        break;
                    case Module.TASKS:
                        taskKey.setValue(key);
                        taskClientID.setValue(modElement.getElementsByTagName("clientid")
                                .item(0).getTextContent());
                        listID.setValue(modElement.getElementsByTagName("listid")
                                .item(0).getTextContent());
                        setModulePosition(position, tasks);
                        break;
                    case Module.NEWS:
                        newsKey.setValue(key);
                        newsSource.setValue(modElement.getElementsByTagName("source")
                                .item(0).getTextContent());
                        newsSortBy.setValue(modElement.getElementsByTagName("sortby")
                                .item(0).getTextContent());
                        setModulePosition(position, news);
                        break;
                    case Module.CLOCK:
                        setModulePosition(position, clock);
                        break;
                    case Module.QUOTE:
                        quoteKey.setValue(key);
                        category.setValue(modElement.getElementsByTagName("category")
                                .item(0).getTextContent());
                        break;
                    default:
                        System.err.println("UNKNOWN NAME ATTR: " + modElement.getAttribute("name"));
                }
            }
        }        
    }
    
    private static void setModulePosition(String position, Module module){
        switch (position){
            case "topRight":
                topRightMod = module;
                module.setOnMirror(true);
                break;
            case "topLeft":
                topLeftMod = module;
                module.setOnMirror(true);
                break;
            case "bottomRight":
                bottomRightMod = module;
                module.setOnMirror(true);
                break;
            case "bottomLeft":
                bottomLeftMod = module;
                module.setOnMirror(true);
                break;
            case "bottom":
                bottom = module;
                module.setOnMirror(true);
                break;
            case "top":
                top = module;
                module.setOnMirror(true);
                break;
            default:
                module.setOnMirror(false);
                break;
        }
    }
        
    public static Module getTopRightMod(){
        return topRightMod;
    }
    
    public static Module getTopLeftMod(){
        return topLeftMod;
    }
    
    public static Module getBottomRightMod(){
        return bottomRightMod;
    }
    
    public static Module getBottomLeftMod(){
        return bottomLeftMod;
    }
    
    public static Module getBottomMod(){
        return bottom;
    }
    
    public static Module getTopMod(){
        return top;
    }
    
    public static String getZipCode(){
        return zipcode.getValue();
    }
    
    public static String getWeatherKey(){
        return weatherKey.getValue();
    }
    
    public static String getZipcodeKey(){
        return zipcodeKey.getValue();
    }
    
    public static String getQuoteKey() {
        return quoteKey.getValue();
    }
    
    public static String getQuoteCategory() {
        return category.getValue();
    }

    public static String getTaskKey() {
        return taskKey.getValue();
    }
    
    public static String getTaskClientID(){
        return taskClientID.getValue();
    }
    
    public static String getListID(){
        return listID.getValue();
    }

    public static String getNewsKey() {
        return newsKey.getValue();
    }
    
    public static String getNewsSource() {
        return newsSource.getValue();
    }
    
    public static String getNewsSortBy() {
        return newsSortBy.getValue();
    }
}
