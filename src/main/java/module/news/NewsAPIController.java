/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.news;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.Duration;
import models.ModelManager;
import models.NewsAPIModel;
import models.datatypes.Headline;
import module.ModuleController;

/**
 * FXML Controller class
 *
 * @author hasan
 */
public class NewsAPIController implements Initializable, ModuleController {

    @FXML
    private ScrollPane newsModule;
    @FXML
    private VBox newsList;
    
    private Timeline autoScroll;
    private double scrollAmount = 0.0;
    private boolean scrollDown = true;
    
    private final List<Headline> currentHeadlines = new ArrayList<>();

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
        
        // Scroll fade code provided by StackOverflow user: TM00
//        configureScrollFade();
    }  
    
    private final double FADE_THRESHOLD = 90;
    private void configureScrollFade() {
        newsModule.vvalueProperty().addListener(new ChangeListener<Number>() 
            {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
                {
                    currentHeadlines.forEach((l) -> {
                        Bounds bounds = getVisibleBounds(l);
                        if (bounds == null) return;
                        double minY = bounds.getMinY();
                        double maxY = bounds.getMaxY();

                        if(Math.abs(minY) < FADE_THRESHOLD ) {
                            l.setOpacity(1 - (FADE_THRESHOLD - Math.abs(minY)) / FADE_THRESHOLD);
                        }
                        else if(Math.abs(maxY) < FADE_THRESHOLD) {
                            l.setOpacity(1 - (FADE_THRESHOLD - Math.abs(maxY)) / FADE_THRESHOLD);
                        }
                        else {
                            l.setOpacity(1);
                        }
                    });
                }
            });
    }

    private Bounds getVisibleBounds(Node aNode) {
        // If node not visible, return empty bounds
        if(!aNode.isVisible()) return new BoundingBox(0,0,-1,-1);

        // If node has clip, return clip bounds in node coords
        if(aNode.getClip()!=null) return aNode.getClip().getBoundsInParent();

        // If node has parent, get parent visible bounds in node coords
        Bounds bounds = aNode.getParent()!=null? getVisibleBounds(aNode.getParent()) : null;
        if(bounds!=null && !bounds.isEmpty()) bounds = aNode.parentToLocal(bounds);
        return bounds;
    }
    
    private void configureAutoScroll(){        
        // Create auto scroll animation
        autoScroll = new Timeline(
            new KeyFrame(Duration.seconds(0), (ActionEvent actionEvent) -> {
                // Scroll down or up depending on current scroll state
                if (scrollDown){
                    scrollAmount += 0.0006;
                }
                else{
                    scrollAmount -= 0.0006;
                }  
                
                // Set the scroll value for the scroll pane
                newsModule.setVvalue(scrollAmount);
                
                // Set new scroll state if at top or bottom
                if (scrollAmount >=  1.0){
                    scrollDown = false;
                    pauseScroll();
                }
                else if (scrollAmount <= 0.0){
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
        new Thread(() -> {
            try {
                autoScroll.pause();
                Thread.sleep(5000);
                autoScroll.play();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        }).start();
    }

    private NewsAPIModel newsAPIModel;
    
    @Override
    public void setModel(ModelManager modelManager) {
        this.newsAPIModel = modelManager.getNewsModel();
    }
    
    public void addPadding() {
        for(int i=0; i<2; i++) {
            newsList.getChildren().add(new Label());
        }
    }
    
    @Override
    public void update() {
        Platform.runLater(() -> {
           newsList.getChildren().clear();
           currentHeadlines.clear();
           for (String headline : newsAPIModel.getHeadlines()){
               Headline h = new Headline(headline);
               newsList.getChildren().add(h);
               currentHeadlines.add(h);
           }
       });
    }
}
