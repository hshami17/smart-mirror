package smartmirror;

import api_calls.HueMotionSensorAPI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Config;
import utils.PCM;
import utils.PCS;
import utils.Watcher;

/**
 * 
 * @author hasan
 */
public class SmartMirror extends Application implements PropertyChangeListener {
    
    public static Stage mirrorStage = null;
    private static boolean fullscreen = false;
    private AtomicBoolean minimalModeActive = null;
    private MirrorViewController mirrorViewController;
    private Scene mirrorView;
  
    public static void main(String[] args) {
        String configPath = System.getenv("CONFIGPATH");
        String watchPath = System.getenv("WATCHPATH");
        String webAddress = System.getenv("WEBADDRESS");
        Config.CONFIG_PATH = configPath != null ? configPath : "src/main/resources/mirror_config.xml";
        Config.WATCH_PATH = watchPath != null ? watchPath : "src/main/resources";
        Config.WEB_ADDRESS = webAddress != null ? webAddress : "Web service address not found";

        for (String arg : args) {
            switch (arg) {
                case "-fullscreen":
                    fullscreen = true;
                    break;
                default:
                    System.out.println("UNKNOWN ARG: " + arg);
                    break;
            }
        }

        launch(args);
    }
    
    private static String getIpv4(String interfaceName) {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            if (networkInterface == null) {
                return "";
            }
            String ipv4Addr;
            Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
            InetAddress curr = addrs.nextElement();
            while (addrs.hasMoreElements()) {
                curr = addrs.nextElement();
                if (curr instanceof Inet4Address && !curr.isLoopbackAddress()) {
                    ipv4Addr = curr.toString().replace("/", "").trim();
                    return ipv4Addr;
                }
            }
            return "";
        } catch (SocketException ex) {
            Logger.getLogger(SmartMirror.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    @Override
    public void start(Stage mirrorStage) throws Exception {
        // Get mirror settings from XML
        Config.configureMirror();
        
        // Load mirror view
        FXMLLoader loader = new FXMLLoader(SmartMirror.class.getResource("/fxml/MirrorView.fxml"));
        Parent mirrorRoot = loader.load();
        mirrorViewController = loader.getController();
        mirrorView = new Scene(mirrorRoot);
        
        mirrorViewController.getMirrorPane().setOpacity(1.0);
        mirrorViewController.getMinimalPane().setOpacity(0.0);
        mirrorViewController.getMirrorPane().setVisible(true);
        mirrorViewController.getMinimalPane().setVisible(true);

        // Watcher to watch for config file changes
        PCS.INST.addPropertyChangeListener(PCM.NEW_CONFIG, this);
        new Watcher().start();
        
        // TODO: For testing only
        mirrorStage.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            boolean show = false;
            @Override
            public void handle(MouseEvent event) {
                show = !show;
                goToMinimalMode(show);
            }
        });
        
        mirrorStage.setScene(mirrorView);
        mirrorStage.setFullScreen(fullscreen);
        mirrorStage.setOnCloseRequest(e -> System.exit(0));
        mirrorStage.show();
        
        SmartMirror.mirrorStage = mirrorStage;
        
        PCS.INST.addPropertyChangeListener(PCM.MINIMAL_MODE, this);
        if (System.getenv("HOSTNAME").equals("raspberrypi")) {
            new HueMotionSensorAPI().start();
        }
    }
    
    synchronized private void goToMinimalMode(boolean minimalMode){
        if (minimalModeActive == null) {
            minimalModeActive = new AtomicBoolean();
        }
        else if (minimalMode == minimalModeActive.get()) {
            // Ignore if we are in minimal mode already
            return;
        }
        
        Duration duration = new Duration(300);
        Pane fadingOutPane = minimalMode ? mirrorViewController.getMirrorPane() : mirrorViewController.getMinimalPane();
        Pane fadingInPane = minimalMode ? mirrorViewController.getMinimalPane() : mirrorViewController.getMirrorPane();
        
        FadeTransition viewFadeOut = new FadeTransition(duration, fadingOutPane);
        viewFadeOut.setFromValue(fadingOutPane.getOpacity());
        viewFadeOut.setToValue(0.0);  
        
        FadeTransition viewFadeIn = new FadeTransition(duration, fadingInPane);
        viewFadeIn.setFromValue(fadingInPane.getOpacity());
        viewFadeIn.setToValue(1.0);
        
        viewFadeOut.setOnFinished(e -> viewFadeIn.play());        
        viewFadeOut.play();
        
        minimalModeActive.set(minimalMode);
    }
    
    private void loadNewConfig(){
        mirrorViewController.clearAllContainers();
        Config.configureMirror();
        mirrorViewController.placeModules();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase(PCM.NEW_CONFIG)){
            loadNewConfig();
        }
        else if (evt.getPropertyName().equals(PCM.MINIMAL_MODE)) {
            boolean minimalMode = (boolean)evt.getNewValue();
            Platform.runLater(() -> goToMinimalMode(minimalMode));
        }
    }
}
