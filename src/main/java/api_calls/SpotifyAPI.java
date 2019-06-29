/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_calls;

import java.io.IOException;
import utils.PCM;

/**
 *
 * @author hasan
 */
public class SpotifyAPI extends APIManager {

    public SpotifyAPI() {
        super(1, PCM.PULL_SPOTIFY);
    }

    @Override
    protected void fetch() throws IOException {
        System.out.println("DOING SPOTIFY FETCH...");
    }
}
