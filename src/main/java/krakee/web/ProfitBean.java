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
import krakee.profit.ProfitDTO;
import krakee.profit.ProfitEJB;

/**
 * JSF bean for Profit page
 * @author rgt
 */
@SessionScoped
@Named(value = "profitBean")
public class ProfitBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<ProfitDTO> profitList;
    private long testIter=1000;

    @EJB
    ProfitEJB profitEjb;
    
    /**
     * Start Random profit calculation
     */
    public void onRandom(){
        profitEjb.calcProfit(testIter);
        this.onProfitQuery();
    }
    
    /**
     * get All profit
     */
    public void onProfitQuery(){
        this.profitList = profitEjb.get();
    }

    public List<ProfitDTO> getProfitList() {
        return profitList;
    }

    public long getTestIter() {
        return testIter;
    }

    public void setTestIter(long testIter) {
        this.testIter = testIter;
    }
    


}
