package krakee.web;

import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import krakee.calc.CandleDTO;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;

/**
 *
 * @author rgt
 */
@Named(value = "candleBean")
public class CandleBean {

    @EJB
    MongoEJB mongo;

    private List<CandleDTO> candleList = null;
    private int queryLimit = 100;
    private OhlcChartModel ohlcModel;

    public void onCandleQuery() {
        this.candleList = mongo.getLastCandles(this.queryLimit);
        System.out.println(this.candleList.size());

    }

    private void createOhlcModel() {
        if (this.candleList == null) {

            ohlcModel = new OhlcChartModel();
            ohlcModel.setTitle("Candle");
            ohlcModel.getAxis(AxisType.X).setLabel("Trades");
            ohlcModel.getAxis(AxisType.Y).setLabel("Candle");
            ohlcModel.setCandleStick(true);
            ohlcModel.setAnimate(true);
            ohlcModel.setZoom(true);

            int i = 0;
            for (CandleDTO dto : candleList) {
                OhlcChartSeries series = new OhlcChartSeries(i++,
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

}
