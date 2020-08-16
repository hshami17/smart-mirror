/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module;

import api_calls.API;
import api_calls.Covid;
import api_calls.DarkSky;
import api_calls.News;
import api_calls.RandomFamousQuote;
import api_calls.UselessFacts;
import api_calls.Spotify;
import api_calls.Wunderlist;

/**
 *
 * @author hasan
 */
public enum MirrorModule {
    DARKSKY("/fxml/DarkSky.fxml", new DarkSky()),
    CLOCK("/fxml/Clock.fxml"),
    NEWS("/fxml/NewsAPI.fxml", new News()),
    WUNDERLIST("/fxml/Wunderlist.fxml", new Wunderlist()),
    RANDOM_FAMOUS_QUOTE("/fxml/RandomFamousQuotes.fxml", new RandomFamousQuote()),
    USELESS_FACTS("/fxml/UselessFacts.fxml", new UselessFacts()),
    SPOTIFY("/fxml/SpotifyPlayer.fxml", new Spotify()),
    COVID("/fxml/Covid.fxml", new Covid()),
    
    // Minimal modules
    DARKSKY_MINIMAL("/fxml/minimal/DarkSkyMinimal.fxml"),
    CLOCK_MINIMAL("/fxml/minimal/ClockMinimal.fxml"),
    SPOTIFY_MINIMAL("/fxml/minimal/SpotifyMinimal.fxml"),
    COVID_MINIMAL("/fxml/minimal/CovidMinimal.fxml");
    
    private final String fxml;
    private final API api;
    
    MirrorModule(String fxml, API api) {
        this.fxml = fxml;
        this.api = api;
    }

    MirrorModule(String fxml) {
        this(fxml, null);
    }
    
    public String getFxml() {
        return fxml;
    }
    
    public API getApi() {
        return api;
    }
}

