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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.ModelManager;
import models.SpotifyTrackModel;
import module.ModuleController;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class SpotifyController implements Initializable, ModuleController {
    
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
    @FXML
    private ImageView imgPlayPause;
    
    protected static final Image play = new Image("/images/play-icon.png");
    protected static final Image pause = new Image("/images/pause-icon.png");

    // TODO: Temporary, just using for testing purposes 
    private final AtomicBoolean trackPlaying = new AtomicBoolean(true);
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        runNowPlaying();
    }  

    private void runNowPlaying() {
//        new Thread(() -> {
//            while (trackProgressSlider.valueProperty().get() != trackLength) {
//                Platform.runLater(() -> {
//                    trackProgressSlider.increment();
//                    long current = (long)trackProgressSlider.valueProperty().get();
//                    setTrackProgress(current);
//                });
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(SpotifyPlayerController.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                while (!trackPlaying.get()) {/*wait*/}
//            }
//        }).start();
    }
    
    protected static double[] millisecondsToMinutesSeconds(long milliseconds) {
        double minutes = ((double)milliseconds / 1000) / 60;
        double seconds = (minutes - (int)minutes) * 60;
        
        return new double[]{minutes, seconds};
    }
    
    protected static void setTrackLength(long trackLength, Label lblTrackLength) {
        double[] trackMinSec = millisecondsToMinutesSeconds(trackLength);
        double minutes = trackMinSec[0];
        double seconds = trackMinSec[1];
        lblTrackLength.setText(String.format(Locale.US, "%02d", (int)minutes) + ":" + String.format(Locale.US, "%02d", (int)seconds));
    }
    
    protected static void setTrackProgress(long trackProgress, Label lblCurrentTime) {
        double[] currentMinSec = millisecondsToMinutesSeconds(trackProgress);
        double minutesCurr = currentMinSec[0];
        double secondsCurr = currentMinSec[1];
        lblCurrentTime.setText(String.format(Locale.US, "%02d", (int)minutesCurr) + ":" + String.format(Locale.US, "%02d", (int)secondsCurr) + "/");
    }
    
    @Override
    public void update() {
        SpotifyTrackModel spotifyTrackModel = ModelManager.INST.getSpotifyTrackModel();
        Platform.runLater(() -> {
//            if (isPlaying != trackPlaying.get()) {
//                setTrackProgress(spotifyTrackModel.getTrackProgress());
//                trackProgressSlider.setValue(spotifyTrackModel.getTrackProgress());
//            }
            setTrackProgress(spotifyTrackModel.getTrackProgress(), lblCurrentTime);
            trackProgressSlider.setValue(spotifyTrackModel.getTrackProgress());
            lblTrackStatus.setText(spotifyTrackModel.isPlaying() ? "PLAYING" : "PAUSED");
            imgPlayPause.setImage(spotifyTrackModel.isPlaying() ? play : pause);
            lblAlbum.setText(spotifyTrackModel.getAlbum());
            lblArtist.setText(spotifyTrackModel.getArtist());
            lblTrack.setText(spotifyTrackModel.getTrackName());
            imgAlbumArt.setImage(spotifyTrackModel.getSongArt());
            setTrackLength(spotifyTrackModel.getTrackLength(), lblTrackLength);
            if (trackProgressSlider.getMax() != spotifyTrackModel.getTrackLength()) {
                trackProgressSlider.setMax(spotifyTrackModel.getTrackLength());
            }
        });
    }
}
