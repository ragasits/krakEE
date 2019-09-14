package krakee.get;


import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

/**
 *
 * @author rgt
 */
@Stateless
@Singleton
public class TimerEjb {

    static final Logger LOGGER = Logger.getLogger(TimerEjb.class.getCanonicalName());

    @Schedule(hour = "*", minute = "*", second = "*/5", info = "Every 5 second timer", timezone = "UTC", persistent = false)
    public void printSchedule() {

        LOGGER.info("TimerEjb Schedule Fired .... ");
    }

}
