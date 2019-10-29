package krakee.web;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.calc.CandleDTO;
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

    public void onCandleQuery() {
        this.candleList = mongo.getLastCandles(this.queryLimit);
        this.createOhlcModel();
    }

    private void createOhlcModel() {

        ohlcModel = new OhlcChartModel();
        ohlcModel.setTitle("Candle");
        //ohlcModel.getAxis(AxisType.X).setLabel("Trades");
        ohlcModel.getAxis(AxisType.Y).setLabel("Candle");

        ohlcModel.getAxes().put(AxisType.X, new DateAxis("Trades"));
        //ohlcModel.getAxes().put(AxisType.Y, new Axis("Candle"));
        ohlcModel.setCandleStick(true);
        ohlcModel.setAnimate(true);
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
