/*
 * Copyright (C) 2024 rgt
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
package krakee.prod;

import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import krakee.ConfigEJB;
import krakee.MyException;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.get.TradeEJB;

/**
 *
 * @author rgt
 */
@Stateless
public class ProdEJB {

    static final Logger LOGGER = Logger.getLogger(ProdEJB.class.getCanonicalName());

    @EJB
    private ConfigEJB configEjb;

    @EJB
    private TradeEJB tradeEjb;

    @EJB
    private CandleEJB candleEjb;

    /**
     * Run production in every 30 min
     */
    @Schedule(minute = "00, 30", hour = "*")
    //@Schedule(hour = "*", minute = "*", second = "00")
    public void runProduction() {
        Date runDate = Calendar.getInstance().getTime();
        LOGGER.log(Level.INFO, "Start runProduction: {0}", runDate);

        //Checks
        if (!configEjb.isRunProduction()) {
            LOGGER.log(Level.INFO, "isRunProduction: false");
            return;
        }

        if (configEjb.isRunTrade() || configEjb.isRunCandle()) {
            LOGGER.log(Level.INFO, "isRunTrade :{0} isRunCandle: {1}", new Object[]{configEjb.isRunTrade(), configEjb.isRunCandle()});
            return;
        }

        // get Trades
        runTrade(runDate);

        //Calculate Candles
        candleEjb.deleteLastCandle();

        try {
            this.createCandle(runDate);
            candleEjb.callCandleProd();
        } catch (MyException ex) {
            Logger.getLogger(ProdEJB.class.getName()).log(Level.SEVERE, null, ex);
        }

        configEjb.setRunCandle(false);
        configEjb.setRunProduction(true);

        Date stopdate = Calendar.getInstance().getTime();
        LOGGER.log(Level.INFO, "Stop runProduction: {0}", stopdate);

        //Save log
        ProdLogDTO dto = new ProdLogDTO(runDate,
                configEjb.isRunProduction(),
                configEjb.isRunTrade(),
                configEjb.isRunCandle(), stopdate);
        
        configEjb.getProdLogColl().insertOne(dto);

    }

    /**
     * Create candles
     * @param runDate
     * @throws MyException 
     */
    private void createCandle(Date runDate) throws MyException {
        Date startDate = candleEjb.getStartDate();

        //Set the stopDate, we skip the last unfinished candle
        Calendar cal = Calendar.getInstance();
        cal.setTime(runDate);
        cal.add(Calendar.MINUTE, -1);
        Date stopDate = cal.getTime();

        //Generate candle dates
        while (startDate.getTime() < stopDate.getTime()) {
            LOGGER.log(Level.INFO, "calcDateList {0}-{1}", new Object[]{startDate, runDate});

            configEjb.getCandleColl().insertOne(new CandleDTO(startDate));
            cal.setTime(startDate);
            cal.add(Calendar.MINUTE, 30);
            startDate = cal.getTime();
        }
    }

    /**
     * Get Trades from Kraken
     * @param runDate
     * @throws NumberFormatException 
     */
    private void runTrade(Date runDate) throws NumberFormatException {
        configEjb.setRunProduction(false);

        long runTime = runDate.getTime();
        String last = tradeEjb.getLastValue();
        long lastTime = Long.parseLong(last.substring(0, 13));

        // Get Trades
        while (runTime > lastTime) {
            tradeEjb.callKrakenTrade(last);

            last = tradeEjb.getLastValue();
            lastTime = Long.parseLong(last.substring(0, 13));

            LOGGER.log(Level.INFO, "runProduction: runTime: {0} lastTime: {1}", new Object[]{runTime, lastTime});
        }

        configEjb.setRunTrade(false);
    }

}
