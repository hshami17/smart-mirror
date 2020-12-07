package utils;

import api_calls.API;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import module.MinimalModules;
import module.Module;
import module.MirrorModule;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// Config elements 
import static utils.ConfigElements.category;
import static utils.ConfigElements.darkskyKey;
import static utils.ConfigElements.listID;
import static utils.ConfigElements.newsKey;
import static utils.ConfigElements.newsSortBy;
import static utils.ConfigElements.newsSource;
import static utils.ConfigElements.randomFamousQuoteKey;
import static utils.ConfigElements.wunderlistClientID;
import static utils.ConfigElements.wunderlistKey;
import static utils.ConfigElements.zipcode;
import static utils.ConfigElements.zipcodeKey;

/**
 *
 * @author hasan
 */
public class Config {

    private static final Map<MirrorModule, Module> modules = new HashMap<>();

    public static String WEB_ADDRESS;
    public static String CONFIG_PATH;
    public static String WATCH_PATH;
    
    private static boolean initialized = false;
    
    public static void configureMirror(){
        try {
            parseMirrorConfig();
            if (!initialized){
                // Weather prop listeners
                addPropertyListeners(modules.get(MirrorModule.DARKSKY), 
                        darkskyKey, 
                        zipcodeKey, 
                        zipcode);
                // Quote prop listeners
                addPropertyListeners(modules.get(MirrorModule.RANDOM_FAMOUS_QUOTE), 
                        randomFamousQuoteKey,
                        category);
                // News prop listeners
                addPropertyListeners(modules.get(MirrorModule.NEWS),
                        newsKey,
                        newsSource,
                        newsSortBy);
                
                initialized = true;
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
   
    
    private static void addPropertyListeners(Module module, StringProperty... properties){
        for (StringProperty property : properties){
            property.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                new Thread(() -> {
                    if (module.hasApi()) {
                        module.getApi().pullApi();
                    }
                }).start();
            }); 
        }
    }
    
    private static void hookMinimalModules() {
        Module minimalDarkSky = modules.get(MirrorModule.DARKSKY_MINIMAL);
        API darkSkyAPI = MirrorModule.DARKSKY.getApi();
        darkSkyAPI.addModuleSubscriber(minimalDarkSky);
        darkSkyAPI.start();

        Module minimalSpotify = modules.get(MirrorModule.SPOTIFY_MINIMAL);
        API spotifyAPI = MirrorModule.SPOTIFY.getApi();
        spotifyAPI.addModuleSubscriber(minimalSpotify);
        
        Module minimalCovid = modules.get(MirrorModule.COVID_MINIMAL);
        API covidAPI = MirrorModule.COVID.getApi();
        covidAPI.addModuleSubscriber(minimalCovid);
    }
    
    private static void parseMirrorConfig() throws IOException, ParserConfigurationException,
                                            DOMException, SAXException{
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc;

        doc = dBuilder.parse(new File(CONFIG_PATH));
        doc.getDocumentElement().normalize();

        NodeList moduleConfigs = doc.getElementsByTagName("module");
        
        // Add minimal modules to map
        if (!initialized) {
            MinimalModules.getModules().forEach((minimalModule) -> {
                Module module = new Module(minimalModule);
                modules.put(minimalModule, module);
            });
            hookMinimalModules();
        }
        
        // Get API keys
        for (int i=0; i<moduleConfigs.getLength(); i++){
            Node moduleNode = moduleConfigs.item(i);
            if (moduleNode.getNodeType() == Node.ELEMENT_NODE){
                Element modElement = (Element) moduleNode;
                NodeList apiKeyNodeList = modElement.getElementsByTagName("key");
                String apiKey = "";
                if (apiKeyNodeList.item(0) != null) {
                    apiKey = apiKeyNodeList.item(0).getTextContent();
                }
                
                String moduleNameXml = modElement.getAttribute("name").replace("-", "_").toUpperCase();
                String positionXml = modElement.getElementsByTagName("position").item(0).getTextContent().toUpperCase();
                
                try {
                    MirrorModule moduleName = MirrorModule.valueOf(moduleNameXml);
                    Position position;
                    if (!positionXml.equals("-")) {
                        position = Position.valueOf(positionXml);
                    }
                    else {
                        position = Position.NONE;
                    }
                    
                    // ******** READ IN KEYS FROM CONFIG FILE *********
                    switch (moduleName){
                        case DARKSKY:
                            darkskyKey.setValue(apiKey);
                            zipcodeKey.setValue(modElement.getElementsByTagName("zipcodekey")
                                    .item(0).getTextContent());
                            zipcode.setValue(modElement.getElementsByTagName("zipcode")
                                    .item(0).getTextContent());
                            break;
                        case WUNDERLIST:
                            wunderlistKey.setValue(apiKey);
                            wunderlistClientID.setValue(modElement.getElementsByTagName("clientid")
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
                            randomFamousQuoteKey.setValue(apiKey);
                            category.setValue(modElement.getElementsByTagName("category")
                                    .item(0).getTextContent());
                            break;
                    }
                    
                    if (!initialized) {
                        Module module = new Module(moduleName);
                        modules.put(moduleName, module);
                        
                        // ******** BIND VISIBLE STATE OF MINIMAL MODULES *********
                        switch (moduleName) {
                            case SPOTIFY:
                                Module minimalSpotify = modules.get(MirrorModule.SPOTIFY_MINIMAL);
                                minimalSpotify.visibleProperty().bind(module.onMirrorProperty());
                                break;
                            case COVID:
                                Module minimalCovid = modules.get(MirrorModule.COVID_MINIMAL);
                                minimalCovid.visibleProperty().bind(module.onMirrorProperty());
                                break;
                        }
                    }

                    Module module = modules.get(moduleName);
                    module.setPosition(position);
                }
                catch (IllegalArgumentException ex) {
                    System.err.println("[JAVA] Module not found in enum or is ignored: " + moduleNameXml);
                }
            }
        }
    }

    public static Module getModuleAt(Position position) {
        for (Map.Entry<MirrorModule, Module> entry : modules.entrySet()) {
            if (entry.getValue().getPosition() == position) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public static Module getModule(MirrorModule moduleName) {
        return modules.get(moduleName);
    }
}
