package krakee.web;

import java.io.Serializable;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import krakee.ConfigEJB;
import krakee.TimerEjb;

/**
 * JSF bean for Index page
 * @author rgt
 */
@SessionScoped
@Named(value = "indexBean")
public class IndexBean implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @EJB
    ConfigEJB config;
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
