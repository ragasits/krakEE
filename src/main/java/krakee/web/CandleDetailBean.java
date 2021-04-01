/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import org.bson.types.ObjectId;

/**
 * JSF bean for one Candle
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "candleDetailBean")
public class CandleDetailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    LearnEJB learn;
    @EJB
    CandleEJB candle;

    private String selectedIdHexa = null;
    private Date selectedDate;
    private LearnDTO learnDetail = new LearnDTO();
    private boolean insertLearn = false;

    /**
     * get one Candle
     *
     * @return
     */
    public CandleDTO getDetail() {
        if (this.selectedIdHexa == null || this.selectedIdHexa.isEmpty()) {
            return null;
        }
        return candle.get(new ObjectId(selectedIdHexa));
    }

    public Date getSelectedDate() {
        return (Date)selectedDate.clone();
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = (Date)selectedDate.clone();
    }

    /**
     * Get date related candles
     *
     * @return
     */
    public List<CandleDTO> getCandleList() {
        if (selectedDate != null) {
            return candle.getOneDay(selectedDate);
        }
        return null;
    }

    
    /**
     * Name list for p:autoComplete
     * @param query
     * @return 
     */
    public List<String> complete(String query) {
        return learn.getNames();
    }

    /**
     * Is the candle exists?
     * @return 
     */
    public boolean isSelectedCandle() {
        return (this.selectedIdHexa == null || this.selectedIdHexa.isEmpty());
    }

    /**
     * Get Candle related learns
     *
     * @return
     */
    public List<LearnDTO> getLearnList() {
        if (this.getDetail() != null) {
            return learn.get(this.getDetail().getStartDate());
        }
        return null;
    }

    public void showLearnDetail(LearnDTO dto) {
        this.learnDetail = dto;
    }

    /**
     * Create a new Learn
     * @return 
     */
    public String onNewLearn() {
        this.insertLearn = true;
        this.learnDetail = new LearnDTO();
        //this.learnDetail.setStartDate(selectedDate);
        this.learnDetail.setStartDate(this.getDetail().getStartDate());
        return null;
    }


    /**
     * Save a Learn data
     */
    public void onSaveLearn() {
        if (this.insertLearn) {
            learn.add(learnDetail);
        } else {
            learn.update(learnDetail);
        }
    }

    /**
     * Delete a learn data
     */
    public void onDeleteLearn() {
        learn.delete(learnDetail);
    }

    /**
     * Get the minimum date from the Candle
     * @return 
     */
    public Date getMinDate() {
        return candle.getFirstDate();
    }

    /**
     * Get the maximum date from the Candle
     * @return 
     */
    public Date getMaxDate() {
        return candle.getLatesDate();
    }

    public String getSelectedIdHexa() {
        return selectedIdHexa;
    }

    public void setSelectedIdHexa(String selectedIdHexa) {
        this.selectedIdHexa = selectedIdHexa;
    }

    public LearnDTO getLearnDetail() {
        return learnDetail;
    }

    public void setLearnDetail(LearnDTO learnDetail) {
        this.learnDetail = learnDetail;
    }

    public boolean isInsertLearn() {
        return insertLearn;
    }

}
