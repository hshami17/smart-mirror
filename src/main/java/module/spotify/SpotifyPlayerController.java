/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.spotify;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class SpotifyPlayerController implements Initializable {
    
    @FXML
    private ImageView imgAlbumArt;
    @FXML
    private Label lblArtist;
    @FXML
    private Label lblAlbum;
    @FXML
    private Label lblTrack;
    @FXML
    private Label lblTrackStatus;
    @FXML
    private Label lblCurrentTime;
    @FXML
    private Label lblTrackLength;
    @FXML
    private Slider trackProgress;
    
    private AtomicBoolean trackPlaying = new AtomicBoolean(true);
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread(() -> {
            long max = 23431;
            int minutes = (int)TimeUnit.MILLISECONDS.toMinutes(max);
            int seconds = (int)TimeUnit.MILLISECONDS.toSeconds(max);
            Platform.runLater(() -> {
                imgAlbumArt.setImage(new Image("https://i.scdn.co/image/6c1f62bfe24b3cf4a0f1c61448eada0ae0d16dff"));
                lblArtist.setText("Khalid");
                lblAlbum.setText("Free Spirit");
                lblTrack.setText("Saturday Nights");
                
                trackProgress.maxProperty().setValue(max);
                lblTrackLength.setText(String.format(Locale.US, "%02d", minutes) + ":" + String.format(Locale.US, "%02d", seconds));
            });
            while (trackProgress.valueProperty().get() != max) {
                Platform.runLater(() -> {
                    trackProgress.increment();
                    long current = (long)trackProgress.valueProperty().get();
                    int minutesCurr = (int)TimeUnit.MILLISECONDS.toMinutes(current);
                    int secondsCurr = (int)TimeUnit.MILLISECONDS.toSeconds(current);
                    lblCurrentTime.setText(String.format(Locale.US, "%02d", minutesCurr) + ":" + String.format(Locale.US, "%02d", secondsCurr) + "/");
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SpotifyPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
                while (!trackPlaying.get()) {/*wait*/}
            }
        }).start();
    }    
    
    // TODO: Test event, remove later
    @FXML
    private void mouseClicked(MouseEvent evt) {
        trackPlaying.set(!trackPlaying.get());
        Platform.runLater(() -> lblTrackStatus.setText(trackPlaying.get() ? "Playing" : "Paused"));
    }
    
}
