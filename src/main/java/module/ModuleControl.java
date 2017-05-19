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
    public void setModel(ModelManager modelManager);
    public void startAPI();
    public void stopAPI();
    public void pullAPI();
}
