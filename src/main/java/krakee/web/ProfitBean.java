/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.web;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.profit.ProfitBestDTO;
import krakee.profit.ProfitBestEJB;
import krakee.profit.ProfitDTO;
import krakee.profit.ProfitEJB;

/**
 * JSF bean for Profit page
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "profitBean")
public class ProfitBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long selectedTest;

    @EJB
    private ProfitEJB profitEjb;
    @EJB
    private ProfitBestEJB bestEjb;

    /**
     * Start Random profit calculation
     */
    public void onRandom() {
        profitEjb.calcProfit();
    }

    public List<ProfitDTO> getProfitList() {
        if (this.selectedTest!=null){
            return profitEjb.get(selectedTest);
        }
        return null;
    }

    public List<ProfitBestDTO> getBestList() {
        return bestEjb.get();
    }

    public Long getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(Long selectedTest) {
        this.selectedTest = selectedTest;
    }
    
    

}
