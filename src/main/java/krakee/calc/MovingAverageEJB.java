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
    ConfigEJB configEjb;

    /**
     * Calculate Moving Average values
     */
    public void calculateMovingAverage() {
        List<CandleDTO> candleList;
        MovingAverageDTO ma;

        //Get the candles
        candleList = configEjb.getCandleColl()
                .find(and(eq("calcCandle", true), eq("movingAverage.calcMovingAverage", false)))
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            ma = candle.getMovingAverage();
            ma.setCalcMovingAverage(true);

            ma.setSma20(this.calcSMA(candle, 20));

            ma.setEma9(this.calcEMA(candle, 9));
            ma.setEma12(this.calcEMA(candle, 12));
            ma.setEma26(this.calcEMA(candle, 26));

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
    private BigDecimal calcSMA(CandleDTO dto, int limit) {
        BigDecimal close = BigDecimal.ZERO;
        int i = 0;

        //Get the candles
        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(lte("startDate", dto.getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(limit)
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            close = close.add(candle.getClose());
            i++;
        }

        if (i > 0) {
            return close.divide(BigDecimal.valueOf(i), 5, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Exponential Moving Average
     *
     * @param dto
     * @param limit
     * @return
     */
    private BigDecimal calcEMA(CandleDTO dto, int limit) {
        MathContext mc = new MathContext(5, RoundingMode.HALF_UP);

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

            if (prev.getMovingAverage().getEMA(limit).equals(BigDecimal.ZERO)) {
                //First element
                return this.calcSMA(dto, limit);
            } else {
                //Next elements
                BigDecimal smooth = BigDecimal.valueOf(2).divide(
                        BigDecimal.valueOf(limit).add(BigDecimal.ONE), mc);
                BigDecimal prevEma = prev.getMovingAverage().getEMA(limit);

                return smooth.multiply(
                        dto.getClose().subtract(prevEma), mc).add(prevEma);
            }
        }

        return BigDecimal.ZERO;
    }
}
