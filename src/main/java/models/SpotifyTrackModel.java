/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javafx.scene.image.Image;

/**
 *
 * @author hasan
 */
public class SpotifyTrackModel {
    
    private String artist;
    private String album;
    private String trackName;
    private Image songArt;
    private long trackLength;
    private long trackProgress;
    private boolean playing;
    
    public SpotifyTrackModel() {
        
    }
    
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public Image getSongArt() {
        return songArt;
    }

    public void setSongArt(Image songArt) {
        this.songArt = songArt;
    }

    public long getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(long trackLength) {
        this.trackLength = trackLength;
    }
    
    public long getTrackProgress() {
        return trackProgress;
    }

    public void setTrackProgress(long trackProgress) {
        this.trackProgress = trackProgress;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
