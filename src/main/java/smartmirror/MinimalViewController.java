/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartmirror;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

/**
 *
 * @author hasan
 */
public class MinimalViewController implements Initializable {
    
    @FXML
    private Pane minimalClock;
    @FXML
    private Pane minimalWeather;
    @FXML
    private Pane minimalSpotify;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
}
