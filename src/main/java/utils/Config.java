package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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
    private static String weatherKey;
    private static String zipcodeKey;
    private static StringProperty zipcode = new SimpleStringProperty("");
    
    // Quotes
    private static String quoteKey;
    private static StringProperty category = new SimpleStringProperty("");
    
    // Tasks
    private static String taskKey;
    private static String taskClientID;
    private static String listID;
    
    // News
    private static String newsKey;
    private static String newsSource;
    private static String newsSortBy;
    
    private static boolean initialized = false;
    
    public static void configureMirror(){
        
        try {
            if (!initialized){
                clock = new Module("/fxml/Clock.fxml", Module.CLOCK);
                weather = new Module("/fxml/DarkSky.fxml", Module.WEATHER);
                tasks = new Module("/fxml/Wunderlist.fxml", Module.TASKS);
                news = new Module("/fxml/NewsAPI.fxml", Module.NEWS);
                getConfigurations();
                addPropertyListener(zipcode, PCM.PULL_WEATHER);
                addPropertyListener(category, PCM.PULL_QUOTE);
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
   
    
    private static void addPropertyListener(StringProperty propertyVal, String property){
        propertyVal.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            new Thread(() -> {
                PCS.INST.firePropertyChange(property);
            }).start();
        });
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
            doc = dBuilder.parse(Config.class.getResourceAsStream("/mirror_config.xml"));
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
                        weatherKey = key;
                        zipcodeKey = modElement.getElementsByTagName("zipcodekey")
                                .item(0).getTextContent();
                        zipcode.setValue(modElement.getElementsByTagName("zipcode")
                                .item(0).getTextContent());
                        setModulePosition(position, weather);
                        //apiControl(weather);
                        break;
                    case Module.TASKS:
                        taskKey = key;
                        taskClientID = modElement.getElementsByTagName("clientid")
                                .item(0).getTextContent();
                        listID = modElement.getElementsByTagName("listid")
                                .item(0).getTextContent();
                        setModulePosition(position, tasks);
                        //apiControl(tasks);
                        break;
                    case Module.NEWS:
                        newsKey = key;
                        newsSource = modElement.getElementsByTagName("source")
                                .item(0).getTextContent();
                        newsSortBy = modElement.getElementsByTagName("sortby")
                                .item(0).getTextContent();
                        setModulePosition(position, news);
                        //apiControl(news);
                        break;
                    case Module.CLOCK:
                        setModulePosition(position, clock);
                        break;
                    case Module.QUOTE:
                        quoteKey = key;
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
    
//    private static void apiControl(Module module){
//        if (module.isOnMirror()){
//            module.startAPI();
//        }
//        else{
//            module.stopAPI();
//        }
//    }
    
//    public static Module getClock(){
//        return clock;
//    }
//    
//    public static Module getWeather() {
//        return weather;
//    }
//
//    public static Module getTasks() {
//        return tasks;
//    }
//
//    public static Module getNews() {
//        return news;
//    }
    
    public static void addModuleMapListener(ListChangeListener listener){
        //moduleList.addListener(listener);
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
        return weatherKey;
    }
    
    public static String getZipcodeKey(){
        return zipcodeKey;
    }
    
    public static String getQuoteKey() {
        return quoteKey;
    }
    
    public static String getQuoteCategory() {
        return category.getValue();
    }

    public static String getTaskKey() {
        return taskKey;
    }
    
    public static String getTaskClientID(){
        return taskClientID;
    }
    
    public static String getListID(){
        return listID;
    }

    public static String getNewsKey() {
        return newsKey;
    }
    
    public static String getNewsSource() {
        return newsSource;
    }
    
    public static String getNewsSortBy() {
        return newsSortBy;
    }
}
