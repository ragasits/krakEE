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
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "profitBean")
public class ProfitBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<ProfitDTO> profitList;

    @EJB
    ProfitEJB profitEjb;
    
    public void onRandom(){
        profitEjb.calcProfit();
        this.onProfitQuery();
    }
    
    public void onProfitQuery(){
        this.profitList = profitEjb.get();
    }

    public List<ProfitDTO> getProfitList() {
        return profitList;
    }
    


}
