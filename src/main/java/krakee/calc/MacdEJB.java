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
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import krakee.ConfigEJB;

/**
 * Calculate and store MACD elements
 *
 * @author rgt
 */
@Stateless
public class MacdEJB {

    @EJB
    private ConfigEJB configEjb;
    @EJB
    private MovingAverageEJB maEjb;
    @EJB
    private CandleEJB candleEjb;

    /**
     * Calculate MACD
     */
    public void calculateMacd() {

        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(and(eq("calcCandle", true), eq("macd.calcMacd", false)))
                .sort(Sorts.ascending("startDate"))
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            MacdDTO macd = candle.getMacd();
            macd.setCalcMacd(true);

            //MACD Line: (12-day EMA - 26-day EMA)
            macd.setMacdLine(candle
                    .getMovingAverage()
                    .getEma12()
                    .subtract(candle
                            .getMovingAverage()
                            .getEma26())
                    .setScale(5, RoundingMode.HALF_UP));

            //Signal Line: 9-day EMA of MACD Line
            macd.setSignalLine(maEjb.calcEMA(candle, 9, false).setScale(5, RoundingMode.HALF_UP));

            //MACD Histogram: MACD Line - Signal Line
            macd.setMacdHistogram(macd.getMacdLine().subtract(macd.getSignalLine()).setScale(5, RoundingMode.HALF_UP));

            //Flags
            macd.setBullMarket(macd.getMacdHistogram().compareTo(BigDecimal.ZERO) == 1);
            macd.setBearMarket(macd.getMacdHistogram().compareTo(BigDecimal.ZERO) == -1);

            //Crossover
            CandleDTO prev = candleEjb.getPrev(candle.getStartDate());
            if (prev != null) {
                double hist1 = prev.getMacd().getMacdHistogram().doubleValue();
                double hist2 = macd.getMacdHistogram().doubleValue();

                if ((hist1 > 0d && hist2 < 0d) || (hist1 < 0d && hist2 > 0d) ){
                    macd.setCrossover(true);
                }
            }

            //Save candle
            configEjb.getCandleColl().replaceOne(eq("_id", candle.getId()), candle);
        }

    }
}
