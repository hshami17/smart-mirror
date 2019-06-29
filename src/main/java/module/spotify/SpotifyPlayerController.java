/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.spotify;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
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
import models.SpotifyTrackModel;
import module.ModuleController;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class SpotifyPlayerController implements Initializable, ModuleController {
    
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
    private Slider trackProgressSlider;
    
    private SpotifyTrackModel currentTrack;
    
    // TODO: Temporary, just using for testing purposes 
    private final long trackLength = 234314;
    private final AtomicBoolean trackPlaying = new AtomicBoolean(true);
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        runNowPlaying();
    }  
    
    private void setCurrentTrack(SpotifyTrackModel currentTrack) {
        this.currentTrack = currentTrack;
    }
    
    private void runNowPlaying() {
        new Thread(() -> {
            double[] trackMinSec = millisecondsToMinutesSeconds(trackLength);
            double minutes = trackMinSec[0];
            double seconds = trackMinSec[1];
            Platform.runLater(() -> {
                imgAlbumArt.setImage(new Image("https://i.scdn.co/image/6c1f62bfe24b3cf4a0f1c61448eada0ae0d16dff"));
                lblArtist.setText("Khalid");
                lblAlbum.setText("Free Spirit");
                lblTrack.setText("Saturday Nights");

                trackProgressSlider.maxProperty().setValue(trackLength);
                lblTrackLength.setText(String.format(Locale.US, "%02d", (int)minutes) + ":" + String.format(Locale.US, "%02d", (int)seconds));
            });
            while (trackProgressSlider.valueProperty().get() != trackLength) {
                Platform.runLater(() -> {
                    trackProgressSlider.increment();
                    long current = (long)trackProgressSlider.valueProperty().get();
                    double[] currentMinSec = millisecondsToMinutesSeconds(current);
                    double minutesCurr = currentMinSec[0];
                    double secondsCurr = currentMinSec[1];
                    lblCurrentTime.setText(String.format(Locale.US, "%02d", (int)minutesCurr) + ":" + String.format(Locale.US, "%02d", (int)secondsCurr) + "/");
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
    
    private double[] millisecondsToMinutesSeconds(long milliseconds) {
        double minutes = ((double)milliseconds / 1000) / 60;
        double seconds = (minutes - (int)minutes) * 60;
        
        return new double[]{minutes, seconds};
    }
    
    // TODO: Test event, remove later
    @FXML
    private void mouseClicked(MouseEvent evt) {
        trackPlaying.set(!trackPlaying.get());
        Platform.runLater(() -> lblTrackStatus.setText(trackPlaying.get() ? "Playing" : "Paused"));
    }
    
}
