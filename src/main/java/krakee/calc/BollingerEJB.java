/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.Common;
import krakee.ConfigEJB;

/**
 * Calculate Bollinger values
 *
 * @author rgt
 */
@Stateless
public class BollingerEJB {

    static final Logger LOGGER = Logger.getLogger(BollingerEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;

    /**
     * Calculate Bollinger values + Delta + Trend
     *
     */
    public void calculateBollinger() {
        List<CandleDTO> candleList;
        CandleDTO prev;
        BollingerDTO bollinger;

        //Get the candles
        candleList = config.getCandleColl()
                .find(and(eq("calcCandle", true), eq("bollinger.calcBollinger", false)))
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            bollinger = candle.getBollinger();
            bollinger.setCalcBollinger(true);
            bollinger.setSma(this.calcSMA(candle));
            bollinger.setStDev(this.calcStDev(candle));
            bollinger.setBollingerUpper(bollinger.getSma().add(bollinger.getStDev().multiply(BigDecimal.valueOf(2))));
            bollinger.setBollingerLower(bollinger.getSma().subtract(bollinger.getStDev().multiply(BigDecimal.valueOf(2))));

            //Get the prev candle
            prev = config.getCandleColl()
                    .find(lt("startDate", candle.getStartDate()))
                    .sort(Sorts.descending("startDate"))
                    .first();

            //Calc Delta + Trend
            if (prev != null) {
                bollinger.calcDeltaAndTrend(candle, prev.getBollinger());
            }

            //Save candle
            config.getCandleColl()
                    .replaceOne(eq("_id", candle.getId()), candle);

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
        List<CandleDTO> candleList = config.getCandleColl()
                .find(lte("startDate", dto.getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(20)
                .into(new ArrayList<>());

        for (CandleDTO candle : candleList) {
            //Standard Deviation
            stdev = stdev.add(candle.getClose().subtract(dto.getBollinger().getSma()).pow(2));
            i++;
        }

        if (i > 0) {
            return Common.sqrt(stdev.divide(BigDecimal.valueOf(i), 5, RoundingMode.HALF_UP), 2);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calculate Standard Moving Average
     *
     * @param dto
     * @return
     */
    private BigDecimal calcSMA(CandleDTO dto) {
        BigDecimal close = BigDecimal.ZERO;
        int i = 0;

        //Get the candles
        List<CandleDTO> candleList = config.getCandleColl()
                .find(lte("startDate", dto.getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(20)
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

}
