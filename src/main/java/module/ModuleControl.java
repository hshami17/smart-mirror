/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module;

import models.ModelManager;

/**
 *
 * @author hasan
 */
public interface ModuleControl {
    void setModel(ModelManager modelManager);
    void startAPI();
    void stopAPI();
    default void align(boolean left){}
}
