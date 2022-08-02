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
@Named(value = "profitBean")
public class ProfitBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long selectedTest;
    private String selectedName;

    @EJB
    private ProfitEJB profitEjb;
  
    public void onCalc(){
        profitEjb.calcProfit(selectedName);
    }

    /**
     * get profit list, filter by best
     * @return 
     */
    public List<ProfitItemDTO> getProfitList() {
        if (this.selectedTest!=null){
            return profitEjb.get(selectedTest).getItems();
        }
        return null;
    }
    
    /**
     * Get selected profit
     * @return 
     */
    public ProfitDTO getProfit() {
        if (this.selectedTest!=null){
            return profitEjb.get(selectedTest);
        }
        return null;
    }    

    /**
     * Get best list
     * @return 
     */
    public List<ProfitDTO> getBestList() {
        return profitEjb.get();
    }

    public Long getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(Long selectedTest) {
        this.selectedTest = selectedTest;
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }
}
