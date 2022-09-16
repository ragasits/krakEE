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
import javax.inject.Inject;
import javax.inject.Named;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;

/**
 * JSF bean for one Candle
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "learnBean")
public class LearnBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private LearnEJB learn;
    @EJB
    private CandleEJB candle;

    @Inject
    private CandleDetailBean candleBean;

    /**
     * Get all Learn
     *
     * @return
     */
    public List<LearnDTO> getLearnList() {
        return learn.get();
    }

    /**
     * Get Names (Distinct)
     *
     * @return
     */
    public List<String> getLearnNameList() {
        return learn.getNames();
    }

    /**
     * Link to candleDetail
     *
     * @param learn
     * @return
     */
    public String showDetail(LearnDTO learn) {

        if (learn != null) {
            candleBean.setSelectedDate(learn.getStartDate());

            CandleDTO dto = candle.get(learn.getStartDate());
            candleBean.setSelectedIdHexa(dto.getIdHexa());

            return "candleDetail?faces-redirect=true";
        }
        return null;
    }

    //Check1
    public void chkLearnPeaks() {
        learn.chkLearnPeaks();
    }

    //Check2
    public void chkLearnPairs() {
        learn.chkLearnPairs();
    }
}
