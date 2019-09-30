/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.web;

import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.inject.Named;
import krakee.ConfigEJB;

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
        if (this.startDate==null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(mongo.getLatesDateFromCandle());
            cal.add(Calendar.MONTH, -30);
            this.startDate = cal.getTime();
        }
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        if (this.stopDate == null) {
            this.stopDate = mongo.getLatesDateFromCandle();
        }
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

}
