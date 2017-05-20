/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author hasan
 */
public class PCS {
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public void addPropertyChangeListener(String propName, PropertyChangeListener listener){
        pcs.addPropertyChangeListener(propName, listener);
    }
    
    public void firePropertyChange(String propName){
        pcs.firePropertyChange(propName, null, null);
    }
    
    public void firePropertyChange(String propName, Object newValue){
        pcs.firePropertyChange(propName, null, newValue);
    }
    
    public static PCS INST = new PCS();
}
