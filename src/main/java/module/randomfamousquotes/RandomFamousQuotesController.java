package module.randomfamousquotes;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;
import models.ModelManager;
import models.RandomFamousQuoteModel;
import smartmirror.MirrorViewController;
import utils.PCM;
import utils.PCS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import module.ModuleController;

/**
 * Created by hasan on 5/26/17.
 */
public class RandomFamousQuotesController implements Initializable, ModuleController,
        PropertyChangeListener{

    @FXML
    private Label quote;

    private RandomFamousQuoteModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PCS.INST.addPropertyChangeListener(PCM.FADE_OUT_QUOTE, this);
    }


    @Override
    public void setModel(ModelManager modelManager) {
        model = modelManager.getQuoteModel();
    }

    @Override
    public void displayingModule() {
        quote.setText("");
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()){
            case PCM.FADE_OUT_QUOTE:
                Platform.runLater(() -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), quote);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.play();
                });
                break;
        }
    }
    
    @Override
    public void update() {
        Platform.runLater(() -> {
            quote.setText(model.getQuote());
            if (!MirrorViewController.modulesHidden.get()) {
                FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), quote);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }
        });
    }
}
