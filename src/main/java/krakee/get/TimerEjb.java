package krakee.get;

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


    
    @EJB
    TradeEJB trade;

    @Schedule(hour = "*", minute = "*", second = "*/10", info = "Every 10 second timer", timezone = "UTC", persistent = false)
    public void printSchedule() {
        trade.callKrakenTrade();
    }
}
