/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_calls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.scene.image.Image;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import models.ModelManager;
import models.SpotifyTrackModel;
import utils.PCM;

/**
 *
 * @author hasan
 */
public class SpotifyAPI extends APIManager {
    
    private final SpotifyTrackModel spotifyTrackModel;
    private String lastAlbumImageUrl = "";
    
    public SpotifyAPI() {
        super(200, PCM.PULL_SPOTIFY);
        this.spotifyTrackModel = ModelManager.INST.getSpotifyTrackModel();
    }

    @Override
    protected void fetch() throws IOException {
        String webaddress = System.getenv("WEBADDRESS");
        if (webaddress != null && !webaddress.isEmpty()) {
            try {
                URL spotifyCurrentTrackUrl = new URL("http://" + webaddress + "/spotify-current-track");
                InputStream inSpotifyApi = spotifyCurrentTrackUrl.openStream();
                JsonObject objSpotifyApi = Json.createReader(inSpotifyApi).readObject();

                JsonObject itemObj = objSpotifyApi.getJsonObject("item");

                boolean isPlaying = objSpotifyApi.getBoolean("is_playing");
                String currentTrack = itemObj.getJsonString("name").getString();
                String albumArtUrl = itemObj.getJsonObject("album").getJsonArray("images").getJsonObject(0).getJsonString("url").getString();
                StringBuilder artists = new StringBuilder();
                JsonArray artistsJson = itemObj.getJsonArray("artists");
                for (int i=0; i<artistsJson.size(); i++) {
                    JsonObject artistObj = artistsJson.getJsonObject(i);
                    artists.append(artistObj.getJsonString("name").getString());
                    if (i != artistsJson.size()-1) {
                        artists.append(", ");
                    }
                }
                String albumName = itemObj.getJsonObject("album").getJsonString("name").getString();
                long duration = itemObj.getJsonNumber("duration_ms").longValue();
                long progress = objSpotifyApi.getJsonNumber("progress_ms").longValue();

                spotifyTrackModel.setPlaying(isPlaying);
                spotifyTrackModel.setArtist(artists.toString());
                spotifyTrackModel.setAlbum(albumName);
                spotifyTrackModel.setTrackName(currentTrack);
                if (!albumArtUrl.equals(lastAlbumImageUrl)) {
                    spotifyTrackModel.setSongArt(new Image(albumArtUrl));
                }
                lastAlbumImageUrl = albumArtUrl;
                spotifyTrackModel.setTrackLength(duration);
                spotifyTrackModel.setTrackProgress(progress);
            }
            catch (ClassCastException | NullPointerException ex) {}
        }
    }
}
