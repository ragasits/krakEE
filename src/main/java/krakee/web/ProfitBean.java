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
import krakee.profit.ProfitItemDTO;
import krakee.profit.ProfitDTO;
import krakee.profit.ProfitEJB;

/**
 * JSF bean for Profit page
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "profit1Bean")
public class ProfitBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long selectedTest;

    @EJB
    private ProfitEJB profit1Ejb;

    /**
     * Start Random profit calculation
     */
    public void onRandom() {
        profit1Ejb.calcProfit();
    }

    /**
     * get profit list, filter by best
     * @return 
     */
    public List<ProfitItemDTO> getProfitList() {
        if (this.selectedTest!=null){
            return profit1Ejb.get(selectedTest).getItems();
        }
        return null;
    }

    /**
     * Get best list
     * @return 
     */
    public List<ProfitDTO> getBestList() {
        return profit1Ejb.get();
    }

    public Long getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(Long selectedTest) {
        this.selectedTest = selectedTest;
    }
}
