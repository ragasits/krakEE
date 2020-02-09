/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import org.bson.Document;

/**
 *
 * @author rgt
 */
@Stateless
public class BollingerEJB {

    static final Logger LOGGER = Logger.getLogger(BollingerEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;

    /**
     * Calculate Bollinger values
     *
     */
    public void calculateBollinger() {
        CandleDTO candle;
        BollingerDTO bollinger;

        //Get the candles
        MongoCursor<Document> cursor = config.getCandleColl()
                .find(and(eq("calcCandle", true), eq("bollinger.calcBollinger", false)))
                .iterator();

        while (cursor.hasNext()) {
            candle = new CandleDTO(cursor.next());
            bollinger = candle.getBollinger();
            bollinger.setCalcBollinger(true);
            bollinger.setSma(this.calcSMA(candle));
            candle.setBollinger(bollinger);

            //Save candle
            config.getCandleColl()
                    .replaceOne(eq("_id", candle.getId()), candle.getCandle());

        }
    }

    private BigDecimal calcSMA(CandleDTO dto) {
        CandleDTO candle;
        BigDecimal close = BigDecimal.ZERO;
        Document doc;
        int i = 0;

        //Get the candles
        MongoCursor<Document> cursor = config.getCandleColl()
                .find(lte("startDate", dto.getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(20)
                .iterator();

        while (cursor.hasNext()) {
            doc = cursor.next();
            candle = new CandleDTO(doc);
            close = close.add(candle.getClose());
            i++;
        }

        if (i > 0) {
            return close.divide(BigDecimal.valueOf(i), 5, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

}
