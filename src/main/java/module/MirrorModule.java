/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module;

import api_calls.API;
import api_calls.CovidAPI;
import api_calls.DarkSkyAPI;
import api_calls.NewsAPI;
import api_calls.RandomFamousQuoteAPI;
import api_calls.UselessFactsAPI;
import api_calls.SpotifyAPI;
import api_calls.WunderlistAPI;

/**
 *
 * @author hasan
 */
public enum MirrorModule {
    DARKSKY("/fxml/DarkSky.fxml", new DarkSkyAPI()),
    CLOCK("/fxml/Clock.fxml"),
    NEWS("/fxml/NewsAPI.fxml", new NewsAPI()),
    WUNDERLIST("/fxml/Wunderlist.fxml", new WunderlistAPI()),
    RANDOM_FAMOUS_QUOTE("/fxml/RandomFamousQuotes.fxml", new RandomFamousQuoteAPI()),
    USELESS_FACTS("/fxml/UselessFacts.fxml", new UselessFactsAPI()),
    SPOTIFY("/fxml/SpotifyPlayer.fxml", new SpotifyAPI()),
    COVID("/fxml/Covid.fxml", new CovidAPI()),
    
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

