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
package krakee;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Timeout;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.annotation.PostConstruct;
import krakee.calc.CandleEJB;
import krakee.get.TradeEJB;

/**
 * Scheduling services
 *
 * @author rgt
 */
@Singleton
@Startup
public class TimerEJB {

    static final Logger LOGGER = Logger.getLogger(TimerEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;
    @EJB
    TradeEJB trade;
    @EJB
    CandleEJB candle;

    @Resource
    TimerService timerService;

    private int duration;

    /**
     * Start default Timer
     */
    @PostConstruct
    public void init() {
        if (config.isRunProduction()){
            return;
        }
        
        //If enabled the running
        if (config.isRunTrade() || config.isRunCandle()) {
            this.duration = config.getDefaultTimerDuration();
            
            timerService.createSingleActionTimer(this.duration* 1000L, new TimerConfig(null, false));
        }
    }

    /**
     * Scheduled task, rescheduling
     */
    @Timeout
    public void timeOut() {

        //Run task
        if (config.isRunTrade()) {
            trade.callKrakenTrade();
        }

        if (config.isRunCandle()) {
            candle.callCandle();
        }

        //Set next running
        if (config.isRunTrade() || config.isRunCandle()) {
            if ((trade.getPairTradeSize() == 1000) || (candle.getCandleSize() > 1)) {
                this.duration = config.getDefaultTimerDuration();
            } else {
                this.duration = this.duration * 2;
            }
        }
        timerService.createSingleActionTimer(this.duration* 1000L, new TimerConfig(null, false));

        LOGGER.log(Level.INFO, "Schedule Fired .... {0} {1} {2}", new Object[]{config.isRunTrade(), config.isRunCandle(), this.duration});
    }

    public long getDuration() {
        return duration;
    }

}
