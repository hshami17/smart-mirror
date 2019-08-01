/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.darksky;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import models.DarkSkyModel;
import models.ModelManager;
import module.ModuleController;

/**
 *
 * @author hasan
 */
public class DarkSkyMinimalController implements Initializable, ModuleController {
    
    @FXML
    private Label lblCurrentTemp;
    @FXML
    private ImageView imgWeatherIcon;
    @FXML
    private Label lblCurrentWeather;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblCurrentTemp.setText("60\u00b0");
    }
    
    @Override
    public void update() {
        DarkSkyModel darkSkyModel = ModelManager.INST.getWeatherModel();
        Platform.runLater(() -> {
            lblCurrentTemp.setText(String.valueOf(darkSkyModel.getCurrentTemp()) + "\u00b0");
            imgWeatherIcon.setImage(darkSkyModel.getCurrentIcon());
            lblCurrentWeather.setText(darkSkyModel.getCurrentSummary());
        });
    }
}
