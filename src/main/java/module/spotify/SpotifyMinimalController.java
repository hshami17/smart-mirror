/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.spotify;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import models.ModelManager;
import models.SpotifyTrackModel;
import module.ModuleController;
import static module.spotify.SpotifyController.pause;
import static module.spotify.SpotifyController.play;

/**
 *
 * @author hasan
 */
public class SpotifyMinimalController implements Initializable, ModuleController {
    
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
      
    }
    
    @Override
    public void update() {
        SpotifyTrackModel spotifyTrackModel = ModelManager.INST.getSpotifyTrackModel();
        Platform.runLater(() -> {
//            if (isPlaying != trackPlaying.get()) {
//                setTrackProgress(spotifyTrackModel.getTrackProgress());
//                trackProgressSlider.setValue(spotifyTrackModel.getTrackProgress());
//            }
            SpotifyController.setTrackProgress(spotifyTrackModel.getTrackProgress(), lblCurrentTime);
            trackProgressSlider.setValue(spotifyTrackModel.getTrackProgress());
            lblTrackStatus.setText(spotifyTrackModel.isPlaying() ? "PLAYING" : "PAUSED");
            imgPlayPause.setImage(spotifyTrackModel.isPlaying() ? play : pause);
            lblAlbum.setText(spotifyTrackModel.getAlbum());
            lblArtist.setText(spotifyTrackModel.getArtist());
            lblTrack.setText(spotifyTrackModel.getTrackName());
            imgAlbumArt.setImage(spotifyTrackModel.getSongArt());
            SpotifyController.setTrackLength(spotifyTrackModel.getTrackLength(), lblTrackLength);
            if (trackProgressSlider.getMax() != spotifyTrackModel.getTrackLength()) {
                trackProgressSlider.setMax(spotifyTrackModel.getTrackLength());
            }
        });
    }
}
