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
public interface ModuleController {
    default void update(){}
    default void setModel(ModelManager modelManager){}
    default void displayingModule(){}
    default void removingModule(){}
    default void align(boolean left){}
}
