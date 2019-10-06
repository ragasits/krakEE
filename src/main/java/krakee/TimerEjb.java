package krakee;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import krakee.calc.CandleEJB;
import krakee.get.TradeEJB;

/**
 * Scheduling services
 *
 * @author rgt
 */
@Singleton
@Startup
public class TimerEjb {

    static final Logger LOGGER = Logger.getLogger(TradeEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;
    @EJB
    TradeEJB trade;
    @EJB
    CandleEJB candle;

    @Resource
    TimerService timerService;

    private long duration;

    /**
     * Start default Timer
     */
    @PostConstruct
    public void init() {
        //If enabled the running
        if (config.isRunTrade() || config.isRunCandle()) {
            this.duration = config.getDefaultTimerDuration();
            timerService.createTimer(this.duration, null);
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
        if (trade.getPairTradeSize() == 1000) {
            this.duration = config.getDefaultTimerDuration();
        } else {
            this.duration = this.duration * 2;
        }
        timerService.createTimer(this.duration, null);

        LOGGER.log(Level.INFO, "Schedule Fired .... "
                + config.isRunTrade() + " "
                + config.isRunCandle() + " "
                + this.duration);
    }

    public long getDuration() {
        return duration;
    }

}
