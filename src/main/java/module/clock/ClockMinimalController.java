/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clock;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author hasan
 */
public class ClockMinimalController implements Initializable {
    
    @FXML
    private Label lblTime;
    @FXML
    private Label lblDate;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ClockController.hookDateTimeControls(lblTime, lblDate);
    }
}
