/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.datatypes;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author hasan
 */
public class Headline extends Label {
    
    public Headline(String text){
        super(text);
        ImageView logo = new ImageView(new Image("/images/newslogo.png"));
        logo.setFitHeight(15);
        logo.setFitWidth(28);
        this.setGraphic(logo);
        this.getStyleClass().add("news");
    }
}
