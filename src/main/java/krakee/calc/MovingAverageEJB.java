/*
 * Copyright (C) 2021 rgt
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
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;

/**
 * Calculate Moving Averages
 *
 * @author rgt
 */
@Stateless
public class MovingAverageEJB {

    static final Logger LOGGER = Logger.getLogger(MovingAverageEJB.class.getCanonicalName());

    @EJB
    private ConfigEJB configEjb;

    /**
     * Calculate Moving Average values
     */
    public void calculateMovingAverage() {
        List<CandleDTO> candleList;
        MovingAverageDTO ma;

        //Get the candles
        candleList = configEjb.getCandleColl()
                .find(and(eq("calcCandle", true), eq("movingAverage.calcMovingAverage", false)))
                .sort(Sorts.ascending("startDate"))
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            ma = candle.getMovingAverage();
            ma.setCalcMovingAverage(true);

            ma.setSma20(this.calcSMA(candle, 20, true).setScale(5, RoundingMode.HALF_UP));

            ma.setEma9(this.calcEMA(candle, 9, true).setScale(5, RoundingMode.HALF_UP));
            ma.setEma12(this.calcEMA(candle, 12, true).setScale(5, RoundingMode.HALF_UP));
            ma.setEma26(this.calcEMA(candle, 26, true).setScale(5, RoundingMode.HALF_UP));

            //Save candle
            configEjb.getCandleColl().replaceOne(eq("_id", candle.getId()), candle);

        }
    }

    /**
     * Calculate Standard Moving Average
     *
     * @param dto
     * @param limit
     * @return
     */
    private BigDecimal calcSMA(CandleDTO dto, int limit, boolean isClose) {
        BigDecimal sum = BigDecimal.ZERO;
        int i = 0;
        
        //Get the candles
        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(lte("startDate", dto.getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(limit)
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {

            if (isClose) {
                //SUM from the Candle.Close
                sum = sum.add(candle.getClose());
            } else {
                //SUM from the MAXD Line
                sum = sum.add(candle.getMacd().getMacdLine());
            }

            i++;
        }

        if (i > 0) {
            return sum.divide(BigDecimal.valueOf(i), RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Exponential Moving Average
     *
     * @param dto
     * @param limit
     * @param isClose
     * @return
     */
    public BigDecimal calcEMA(CandleDTO dto, int limit, boolean isClose) {

        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(lte("startDate", dto.getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(limit)
                .into(new ArrayList<>());

        if (candleList.size() == limit) {

            //get previous
            CandleDTO prev = configEjb.getCandleColl()
                    .find(lt("startDate", dto.getStartDate()))
                    .sort(Sorts.descending("startDate"))
                    .first();

            if (isClose) {
                //from Candle clsoe
                return this.calcEMAClose(dto, prev, limit, isClose);
            } else {
                //from MACD Line
                return this.calcEMAMacd(dto, prev, limit, isClose);
            }

        }
        return BigDecimal.ZERO;
    }

    /**
     * Sub calculation from the Candle.Close
     * @param dto
     * @param prev
     * @param limit
     * @param isClose
     * @return 
     */
    private BigDecimal calcEMAClose(CandleDTO dto, CandleDTO prev, int limit, boolean isClose) {
        if (prev.getMovingAverage().getEMA(limit).equals(BigDecimal.ZERO)) {
            //First element
            return this.calcSMA(dto, limit, isClose);
        } else {
            //Next elements
            BigDecimal smooth = BigDecimal.valueOf(2).divide(
                    BigDecimal.valueOf(limit).add(BigDecimal.ONE),  MathContext.DECIMAL128);
            
            BigDecimal prevEma = prev.getMovingAverage().getEMA(limit);

            return smooth.multiply(
                    dto.getClose().subtract(prevEma)).add(prevEma);
        }
    }

    /**
     * Sub calculation from the MACD.MACDLine
     * @param dto
     * @param prev
     * @param limit
     * @param isClose
     * @return 
     */
    private BigDecimal calcEMAMacd(CandleDTO dto, CandleDTO prev, int limit, boolean isClose) {
        if (prev.getMacd().getSignalLine().equals(BigDecimal.ZERO)) {
            //First element
            return this.calcSMA(dto, limit, isClose);
        } else {
            //Next elements
            BigDecimal smooth = BigDecimal.valueOf(2).divide(
                    BigDecimal.valueOf(limit).add(BigDecimal.ONE));
            BigDecimal prevEma = prev.getMacd().getSignalLine();

            return smooth.multiply(
                    dto.getMacd().getMacdLine().subtract(prevEma)).add(prevEma);
        }
    }
}
