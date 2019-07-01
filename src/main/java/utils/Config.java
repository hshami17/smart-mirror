package utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import module.Module;
import module.ModuleName;
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
    
    public static String CONFIG_PATH;
    public static String WATCH_PATH;
    
    private static final Map<ModuleName, Module> modules = new HashMap<>();
    
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
                // Weather prop listeners
                addPropertyListeners(PCM.FETCH_DARKSKY, 
                        weatherKey, 
                        zipcodeKey, 
                        zipcode);
                // Quote prop listeners
                addPropertyListeners(PCM.FETCH_RANDOM_FAMOUS_QUOTE, 
                        quoteKey,
                        category);
                // News prop listeners
                addPropertyListeners(PCM.FETCH_NEWS,
                        newsKey,
                        newsSource,
                        newsSortBy);
                parseMirrorConfig();
                initialized = true;
            }
            else {
                parseMirrorConfig();
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
    
    private static void parseMirrorConfig() throws IOException, ParserConfigurationException,
                                            DOMException, SAXException{
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;

        doc = dBuilder.parse(new File(CONFIG_PATH));
        doc.getDocumentElement().normalize();

        NodeList moduleConfigs = doc.getElementsByTagName("module");
        
        // Get API keys
        for (int i=0; i<moduleConfigs.getLength(); i++){
            Node moduleNode = moduleConfigs.item(i);
            if (moduleNode.getNodeType() == Node.ELEMENT_NODE){
                Element modElement = (Element) moduleNode;
                String apiKey = modElement.getElementsByTagName("key").item(0).getTextContent();
                
                String moduleNameXml = modElement.getAttribute("name").replace("-", "_").toUpperCase();
                String positionXml = modElement.getElementsByTagName("position").item(0).getTextContent().toUpperCase();
                
                try {
                    ModuleName moduleName = ModuleName.valueOf(moduleNameXml);
                    Position position;
                    if (!positionXml.equals("-")) {
                        position = Position.valueOf(positionXml);
                    }
                    else {
                        position = Position.NONE;
                    }
                
                    switch (moduleName){
                        case DARKSKY:
                            weatherKey.setValue(apiKey);
                            zipcodeKey.setValue(modElement.getElementsByTagName("zipcodekey")
                                    .item(0).getTextContent());
                            zipcode.setValue(modElement.getElementsByTagName("zipcode")
                                    .item(0).getTextContent());
                            break;
                        case WUNDERLIST:
                            taskKey.setValue(apiKey);
                            taskClientID.setValue(modElement.getElementsByTagName("clientid")
                                    .item(0).getTextContent());
                            listID.setValue(modElement.getElementsByTagName("listid")
                                    .item(0).getTextContent());
                            break;
                        case NEWS:
                            newsKey.setValue(apiKey);
                            newsSource.setValue(modElement.getElementsByTagName("source")
                                    .item(0).getTextContent());
                            newsSortBy.setValue(modElement.getElementsByTagName("sortby")
                                    .item(0).getTextContent());
                            break;
                        case RANDOM_FAMOUS_QUOTE:
                            quoteKey.setValue(apiKey);
                            category.setValue(modElement.getElementsByTagName("category")
                                    .item(0).getTextContent());
                            break;
                    }

                    if (!initialized) {
                        Module module = new Module(moduleName);
                        modules.put(moduleName, module);
                    }

                    Module module = modules.get(moduleName);
                    module.setPosition(position);
                }
                catch (IllegalArgumentException ex) {
                    System.err.println("ERROR IN CONFIG: " + moduleNameXml);
                    System.err.println(ex.toString());
                }
            }
        }        
    }

    public static Module getModuleAt(Position position) {
        for (Map.Entry<ModuleName, Module> entry : modules.entrySet()) {
            if (entry.getValue().getPosition() == position) {
                return entry.getValue();
            }
        }
        return null;
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
