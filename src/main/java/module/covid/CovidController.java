/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.covid;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import models.CovidModel;
import models.ModelManager;
import module.ModuleController;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class CovidController implements Initializable, ModuleController {
    @FXML
    private Label lblTotalCases;
    @FXML
    private Label lblTotalDeaths;
    @FXML
    private Label lblTotalRecovered;
    @FXML
    private Label lblUSACases;
    @FXML
    private Label lblUSADeaths;
    @FXML
    private Label lblUSARecovered;
    @FXML
    private Label lblLastUpdated;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void update() {
        CovidModel covidModel = ModelManager.INST.getCovidModel();
        
        Platform.runLater(() -> {
            lblTotalCases.setText(covidModel.getTotalCases());
            lblTotalDeaths.setText(covidModel.getTotalDeaths());
            lblTotalRecovered.setText(covidModel.getTotalRecovered());
            lblUSACases.setText(covidModel.getTotalCasesUSA());
            lblUSADeaths.setText(covidModel.getTotalDeathsUSA());
            lblUSARecovered.setText(covidModel.getTotalRecoveredUSA());      
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("America/New_York"));
            lblLastUpdated.setText(dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")));
        });
    }
}
