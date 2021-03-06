package krakee.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import krakee.ConfigEJB;
import krakee.MyException;
import krakee.TimerEjb;
import krakee.calc.BollingerEJB;
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
    @EJB
    BollingerEJB bollinger;

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
     * Check Bollinger errors
     */
    public void onBollinger() {
        this.resultList = bollinger.chkBollinger();
        this.showResult("Candle", "Bollinger");
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
        try {
            this.resultList = candle.chkDates();
        } catch (MyException ex) {
            this.showResult("Candle", ex.getMessage());
        }
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
