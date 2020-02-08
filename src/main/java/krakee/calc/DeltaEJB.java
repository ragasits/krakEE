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
import com.mongodb.client.model.Sorts;
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
public class DeltaEJB {

    static final Logger LOGGER = Logger.getLogger(DeltaEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;

    /**
     * Calculate delta values
     *
     */
    public void calculateDelta() {
        CandleDTO candle;
        CandleDTO prev;
        DeltaDTO delta;

        //Get the candles
        FindIterable<Document> result = config.getCandleColl()
                .find(and(eq("calcCandle", true), eq("delta.calcDelta", false)));

        try (MongoCursor<Document> cursor = result.iterator()) {
            while (cursor.hasNext()) {
                candle = new CandleDTO(cursor.next());

                //Get the prev candle
                Document doc = config.getCandleColl()
                        .find(lt("startDate", candle.getStartDate()))
                        .sort(Sorts.descending("startDate"))
                        .first();

                if (doc != null) {
                    prev = new CandleDTO(doc);

                    //Calculate delta values
                    delta = new DeltaDTO(candle, prev);
                    candle.setDelta(delta);

                } else {
                    //First row
                    candle.getDelta().setCalcDelta(true);
                }

                //Save candle
                config.getCandleColl()
                        .replaceOne(eq("_id", candle.getId()), candle.getCandle());

            }
        }
    }

}
