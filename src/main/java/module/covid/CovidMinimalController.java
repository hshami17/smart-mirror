/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.covid;

import java.net.URL;
import java.util.ResourceBundle;
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
public class CovidMinimalController implements Initializable, ModuleController {
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
        
        lblTotalCases.setText(covidModel.getTotalCases());
        lblTotalDeaths.setText(covidModel.getTotalDeaths());
        lblTotalRecovered.setText(covidModel.getTotalRecovered());
        lblUSACases.setText(covidModel.getTotalCasesUSA());
        lblUSADeaths.setText(covidModel.getTotalDeathsUSA());
        lblUSARecovered.setText(covidModel.getTotalRecoveredUSA());
    }
}
