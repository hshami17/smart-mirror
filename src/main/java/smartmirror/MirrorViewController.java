package smartmirror;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import models.ModelManager;
import models.RandomFamousQuoteModel;
import module.Module;
import utils.Config;
import utils.PCM;
import utils.PCS;

/**
 * Mirror View controller class
 *
 * @author hasan
 */
public class MirrorViewController implements Initializable, PropertyChangeListener {
    
    private boolean modulesHidden = false;
    
    // Module containers
    @FXML
    private Pane topRight;
    @FXML
    private Pane topLeft;
    @FXML
    private Pane bottomLeft;
    @FXML
    private Pane bottomRight;
    @FXML
    private Pane bottom;
    
    @FXML
    private TextArea alertPrompt;
    @FXML
    private Button hideButton;
    
    private FadeTransition buttonFadeInOut;
    private Thread alertBoxThread = new Thread();
    
    // Quote module
    @FXML
    private Label quoteofday;
    @FXML
    private Separator separator;
    private RandomFamousQuoteModel quoteModel; 
    
    @FXML
    private void hideShowButtonPressed(ActionEvent event){
        hideShowAllModules();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
       switch(evt.getPropertyName()){
           case PCM.QUOTE_UPDATE:
                Platform.runLater(() ->{
                quoteofday.setText(quoteModel.getQuote());
                if (!modulesHidden){
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), quoteofday);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                }
                });
               break;
           case PCM.FADE_OUT_QUOTE:
                Platform.runLater(() ->{
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), quoteofday);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.play();    
                });
               break;
           case PCM.ALERT:
               alert((String) evt.getNewValue());
               break;
       }
    }
    
    private void alert(String message){
        alertPrompt.setText(message);
        if (!alertBoxThread.isAlive()){
            alertBoxThread = new Thread(() -> {
                try {
                    FadeTransition promptFade = new FadeTransition(new Duration(500), alertPrompt);
                    promptFade.setFromValue(alertPrompt.getOpacity() == 1.0 ? 1.0 : 0.0);
                    promptFade.setToValue(alertPrompt.getOpacity() == 1.0 ? 0.0 : 1.0);
                    promptFade.play();

                    Thread.sleep(8000);

                    promptFade.setFromValue(alertPrompt.getOpacity() == 1.0 ? 1.0 : 0.0);
                    promptFade.setToValue(alertPrompt.getOpacity() == 1.0 ? 0.0 : 1.0);
                    promptFade.play();
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(MirrorViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            });
            alertBoxThread.start();
        }
    }
    
    private void hideShowAllModules(){
        Duration duration = new Duration(!modulesHidden ? 500 : 1000);
        FadeTransition quoteFade = new FadeTransition(duration, quoteofday);
        quoteFade.setFromValue(!modulesHidden ? 1.0 : 0.0);
        quoteFade.setToValue(!modulesHidden ? 0.0 : 1.0);

        FadeTransition sepFade = new FadeTransition(duration, separator);
        sepFade.setFromValue(!modulesHidden ? 1.0 : 0.0);
        sepFade.setToValue(!modulesHidden ? 0.0 : 1.0);

        FadeTransition topLeftFade = new FadeTransition(duration, topLeft);
        topLeftFade.setFromValue(!modulesHidden ? 1.0 : 0.0);
        topLeftFade.setToValue(!modulesHidden ? 0.0 : 1.0);

        FadeTransition topRightFade = new FadeTransition(duration, topRight);
        topRightFade.setFromValue(!modulesHidden ? 1.0 : 0.0);
        topRightFade.setToValue(!modulesHidden ? 0.0 : 1.0);

        FadeTransition bottomLeftFade = new FadeTransition(duration, bottomLeft);
        bottomLeftFade.setFromValue(!modulesHidden ? 1.0 : 0.0);
        bottomLeftFade.setToValue(!modulesHidden ? 0.0 : 1.0);
        
        FadeTransition bottomRightFade = new FadeTransition(duration, bottomRight);
        bottomRightFade.setFromValue(!modulesHidden ? 1.0 : 0.0);
        bottomRightFade.setToValue(!modulesHidden ? 0.0 : 1.0);

        FadeTransition bottomFade = new FadeTransition(duration, bottom);
        bottomFade.setFromValue(!modulesHidden ? 1.0 : 0.0);
        bottomFade.setToValue(!modulesHidden ? 0.0 : 1.0);

        quoteFade.play();
        sepFade.play();
        topLeftFade.play();
        topRightFade.play();
        bottomLeftFade.play();
        bottomRightFade.play();
        bottomFade.play();

        buttonFadeInOut.setFromValue(1.0);
        buttonFadeInOut.setToValue(0.1);
        buttonFadeInOut.setCycleCount(FadeTransition.INDEFINITE);
        buttonFadeInOut.setAutoReverse(true);

        modulesHidden = !modulesHidden;

        if (modulesHidden){
            buttonFadeInOut.play();
        }
        else{
            buttonFadeInOut.stop();
            buttonFadeInOut.setFromValue(hideButton.getOpacity());
            buttonFadeInOut.setToValue(1.0);
            buttonFadeInOut.setCycleCount(1);
            buttonFadeInOut.setAutoReverse(false);
            buttonFadeInOut.play();
        }
    }
    
    private void configureModuleContainers(){
        // Manipulate pref width & height depending on
        // what module is placed in the container
        
        // Top Right
        
        
        // Top Left
        double[] dimensions = ((Module) topLeft.getChildren().get(0)).getDimensions();
        topLeft.setPrefWidth(dimensions[0]);
        topLeft.setPrefHeight(dimensions[1]);
        
        
        // Bottom Right
        
        // Bottom Left
    }
    
    public void clearAllContainers(){
        Platform.runLater(() -> {
            topRight.getChildren().clear();
            topLeft.getChildren().clear();
            bottomRight.getChildren().clear();
            bottomLeft.getChildren().clear();
            bottom.getChildren().clear();
        });
    }
    
    public void placeModules(){
        Platform.runLater(() -> {
            if (Config.getTopRightMod() != null)
                topRight.getChildren().setAll(Config.getTopRightMod());
            if (Config.getTopLeftMod() != null)
                topLeft.getChildren().setAll(Config.getTopLeftMod()); 
            if (Config.getBottomRightMod() != null)
                bottomRight.getChildren().setAll(Config.getBottomRightMod());
            if (Config.getBottomLeftMod() != null)
                bottomLeft.getChildren().setAll(Config.getBottomLeftMod());
            if (Config.getBottomMod() != null)
                bottom.getChildren().setAll(Config.getBottomMod());
        });
    }
        
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        alertPrompt.setOpacity(0);
        
        quoteModel = ModelManager.INST.getQuoteModel();
        buttonFadeInOut = new FadeTransition(Duration.millis(1000), hideButton);
        
        PCS.INST.addPropertyChangeListener(PCM.QUOTE_UPDATE, this);
        PCS.INST.addPropertyChangeListener(PCM.FADE_OUT_QUOTE, this);
        PCS.INST.addPropertyChangeListener(PCM.ALERT, this);
        
        placeModules();
        //configureModuleContainers();
    }
}
