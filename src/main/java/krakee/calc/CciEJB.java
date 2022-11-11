/*
 * Copyright (C) 2022 rgt
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
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;

/**
 * Calculate Commodity Channel Index (CCI)
 *
 * @author rgt
 */
@Stateless
public class CciEJB {

    private static final BigDecimal CONST = BigDecimal.valueOf(0.015);
    private static final String STARTDATE = "startDate";

    @EJB
    ConfigEJB configEjb;

    /**
     * Calculate CCI
     */
    public void calculateCci() {
        BigDecimal sum;
        BigDecimal avg;
        BigDecimal mad;
        BigDecimal cci;

        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(and(eq("calcCandle", true), eq("cci.calcCci", false)))
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            // Typical Price
            BigDecimal typicalPrice = candle.getHigh()
                    .add(candle.getLow())
                    .add(candle.getClose())
                    .divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);

            candle.getCci().setTypicalPrice(typicalPrice);
            this.saveCci(candle);

            // 20-day SMA of TP
            List<CandleDTO> smaList = configEjb.getCandleColl()
                    .find(lte(STARTDATE, candle.getStartDate()))
                    .sort(Sorts.descending(STARTDATE))
                    .limit(20)
                    .into(new ArrayList<>());

            sum = BigDecimal.ZERO;

            for (CandleDTO dto : smaList) {
                sum = sum.add(dto.getCci().getTypicalPrice());
            }

            avg = sum.divide(BigDecimal.valueOf(20), RoundingMode.HALF_UP);
            candle.getCci().setSma20Typical(avg);
            this.saveCci(candle);

            //20-day Mean Deviation
            List<CandleDTO> madList = configEjb.getCandleColl()
                    .find(lte(STARTDATE, candle.getStartDate()))
                    .sort(Sorts.descending(STARTDATE))
                    .limit(20)
                    .into(new ArrayList<>());

            sum = BigDecimal.ZERO;
            for (CandleDTO dto : madList) {
                sum = sum.add(
                        avg.subtract(dto.getCci().getTypicalPrice()).abs()
                );
            }
            mad = sum.divide(BigDecimal.valueOf(20), RoundingMode.HALF_UP);

            candle.getCci().setMad20(mad);
            this.saveCci(candle);

            //20-day CCI
            cci = candle.getCci().getTypicalPrice()
                    .subtract(candle.getCci().getSma20Typical())
                    .divide(
                            CONST.multiply(candle.getCci().getMad20()),
                            RoundingMode.HALF_UP);

            if (cci.compareTo(BigDecimal.valueOf(100)) >= 0) {
                candle.getCci().setOverBought(true);
            }

            if (cci.compareTo(BigDecimal.valueOf(-100)) <= 0) {
                candle.getCci().setOverSold(true);
            }

            candle.getCci().setCci20(cci);
            candle.getCci().setCalcCci(true);
            this.saveCci(candle);

        }

    }

    private void saveCci(CandleDTO candle) {
        configEjb.getCandleColl()
                .replaceOne(eq("_id", candle.getId()), candle);
    }

}
