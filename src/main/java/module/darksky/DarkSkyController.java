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
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import models.DarkSkyModel;
import models.ModelManager;
import module.ModuleController;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class DarkSkyController implements Initializable, ModuleController {

    @FXML
    private Label temperature;
    @FXML
    private ImageView weatherIcon;
    @FXML
    private Label weeklySummary;
    @FXML
    private Label dailySummary;
    @FXML
    private HBox forecastList;
    @FXML
    private VBox days;
    @FXML
    private VBox icons;
    @FXML
    private VBox highs;
    @FXML
    private VBox lows;
    @FXML
    private Label location;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    
    @Override
    public void update() {
        DarkSkyModel darkSkyModel = ModelManager.INST.getWeatherModel();
        Platform.runLater(() -> {
            temperature.setText(darkSkyModel.getCurrentTemp().toString() + "\u00b0");
            weatherIcon.setImage(darkSkyModel.getCurrentIcon());
            location.setText(darkSkyModel.getLocation());
            dailySummary.setText(darkSkyModel.getCurrentSummary());
            weeklySummary.setText(darkSkyModel.getWeeklySummary());
            // Clear the previous forecast pull
            days.getChildren().clear();
            icons.getChildren().clear();
            highs.getChildren().clear();
            lows.getChildren().clear();         
            // Loop through forecast and add to HBox
            darkSkyModel.getForecastList().forEach((forecast) -> {
                Label day = new Label(forecast.getDay());
                day.getStyleClass().add("forecast");

                days.getChildren().add(day);

                ImageView icon = new ImageView(forecast.getIcon());
                icon.setFitHeight(20);
                icon.setFitWidth(20);
                icon.setPreserveRatio(true);
                icon.setSmooth(true);
                icon.setTranslateY(2d);

                icons.getChildren().add(icon);

                Label high = new Label(forecast.getHigh().toString());
                Label low = new Label(forecast.getLow().toString());
                high.getStyleClass().add("forecast");
                low.getStyleClass().add("forecast");

                highs.getChildren().add(high);
                lows.getChildren().add(low);
            });
        });
    }
}
