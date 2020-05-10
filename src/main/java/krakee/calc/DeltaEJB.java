/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;

/**
 * Calculate Delta values
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
        //Get the candles
        List<CandleDTO> candleList = config.getCandleColl()
                .find(and(eq("calcCandle", true), eq("delta.calcDelta", false)))
                .into(new ArrayList<CandleDTO>());

        for (CandleDTO candle : candleList) {
            //Get the prev candle
            CandleDTO prev = config.getCandleColl()
                    .find(lt("startDate", candle.getStartDate()))
                    .sort(Sorts.descending("startDate"))
                    .first();

            if (prev != null) {
                //Calculate delta values
                DeltaDTO delta = new DeltaDTO(candle, prev);
                candle.setDelta(delta);

            } else {
                //First row
                candle.getDelta().setCalcDelta(true);
            }

            //Save candle
            config.getCandleColl()
                    .replaceOne(eq("_id", candle.getId()), candle);
        }
    }
}
