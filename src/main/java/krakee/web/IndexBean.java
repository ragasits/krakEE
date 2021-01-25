package krakee.web;

import java.io.Serializable;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import krakee.ConfigEJB;
import krakee.TimerEjb;
import krakee.calc.CandleEJB;
import krakee.get.TradeEJB;

/**
 * JSF bean for Index page
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "indexBean")
public class IndexBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<String> resultList;

    @EJB
    ConfigEJB config;
    @EJB
    TimerEjb timer;
    @EJB
    TradeEJB trade;
    @EJB
    CandleEJB candle;

    /**
     * Check Open=0 missing trades
     */
    public void onZeroOpen() {
        FacesMessage msg;

        this.resultList = candle.chkZeroOpen();
        if (this.resultList.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", "Open=0 check: OK");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", "Open=0 check: Errors(" + this.resultList.size() + ")");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Check Candle count consistency
     */
    public void onCandleTradeCountChk() {
        FacesMessage msg;

        this.resultList = candle.chkTradeCount();
        if (this.resultList.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", "TradeCnt check: OK");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", "TradeCnt check: Errors(" + this.resultList.size() + ")");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Check Candle date consistency
     */
    public void onDateChk() {
        FacesMessage msg;

        this.resultList = candle.chkDates();
        if (this.resultList.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", "Date check: OK");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", "Date check: Errors(" + this.resultList.size() + ")");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Consistency checking
     */
    public void onTradeChk() {
        this.resultList = trade.chkTradePair();
        FacesMessage msg;
        if (this.resultList.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Trade", "Consistency check: OK");
        } else {
            String errorMsg = "Consistency error: " + this.resultList.size();
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Trade", errorMsg);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean isRunTrade() {
        return config.isRunTrade();
    }

    public boolean isRunCandle() {
        return config.isRunCandle();
    }

    public long getTimerDuration() {
        return timer.getDuration();
    }

    public ArrayList<String> getResultList() {
        return resultList;
    }
    
    public String getResultText(){
        if (this.resultList==null || this.resultList.isEmpty()){
            return "Result";
        }
        return "Result ("+this.resultList.size()+")";
    }

}
