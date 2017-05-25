package module.spotify;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by hasan on 5/25/17.
 */
public class SpotifyController implements Initializable {

    @FXML
    private WebView spotifyPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = spotifyPlayer.getEngine();
        engine.load(getClass().getResource("/styles/spotify.html").toString());
        engine.setUserStyleSheetLocation(getClass().getResource("/styles/webview-style.css").toString());

//        com.sun.javafx.webkit.WebConsoleListener.setDefaultListener((webView1, message, lineNumber, sourceId) ->
//                System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " + message));
    }
}
