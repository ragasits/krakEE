package krakee;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import krakee.calc.CandleEJB;
import krakee.get.TradeEJB;

/**
 * Scheduling services
 *
 * @author rgt
 */
@Stateless
@Singleton
public class TimerEjb {

    static final Logger LOGGER = Logger.getLogger(TradeEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;
    @EJB
    TradeEJB trade;
    @EJB
    CandleEJB candle;

    @Schedule(hour = "*", minute = "*", second = "*/10", info = "Every 10 second timer", timezone = "UTC", persistent = false)
    public void printSchedule() {
        if (config.isRunTrade()) {
            trade.callKrakenTrade();
        }

        if (config.isRunCandle()) {
            candle.callCandle();
        }
        
        LOGGER.log(Level.INFO, "Schedule Fired .... " + config.isRunTrade()+ " "+config.isRunCandle());
    }
}
