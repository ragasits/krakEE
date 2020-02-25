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
import org.bson.types.ObjectId;

/**
 * JSF bean for one Canlde
 * @author rgt
 */
@SessionScoped
@Named(value = "candleDetailBean")
public class CandleDetailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    MongoEJB mongo;

    private String selectedIdHexa;
    private Date selectedDate;

    /**
     * get one Candle
     * @return 
     */
    public CandleDTO getDetail() {
        if (this.selectedIdHexa==null || this.selectedIdHexa.isEmpty()){
            return null;
        }
        return mongo.getCandle(new ObjectId(selectedIdHexa));
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    /**
     * Get date related candles
     * @return 
     */
    public List<CandleDTO> getCandleList() {
        if (selectedDate != null) {
            return mongo.getCandleOneDayCandles(selectedDate);
        }
        return null;
    }

    public Date getMinDate() {
        return mongo.getFirstDateFromCandle();
    }

    public Date getMaxDate() {
        return mongo.getLatesDateFromCandle();
    }

    public String getSelectedIdHexa() {
        return selectedIdHexa;
    }

    public void setSelectedIdHexa(String selectedIdHexa) {
        this.selectedIdHexa = selectedIdHexa;
    }
}
