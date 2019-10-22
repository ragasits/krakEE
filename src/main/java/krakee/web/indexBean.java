package krakee.web;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.ConfigEJB;
import krakee.TimerEjb;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "indexBean")
public class indexBean implements Serializable {

    @EJB
    ConfigEJB config;
    @EJB
    MongoEJB mongo;
    @EJB
    TimerEjb timer;

    
    public indexBean() {
    }

    public boolean isRunTrade() {
        return config.isRunTrade();
    }

    public boolean isRunCandle() {
        return config.isRunCandle();
    }

    public long getTimerDuration(){
        return timer.getDuration();
    }
}
