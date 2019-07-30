/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.darksky;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import models.DarkSkyModel;
import models.ModelManager;
import module.Module;
import module.ModuleController;
import module.ModuleName;
import utils.Config;

/**
 *
 * @author hasan
 */
public class DarkSkyMinimalController implements Initializable, ModuleController {
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    
    @Override
    public void update() {
        DarkSkyModel darkSkyModel = ModelManager.INST.getWeatherModel();
        System.out.println("GOT WEATHER UPDATE, TEMP IS: " + darkSkyModel.getCurrentTemp());
    }
}
