package krakee.get;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

/**
 * Scheduling services
 *
 * @author rgt
 */
@Stateless
@Singleton
public class TimerEjb {

    static final Logger LOGGER = Logger.getLogger(TimerEjb.class.getCanonicalName());
    
    @EJB
    TradeEJB trade;

    @Schedule(hour = "*", minute = "*", second = "*/5", info = "Every 5 second timer", timezone = "UTC", persistent = false)
    public void printSchedule() {
        LOGGER.log(Level.INFO, "TimerEjb Schedule Fired .... {0}", trade.callKrakenTrade());
    }
}
