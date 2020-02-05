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
public class IndexBean implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @EJB
    ConfigEJB config;
    @EJB
    MongoEJB mongo;
    @EJB
    TimerEjb timer;

    
    public IndexBean() {
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