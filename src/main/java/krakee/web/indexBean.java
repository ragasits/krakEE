/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import krakee.ConfigEJB;
import krakee.TimerEjb;
import krakee.calc.CandleDTO;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;

/**
 *
 * @author rgt
 */
@Named(value = "indexBean")
public class indexBean {

    @EJB
    ConfigEJB config;
    @EJB
    MongoEJB mongo;
    @EJB
    TimerEjb timer;

    private Date startDate;
    private Date stopDate;

    
    public indexBean() {
    }

    public boolean isRunTrade() {
        return config.isRunTrade();
    }

    public boolean isRunCandle() {
        return config.isRunCandle();
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return this.stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }


    
    
    public long getTimerDuration(){
        return timer.getDuration();
    }
}
