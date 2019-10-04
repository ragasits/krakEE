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

    private Date startDate;
    private Date stopDate;
    private OhlcChartModel ohlcModel;

    public indexBean() {
    }

    @PostConstruct
    public void init() {
        this.stopDate = mongo.getLatesDateFromCandle();
        if (this.stopDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.stopDate);
            cal.add(Calendar.DATE, -3);
            this.startDate = cal.getTime();
        }
        this.createOhlcModel();
    }
    
    private void createOhlcModel(){
        ohlcModel = new OhlcChartModel();
        ohlcModel.setTitle("Candle");
        ohlcModel.getAxis(AxisType.X).setLabel("Trades");
        ohlcModel.getAxis(AxisType.Y).setLabel("Candle");
        
        List<CandleDTO> CandleList = mongo.getCandleChartFromCandle(startDate, stopDate);
        for (CandleDTO dto : CandleList) {
            OhlcChartSeries series = new OhlcChartSeries(dto.getId(), 
                    dto.getOpen().doubleValue(),
                    dto.getHigh().doubleValue(),
                    dto.getLow().doubleValue(),
                    dto.getClose().doubleValue()
            );
            ohlcModel.add(series);
        }
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

    public OhlcChartModel getOhlcModel() {
        return ohlcModel;
    }
    
    

}
