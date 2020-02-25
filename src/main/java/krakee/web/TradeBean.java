package krakee.web;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import krakee.get.TradePairDTO;

/**
 * JSF bean for Trades
 * @author rgt
 */
@SessionScoped
@Named(value = "tradeBean")
public class TradeBean implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @EJB
    MongoEJB mongo;

    private List<TradePairDTO> tradeList;
    private int queryLimit = 100;

    /**
     * Consistency checking 
     */
    public void onTradeChk() {
        List<String> list = mongo.chkTradePair();
        FacesMessage msg;
        if (list == null || list.isEmpty()) {
            String errorMsg = "Consistency error: " + list.size();
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Trade", errorMsg);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Trade", "Consistency check: OK");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * Get Trade data
     */
    public void onTradeQuery() {
        this.tradeList = mongo.getLastTrades(this.queryLimit);
    }

    public List<TradePairDTO> getTradeList() {
        return tradeList;
    }

    public int getQueryLimit() {
        return queryLimit;
    }

    public void setQueryLimit(int queryLimit) {
        this.queryLimit = queryLimit;
    }

    @Override
    public String toString() {
        return "TradeBean{" + "mongo=" + mongo + ", tradeList=" + tradeList + ", queryLimit=" + queryLimit + '}';
    }
    
    

}
