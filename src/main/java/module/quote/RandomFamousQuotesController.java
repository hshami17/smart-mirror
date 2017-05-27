package module.quote;

import api_calls.RandomFamousQuoteAPI;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;
import models.ModelManager;
import models.RandomFamousQuoteModel;
import module.ModuleControl;
import smartmirror.MirrorViewController;
import utils.PCM;
import utils.PCS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by hasan on 5/26/17.
 */
public class RandomFamousQuotesController implements Initializable, ModuleControl,
        PropertyChangeListener{

    @FXML
    private Label quote;

    private RandomFamousQuoteModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PCS.INST.addPropertyChangeListener(PCM.QUOTE_UPDATE, this);
        PCS.INST.addPropertyChangeListener(PCM.FADE_OUT_QUOTE, this);
        PCS.INST.addPropertyChangeListener(PCM.ALERT, this);
    }


    @Override
    public void setModel(ModelManager modelManager) {
        model = modelManager.getQuoteModel();
    }

    private boolean apiRunning = false;

    @Override
    public void startAPI() {
        if (!apiRunning){
            quote.setText("");
            new RandomFamousQuoteAPI().start();
            apiRunning = true;
        }
    }

    @Override
    public void stopAPI() {
        PCS.INST.firePropertyChange(PCM.STOP_QUOTE_API);
        apiRunning = false;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()){
            case PCM.QUOTE_UPDATE:
                Platform.runLater(() -> {
                    quote.setText(model.getQuote());
                    if (!MirrorViewController.modulesHidden) {
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), quote);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    }
                });
                break;
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
}
