package krakee.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import krakee.calc.CandleDTO;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "candleBean")
public class CandleBean implements Serializable {

    @EJB
    MongoEJB mongo;

    private List<CandleDTO> candleList;
    private int queryLimit = 100;
    private OhlcChartModel ohlcModel;

    @PostConstruct
    public void init() {
        this.createOhlcModel();
    }

    /**
     * Check Candle date consistency
     */
    public void onDateChk() {
        FacesMessage msg;
        
        List<Date> list = mongo.chkCandleDates();
        if (list.isEmpty()){
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", "Date check: OK");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", "Date check: Errors("+list.size()+")");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCandleQuery() {
        this.candleList = mongo.getLastCandles(this.queryLimit);
        this.createOhlcModel();
    }

    private void createOhlcModel() {

        ohlcModel = new OhlcChartModel();
        ohlcModel.setTitle("Candle");
        ohlcModel.getAxis(AxisType.Y).setLabel("Candle");

        ohlcModel.getAxes().put(AxisType.X, new DateAxis("Trades"));
        ohlcModel.setCandleStick(true);
        ohlcModel.setZoom(true);

        ohlcModel.clear();

        if (candleList == null || candleList.isEmpty()) {
            ohlcModel.add(new OhlcChartSeries(0, 0, 0, 0, 0));
        } else {
            for (CandleDTO dto : candleList) {
                OhlcChartSeries series = new OhlcChartSeries(
                        dto.getStartDate().getTime(),
                        dto.getOpen().doubleValue(),
                        dto.getHigh().doubleValue(),
                        dto.getLow().doubleValue(),
                        dto.getClose().doubleValue()
                );
                ohlcModel.add(series);
            }
        }
    }

    /**
     * Show selected candle element
     *
     * @param event
     */
    public void candleSelect(ItemSelectEvent event) {
        int id = this.candleList.size() - event.getItemIndex() - 1;
        CandleDTO dto = this.candleList.get(id);

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Candle", dto.getOHLCtMsg());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<CandleDTO> getCandleList() {
        return this.candleList;
    }

    public int getQueryLimit() {
        return queryLimit;
    }

    public void setQueryLimit(int queryLimit) {
        this.queryLimit = queryLimit;
    }

    public OhlcChartModel getOhlcModel() {
        return ohlcModel;
    }

    public boolean isNotEmtyOhlcChart() {
        return !(this.candleList == null || this.candleList.isEmpty());
    }

}
