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
     * Show check message
     *
     * @param type
     * @param message
     */
    private void showResult(String type, String message) {
        FacesMessage msg;

        if (this.resultList.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, type, message + ": OK");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", message + ": Errors(" + this.resultList.size() + ")");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Check Open=0 missing trades
     */
    public void onZeroOpen() {
        this.resultList = candle.chkZeroOpen();
        this.showResult("Candle", "Open=0 check");
    }

    /**
     * Check Candle count consistency
     */
    public void onCandleTradeCountChk() {
        this.resultList = candle.chkTradeCount();
        this.showResult("Candle", "TradeCnt check");
    }

    /**
     * Check Candle date consistency
     */
    public void onDateChk() {
        this.resultList = candle.chkDates();
        this.showResult("Candle", "Date check");
    }

    /**
     * Consistency checking
     */
    public void onTradeChk() {
        this.resultList = trade.chkTradePair();
        this.showResult("Trade", "Consistency check");
    }

    /**
     * Search for duplicates
     */
    public void onTradeDuplicatesChk() {
        this.resultList = trade.chkTradeDuplicates();
        this.showResult("Trade", "Search for duplicates");
    }

    /**
     * Compare old-new trades
     */
    public void onCompareTrades() {
        this.resultList = trade.chkCompareTrades1();
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

    public String getResultText() {
        if (this.resultList == null || this.resultList.isEmpty()) {
            return "Result";
        }
        return "Result (" + this.resultList.size() + ")";
    }

}
