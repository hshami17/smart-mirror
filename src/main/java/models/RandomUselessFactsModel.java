/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author hasan
 */
public class RandomUselessFactsModel {
    private String uselessFact;

    public String getUselessFact() {
        return uselessFact;
    }

    public void setUselessFact(String uselessFact) {
        this.uselessFact = uselessFact;
        System.out.println("GOT: " + this.uselessFact);
    }
}
