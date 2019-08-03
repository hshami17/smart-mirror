/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.uselessfacts;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import models.ModelManager;
import models.UselessFactsModel;
import module.ModuleController;

/**
 *
 * @author hasan
 */
public class UselessFactsController implements Initializable, ModuleController {
    
    @FXML
    private HBox hbxContainer;
    @FXML
    private Label lblUselessFact;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblUselessFact.setText("");
    }
    
    @Override
    public void update() {
        UselessFactsModel uselessFactsModel = ModelManager.INST.getUselessFactsModel();
        Platform.runLater(() -> {
            lblUselessFact.setText(uselessFactsModel.getUselessFact());
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), lblUselessFact);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }
}
