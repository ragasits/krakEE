/*
 * Copyright (C) 2021 Ragasits Csaba
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package krakee.web;

import java.io.Serializable;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
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
