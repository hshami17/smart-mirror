/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * All configuration elements parsed from the XML for each module are stored here
 * @author hasan
 */
public final class ConfigElements {
    // Dark Sky
    public static final StringProperty darkskyKey = new SimpleStringProperty("");
    public static final StringProperty zipcodeKey = new SimpleStringProperty("");
    public static final StringProperty zipcode = new SimpleStringProperty("");
    
    // Random Famous Quote
    public static final StringProperty randomFamousQuoteKey = new SimpleStringProperty(""); 
    public static final StringProperty category = new SimpleStringProperty("");
    
    // Wunderlist
    public static final StringProperty wunderlistKey = new SimpleStringProperty("");
    public static final StringProperty wunderlistClientID = new SimpleStringProperty("");
    public static final StringProperty listID = new SimpleStringProperty("");
    
    // News API
    public static final StringProperty newsKey = new SimpleStringProperty("");
    public static final StringProperty newsSource = new SimpleStringProperty("");
    public static final StringProperty newsSortBy = new SimpleStringProperty("");
}
