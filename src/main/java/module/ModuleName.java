/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module;

import api_calls.APIManager;
import api_calls.DarkSkyAPI;
import api_calls.NewsAPI;
import api_calls.RandomFamousQuoteAPI;
import api_calls.RandomUselessFacts;
import api_calls.SpotifyAPI;
import api_calls.WunderlistAPI;

/**
 *
 * @author hasan
 */
public enum ModuleName {
    DARKSKY("/fxml/DarkSky.fxml", new DarkSkyAPI()),
    CLOCK("/fxml/Clock.fxml", null),
    NEWS("/fxml/NewsAPI.fxml", new NewsAPI()),
    WUNDERLIST("/fxml/Wunderlist.fxml", new WunderlistAPI()),
    RANDOM_FAMOUS_QUOTE("/fxml/RandomFamousQuotes.fxml", new RandomFamousQuoteAPI()),
    RANDOM_USELESS_FACTS("/fxml/RandomUselessFacts.fxml", new RandomUselessFacts()),
    SPOTIFY("/fxml/SpotifyPlayer.fxml", new SpotifyAPI()),
    
    // Minimal modules
    DARKSKY_MINIMAL("/fxml/DarkSkyMinimal.fxml");
    
    private final String fxml;
    private final APIManager api;
    ModuleName(String fxml, APIManager api) {
        this.fxml = fxml;
        this.api = api;
    }

    ModuleName(String fxml) {
        this(fxml, null);
    }
    
    public String getFxml() {
        return fxml;
    }
    
    public APIManager getApi() {
        return api;
    }
}

