package krakee.web;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.get.TradeEJB;
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
    TradeEJB trade;

    private List<TradePairDTO> tradeList;
    private int queryLimit = 100;



    /**
     * Get Trade data
     */
    public void onTradeQuery() {
        this.tradeList = trade.getLasts(this.queryLimit);
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

}
