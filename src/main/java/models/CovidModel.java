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
public class CovidModel {
    
    private String totalCases;
    private String totalDeaths;
    private String totalRecovered;
    private String totalCasesUSA;
    private String totalDeathsUSA;
    private String totalRecoveredUSA;
    
    public String getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(String totalCases) {
        this.totalCases = totalCases;
    }

    public String getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(String totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public String getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(String totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    public String getTotalCasesUSA() {
        return totalCasesUSA;
    }

    public void setTotalCasesUSA(String totalCasesUSA) {
        this.totalCasesUSA = totalCasesUSA;
    }

    public String getTotalDeathsUSA() {
        return totalDeathsUSA;
    }

    public void setTotalDeathsUSA(String totalDeathsUSA) {
        this.totalDeathsUSA = totalDeathsUSA;
    }

    public String getTotalRecoveredUSA() {
        return totalRecoveredUSA;
    }

    public void setTotalRecoveredUSA(String totalRecoveredUSA) {
        this.totalRecoveredUSA = totalRecoveredUSA;
    }  
}
