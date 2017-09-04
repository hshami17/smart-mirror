/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.news;

import api_calls.NewsAPI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.Duration;
import models.ModelManager;
import models.NewsAPIModel;
import models.datatypes.Headline;
import utils.PCM;
import utils.PCS;
import module.ModuleControl;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class NewsAPIController implements Initializable, ModuleControl,
        PropertyChangeListener{

    @FXML
    private ScrollPane newsModule;
    @FXML
    private VBox newsList;
    
    private Timeline autoScroll;
    private double scrollAmount = 0.0;
    private boolean scrollDown = true;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Hide vertical and horizontal scroll bar from ScrollPane
        newsModule.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        newsModule.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Set list to be size of screen
        newsList.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth());
        
        // Auto scroll
        configureAutoScroll();
        
        PCS.INST.addPropertyChangeListener(PCM.NEWS_UPDATE, this);
    }    
    
    private void configureAutoScroll(){        
        // Create auto scroll animation
        autoScroll = new Timeline(
            new KeyFrame(Duration.seconds(0), (ActionEvent actionEvent) -> {
                // Scroll down or up depending on current scroll state
                if (scrollDown){
                    scrollAmount += 0.001;
                }
                else{
                    scrollAmount -= 0.001;
                }  
                
                // Set the scroll value for the scroll pane
                newsModule.setVvalue(scrollAmount);
                
                // Set new scroll state if at top or bottom
                if (scrollAmount >=  1.0){
//                    System.out.println("--- AT BOTTOM, WILL SCROLL UP --- ");
//                    System.out.println("Scroll amount: " + scrollAmount);
                    scrollDown = false;
                    pauseScroll();
                }
                else if (scrollAmount <= 0.0){
//                    System.out.println("--- AT TOP, WILL SCROLL DOWN ---");
//                    System.out.println("Scroll amount: " + scrollAmount);
                    scrollDown = true;
                    pauseScroll();
                }
            }),
            new KeyFrame(Duration.millis(15))
        );
        
        autoScroll.setCycleCount(Animation.INDEFINITE);
        autoScroll.play();
    }
    
    
    private void pauseScroll(){
//        System.out.println("IN PAUSE SCROLL");
        new Thread(() -> {
            try {
//                System.out.println("Pausing scroll...");
                autoScroll.pause();
                Thread.sleep(5000);
//                System.out.println("Playing scroll now...");
                autoScroll.play();
            } catch (InterruptedException ex) {
//                System.out.println("INTERRUPTED EXCEPTION CAUGHT IN NEWS");
                System.out.println(ex.getMessage() + "\n");
            }
        }).start();
    }

    private NewsAPIModel newsAPIModel;
    
    @Override
    public void setModel(ModelManager modelManager) {
        this.newsAPIModel = modelManager.getNewsModel();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
           newsList.getChildren().clear();
           for (String headline : newsAPIModel.getHeadlines()){
               newsList.getChildren().add(new Headline(headline));
           }
       });
    }    

    private boolean apiRunning = false;
    
    @Override
    public void startModule() {
        if (!apiRunning){
            new NewsAPI().start();
            apiRunning = true;
        }
    }

    @Override
    public void stopModule() {
        PCS.INST.firePropertyChange(PCM.STOP_NEWS_API);
        apiRunning = false;
    }
}
