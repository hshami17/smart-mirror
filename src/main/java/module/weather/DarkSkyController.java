/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.weather;

import api_calls.DarkSkyAPI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import models.DarkSkyModel;
import models.ModelManager;
import models.datatypes.Forecast;
import utils.PCM;
import utils.PCS;
import module.ModuleControl;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class DarkSkyController implements Initializable, ModuleControl, 
        PropertyChangeListener {

    @FXML
    private Pane weatherModule;
    @FXML
    private Label temperature;
    @FXML
    private ImageView weatherIcon;
    @FXML
    private TextArea weeklySummary;
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
        PCS.INST.addPropertyChangeListener(PCM.WEATHER_UPDATE, this);
    }    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            temperature.setText(darkSkyModel.getCurrentTemp().toString() + "\u00b0");
            weatherIcon.setImage(new Image(darkSkyModel.getCurrentIcon().getPath()));
            location.setText(darkSkyModel.getLocation());
            dailySummary.setText(darkSkyModel.getCurrentSummary());
            weeklySummary.setText(darkSkyModel.getWeeklySummary());
            // Clear the previous forecast pull
            days.getChildren().clear();
            icons.getChildren().clear();
            highs.getChildren().clear();
            lows.getChildren().clear();         
            // Loop through forecast and add to HBox
            for (Forecast forecast : darkSkyModel.getForecastList()){

                Label day = new Label(forecast.getDay());
                day.getStyleClass().add("forecast");

                days.getChildren().add(day);

                ImageView icon = new ImageView(forecast.getIcon().getPath());
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
            }
        });
    }
    
    private DarkSkyModel darkSkyModel;
    
    @Override
    public void setModel(ModelManager modelManager) {
        this.darkSkyModel = modelManager.getWeatherModel();
    }
    
    private boolean apiRunning = false;

    @Override
    public void startModule() {
        if (!apiRunning){
            new DarkSkyAPI().start();
            apiRunning = true;
        }
    }

    @Override
    public void stopModule() {
        PCS.INST.firePropertyChange(PCM.STOP_WEATHER_API);
        apiRunning = false;
    }
}
