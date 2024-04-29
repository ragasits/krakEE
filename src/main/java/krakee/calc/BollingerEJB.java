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
package krakee.calc;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.ne;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import krakee.Common;
import krakee.ConfigEJB;

/**
 * Calculate Bollinger values
 *
 * @author rgt
 */
@Stateless
public class BollingerEJB {

    @EJB
    ConfigEJB configEjb;

    /**
     * Calculate Bollinger values + Delta + Trend
     *
     */
    public void calculateBollinger() {
        List<CandleDTO> candleList;
        BollingerDTO bollinger;

        //Get the candles
        candleList = configEjb.getCandleColl()
                .find(and(eq("calcCandle", true), eq("bollinger.calcBollinger", false)))
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            bollinger = candle.getBollinger();
            bollinger.setCalcBollinger(true);
            bollinger.setStDev(this.calcStDev(candle));

            bollinger.setBollingerUpper(candle.getMovingAverage().getSma20().add(bollinger.getStDev().multiply(BigDecimal.valueOf(2))));
            bollinger.setBollingerLower(candle.getMovingAverage().getSma20().subtract(bollinger.getStDev().multiply(BigDecimal.valueOf(2))));

            bollinger.setBollingerBandWidth(bollinger.getBollingerUpper().subtract(bollinger.getBollingerLower()));

            // Calculate trade upper value
            bollinger.setTradeUpper(calcTradeUpper(candle.getClose(), bollinger.getBollingerUpper()));

            //Calculate trade lower value
            bollinger.setTradeLower(calcTradeLower(candle.getClose(), bollinger.getBollingerLower()));

            //Calculate Buy / Sell
            bollinger.setBollingerSell(bollinger.getTradeUpper().compareTo(BigDecimal.ZERO) != 0);
            bollinger.setBollingerBuy(bollinger.getTradeLower().compareTo(BigDecimal.ZERO) != 0);

            //Save candle
            configEjb.getCandleColl().replaceOne(eq("_id", candle.getId()), candle);

        }
    }

    //Calculate trade lower value
    private BigDecimal calcTradeLower(BigDecimal close, BigDecimal lower) {
        if (close.compareTo(lower) < 0) {
            return lower.subtract(close);
        } else {
            return BigDecimal.ZERO;
        }
    }

    // Calculate trade upper value
    private BigDecimal calcTradeUpper(BigDecimal close, BigDecimal upper) {
        if (close.compareTo(upper) > 0) {
            return close.subtract(upper);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate Standard Deviation
     *
     * @param dto
     * @return
     */
    private BigDecimal calcStDev(CandleDTO dto) {
        BigDecimal stdev = BigDecimal.ZERO;
        int i = 0;

        //Get the candles
        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(lte("startDate", dto.getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(20)
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            //Standard Deviation
            stdev = stdev.add(candle.getClose().subtract(dto.getMovingAverage().getSma20()).pow(2));
            i++;
        }

        if (i > 0) {
            return Common.sqrt(stdev.divide(BigDecimal.valueOf(i), 5, RoundingMode.HALF_UP), 2);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Check Bollinger calculation - Trade upper + lower in the same row -
     * Upper, Lower calculation error
     *
     * @return
     */
    public List<String> chkBollinger() {
        ArrayList<String> list = new ArrayList<>();
        List<CandleDTO> candleList;

        //Trade upper + lower in the same row
        candleList = configEjb.getCandleColl()
                .find(
                        and(
                                ne("bollinger.tradeUpper", 0),
                                ne("bollinger.tradeLower", 0)
                        )
                )
                .into(new ArrayList<>());

        if (!candleList.isEmpty()) {
            list.add("tradeUpper + tradeLower");
        }

        //Upper, Lower calculation error
        candleList = configEjb.getCandleColl()
                .find()
                .into(new ArrayList<>());

        for (CandleDTO dto : candleList) {
            BigDecimal upper = calcTradeUpper(dto.getClose(), dto.getBollinger().getBollingerUpper());
            BigDecimal lower = calcTradeLower(dto.getClose(), dto.getBollinger().getBollingerLower());

            if (upper.compareTo(dto.getBollinger().getTradeUpper()) != 0) {
                list.add("Wrong upper: " + upper + " " + dto.getBollinger().getTradeUpper());
            }

            if (lower.compareTo(dto.getBollinger().getTradeLower()) != 0) {
                list.add("Wrong lower: " + lower + " " + dto.getBollinger().getTradeLower());
            }
        }

        return list;

    }

}
