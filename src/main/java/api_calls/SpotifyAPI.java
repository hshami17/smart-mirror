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
import utils.Config;

/**
 *
 * @author hasan
 */
public class SpotifyAPI extends API {
    
    private String lastAlbumImageUrl = "";
    
    public SpotifyAPI() {
        super(200);
    }

    @Override
    protected void fetch() throws IOException {
        String webaddress = Config.WEB_ADDRESS;
        if (webaddress != null && !webaddress.isEmpty()) {
            SpotifyTrackModel spotifyTrackModel = ModelManager.INST.getSpotifyTrackModel();
            try {
                URL spotifyCurrentTrackUrl = new URL("http://" + webaddress + "/spotify-current-track");
                InputStream inSpotifyApi = spotifyCurrentTrackUrl.openStream();
                JsonObject objSpotifyApi = Json.createReader(inSpotifyApi).readObject();
                
                JsonObject itemObj = objSpotifyApi.getJsonObject("item");

                boolean isPlaying = objSpotifyApi.getBoolean("is_playing");
                String currentTrack = itemObj.getJsonString("name").getString();
                JsonArray albumArts = itemObj.getJsonObject("album").getJsonArray("images");
                String albumArtUrl;
                if (!albumArts.isEmpty()) {
                    albumArtUrl = albumArts.getJsonObject(0).getJsonString("url").getString();
                }
                else {
                    albumArtUrl = "/images/default-album-art.jpg";
                }
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
