/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import java.math.BigDecimal;
import org.bson.Document;
import org.bson.types.Decimal128;

/**
 *
 * @author rgt
 */
public class DeltaDTO {

    private boolean calcDelta;

    private final Integer deltaCount;
    private final Integer deltaCountBuy;
    private final Integer deltaCountSell;
    private final BigDecimal deltaOpen;
    private final BigDecimal deltaLow;
    private final BigDecimal deltaHigh;
    private final BigDecimal deltaClose;
    private final BigDecimal deltaTotal;
    private final BigDecimal deltaTotalBuy;
    private final BigDecimal deltaTotalSell;
    private final BigDecimal deltaVolume;
    private final BigDecimal deltaVolumeBuy;
    private final BigDecimal deltaVolumeSell;
    //Trends
    private final Integer trendOpenUp;
    private final Integer trendOpenDown;
    private final Integer trendHighUp;
    private final Integer trendHighDown;
    private final Integer trendLowUp;
    private final Integer trendLowDown;
    private final Integer trendCloseUp;
    private final Integer trendCloseDown;

    private final Integer trendTotalUp;
    private final Integer trendTotalBuyUp;
    private final Integer trendTotalSellUp;
    private final Integer trendVolumeUp;
    private final Integer trendVolumeBuyUp;
    private final Integer trendVolumeSellUp;

    private final Integer trendTotalDown;
    private final Integer trendTotalBuyDown;
    private final Integer trendTotalSellDown;
    private final Integer trendVolumeDown;
    private final Integer trendVolumeBuyDown;
    private final Integer trendVolumeSellDown;

    private final Integer trendCountUp;
    private final Integer trendCountBuyUp;
    private final Integer trendCountSellUp;

    private final Integer trendCountDown;
    private final Integer trendCountBuyDown;
    private final Integer trendCountSellDown;

    public DeltaDTO() {
        this.calcDelta = false;
        this.deltaCount = 0;
        this.deltaCountBuy = 0;
        this.deltaCountSell = 0;
        this.deltaOpen = BigDecimal.ZERO;
        this.deltaLow = BigDecimal.ZERO;
        this.deltaHigh = BigDecimal.ZERO;
        this.deltaClose = BigDecimal.ZERO;
        this.deltaTotal = BigDecimal.ZERO;
        this.deltaTotalBuy = BigDecimal.ZERO;
        this.deltaTotalSell = BigDecimal.ZERO;
        this.deltaVolume = BigDecimal.ZERO;
        this.deltaVolumeBuy = BigDecimal.ZERO;
        this.deltaVolumeSell = BigDecimal.ZERO;

        this.trendOpenUp = 0;
        this.trendOpenDown = 0;
        this.trendHighUp = 0;
        this.trendHighDown = 0;
        this.trendLowUp = 0;
        this.trendLowDown = 0;
        this.trendCloseUp = 0;
        this.trendCloseDown = 0;

        this.trendTotalUp = 0;
        this.trendTotalBuyUp = 0;
        this.trendTotalSellUp = 0;
        this.trendVolumeUp = 0;
        this.trendVolumeBuyUp = 0;
        this.trendVolumeSellUp = 0;

        this.trendTotalDown = 0;
        this.trendTotalBuyDown = 0;
        this.trendTotalSellDown = 0;
        this.trendVolumeDown = 0;
        this.trendVolumeBuyDown = 0;
        this.trendVolumeSellDown = 0;

        this.trendCountUp = 0;
        this.trendCountBuyUp = 0;
        this.trendCountSellUp = 0;

        this.trendCountDown = 0;
        this.trendCountBuyDown = 0;
        this.trendCountSellDown = 0;

    }

    public DeltaDTO(Document doc) {
        this.calcDelta = doc.getBoolean("calcDelta");
        this.deltaCount = doc.getInteger("deltaCount");
        this.deltaCountBuy = doc.getInteger("deltaCountBuy");
        this.deltaCountSell = doc.getInteger("deltaCountSell");
        this.deltaOpen = ((Decimal128) doc.get("deltaOpen")).bigDecimalValue();
        this.deltaLow = ((Decimal128) doc.get("deltaLow")).bigDecimalValue();
        this.deltaHigh = ((Decimal128) doc.get("deltaHigh")).bigDecimalValue();
        this.deltaClose = ((Decimal128) doc.get("deltaClose")).bigDecimalValue();
        this.deltaTotal = ((Decimal128) doc.get("deltaTotal")).bigDecimalValue();
        this.deltaTotalBuy = ((Decimal128) doc.get("deltaTotalBuy")).bigDecimalValue();
        this.deltaTotalSell = ((Decimal128) doc.get("deltaTotalSell")).bigDecimalValue();
        this.deltaVolume = ((Decimal128) doc.get("deltaVolume")).bigDecimalValue();
        this.deltaVolumeBuy = ((Decimal128) doc.get("deltaVolumeBuy")).bigDecimalValue();
        this.deltaVolumeSell = ((Decimal128) doc.get("deltaVolumeSell")).bigDecimalValue();

        this.trendOpenUp = doc.getInteger("trendOpenUp");
        this.trendOpenDown = doc.getInteger("trendOpenDown");
        this.trendHighUp = doc.getInteger("trendHighUp");
        this.trendHighDown = doc.getInteger("trendHighDown");
        this.trendLowUp = doc.getInteger("trendLowUp");
        this.trendLowDown = doc.getInteger("trendLowDown");
        this.trendCloseUp = doc.getInteger("trendCloseUp");
        this.trendCloseDown = doc.getInteger("trendCloseDown");

        this.trendTotalUp = doc.getInteger("trendTotalUp");
        this.trendTotalBuyUp = doc.getInteger("trendTotalBuyUp");
        this.trendTotalSellUp = doc.getInteger("trendTotalSellUp");
        this.trendVolumeUp = doc.getInteger("trendVolumeUp");
        this.trendVolumeBuyUp = doc.getInteger("trendVolumeBuyUp");
        this.trendVolumeSellUp = doc.getInteger("trendVolumeSellUp");

        this.trendTotalDown = doc.getInteger("trendTotalDown");
        this.trendTotalBuyDown = doc.getInteger("trendTotalBuyDown");
        this.trendTotalSellDown = doc.getInteger("trendTotalSellDown");
        this.trendVolumeDown = doc.getInteger("trendVolumeDown");
        this.trendVolumeBuyDown = doc.getInteger("trendVolumeBuyDown");
        this.trendVolumeSellDown = doc.getInteger("trendVolumeSellDown");

        this.trendCountUp = doc.getInteger("trendCountUp");
        this.trendCountBuyUp = doc.getInteger("trendCountBuyUp");
        this.trendCountSellUp = doc.getInteger("trendCountSellUp");

        this.trendCountDown = doc.getInteger("trendCountDown");
        this.trendCountBuyDown = doc.getInteger("trendCountBuyDown");
        this.trendCountSellDown = doc.getInteger("trendCountSellDown");

    }

    /**
     * Calculate Deltas
     *
     * @param last
     * @param prev
     */
    public DeltaDTO(CandleDTO last, CandleDTO prev) {
        this.calcDelta = true;
        this.deltaCount = last.getCount() - prev.getCount();
        this.deltaCountBuy = last.getCountBuy() - prev.getCountBuy();
        this.deltaCountSell = last.getCountSell() - prev.getCountSell();
        this.deltaOpen = last.getOpen().subtract(prev.getOpen());
        this.deltaLow = last.getLow().subtract(prev.getLow());
        this.deltaHigh = last.getHigh().subtract(prev.getHigh());
        this.deltaClose = last.getClose().subtract(prev.getClose());
        this.deltaTotal = last.getTotal().subtract(prev.getTotal());
        this.deltaTotalBuy = last.getTotalBuy().subtract(prev.getTotalBuy());
        this.deltaTotalSell = last.getTotalSell().subtract(prev.getTotalSell());
        this.deltaVolume = last.getVolume().subtract(last.getVolume());
        this.deltaVolumeBuy = last.getVolumeBuy().subtract(prev.getVolumeBuy());
        this.deltaVolumeSell = last.getVolumeSell().subtract(prev.getVolumeSell());

        this.trendOpenUp = this.calcTrendUp(last.getOpen(), prev.getOpen(), prev.getDelta().trendOpenUp);
        this.trendOpenDown = this.calcTrendDown(last.getOpen(), prev.getOpen(), prev.getDelta().trendOpenDown);
        this.trendHighUp = this.calcTrendUp(last.getHigh(), prev.getHigh(), prev.getDelta().trendHighUp);
        this.trendHighDown = this.calcTrendDown(last.getHigh(), prev.getHigh(), prev.getDelta().trendHighDown);
        this.trendLowUp = this.calcTrendUp(last.getLow(), prev.getLow(), prev.getDelta().trendLowUp);
        this.trendLowDown = this.calcTrendDown(last.getLow(), prev.getLow(), prev.getDelta().trendLowDown);
        this.trendCloseUp = this.calcTrendUp(last.getClose(), prev.getClose(), prev.getDelta().trendCloseUp);
        this.trendCloseDown = this.calcTrendDown(last.getClose(), prev.getClose(), prev.getDelta().trendCloseDown);

        this.trendTotalUp = this.calcTrendUp(last.getTotal(), prev.getTotal(), prev.getDelta().trendTotalUp);
        this.trendTotalBuyUp = this.calcTrendUp(last.getTotalBuy(), prev.getTotalBuy(), prev.getDelta().trendTotalBuyUp);
        this.trendTotalSellUp = this.calcTrendUp(last.getTotalSell(), prev.getTotalSell(), prev.getDelta().trendTotalSellUp);
        this.trendVolumeUp = this.calcTrendUp(last.getVolume(), prev.getVolume(), prev.getDelta().trendVolumeUp);
        this.trendVolumeBuyUp = this.calcTrendUp(last.getVolumeBuy(), prev.getVolumeBuy(), prev.getDelta().trendVolumeBuyUp);
        this.trendVolumeSellUp = this.calcTrendUp(last.getVolumeSell(), prev.getVolumeSell(), prev.getDelta().trendVolumeSellUp);

        this.trendTotalDown = this.calcTrendDown(last.getTotal(), prev.getTotal(), prev.getDelta().trendTotalDown);
        this.trendTotalBuyDown = this.calcTrendDown(last.getTotalBuy(), prev.getTotalBuy(), prev.getDelta().trendTotalBuyDown);
        this.trendTotalSellDown = this.calcTrendDown(last.getTotalSell(), prev.getTotalSell(), prev.getDelta().trendTotalSellDown);
        this.trendVolumeDown = this.calcTrendDown(last.getVolume(), prev.getVolume(), prev.getDelta().trendVolumeDown);
        this.trendVolumeBuyDown = this.calcTrendDown(last.getVolumeBuy(), prev.getVolumeBuy(), prev.getDelta().trendVolumeBuyDown);
        this.trendVolumeSellDown = this.calcTrendDown(last.getVolumeSell(), prev.getVolumeSell(), prev.getDelta().trendVolumeSellDown);

        this.trendCountUp = this.calcTrendUp(last.getCount(), prev.getCount(), prev.getDelta().trendCountUp);
        this.trendCountBuyUp = this.calcTrendUp(last.getCountBuy(), prev.getCountBuy(), prev.getDelta().trendCountBuyUp);
        this.trendCountSellUp = this.calcTrendUp(last.getCountSell(), prev.getCountSell(), prev.getDelta().trendCountSellUp);

        this.trendCountDown = this.calcTrendDown(last.getCount(), prev.getCount(), prev.getDelta().trendCountDown);
        this.trendCountBuyDown = this.calcTrendDown(last.getCountBuy(), prev.getCountBuy(), prev.getDelta().trendCountBuyDown);
        this.trendCountSellDown = this.calcTrendDown(last.getCountSell(), prev.getCountSell(), prev.getDelta().trendCountSellDown);
    }

    /**
     * Calculate Trend Up value (Integer)
     *
     * @param last
     * @param prev
     * @param prevUp
     * @return
     */
    private Integer calcTrendUp(Integer last, Integer prev, Integer prevUp) {
        return this.calcTrendUp(BigDecimal.valueOf(last), BigDecimal.valueOf(prev), prevUp);
    }

    
    /**
     * Calculate Trend Down value (Integer)
     * @param last
     * @param prev
     * @param prevUp
     * @return 
     */
    private Integer calcTrendDown(Integer last, Integer prev, Integer prevUp) {
        return this.calcTrendUp(BigDecimal.valueOf(last), BigDecimal.valueOf(prev), prevUp);
    }

    /**
     * Calculate Trend up value (BigDecima)
     *
     * @param last
     * @param prev
     * @param prevUp
     * @return
     */
    private Integer calcTrendUp(BigDecimal last, BigDecimal prev, Integer prevUp) {
        switch (last.compareTo(prev)) {
            case 1:
                return prevUp + 1;
            case 0:
                return prevUp;
            case -1:
                return 0;
        }
        return null;
    }

    /**
     * Calculate Trend down value (BigDecimal)
     *
     * @param last
     * @param prev
     * @param prevDown
     * @return
     */
    private Integer calcTrendDown(BigDecimal last, BigDecimal prev, Integer prevDown) {
        switch (last.compareTo(prev)) {
            case 1:
                return 0;
            case 0:
                return prevDown;
            case -1:
                return prevDown + 1;
        }
        return null;
    }

    public Document getDelta() {
        return new Document("calcDelta", this.calcDelta)
                .append("deltaCount", this.deltaCount)
                .append("deltaCountBuy", this.deltaCountBuy)
                .append("deltaCountSell", this.deltaCountSell)
                .append("deltaOpen", this.deltaOpen)
                .append("deltaLow", this.deltaLow)
                .append("deltaHigh", this.deltaHigh)
                .append("deltaClose", this.deltaClose)
                .append("deltaTotal", this.deltaTotal)
                .append("deltaTotalBuy", this.deltaTotalBuy)
                .append("deltaTotalSell", this.deltaTotalSell)
                .append("deltaVolume", this.deltaVolume)
                .append("deltaVolumeBuy", this.deltaVolumeBuy)
                .append("deltaVolumeSell", this.deltaVolumeSell)
                .append("trendOpenUp", this.trendOpenUp)
                .append("trendOpenDown", this.trendOpenDown)
                .append("trendLowUp", this.trendLowUp)
                .append("trendLowDown", this.trendLowDown)
                .append("trendHighUp", this.trendHighUp)
                .append("trendHighDown", this.trendHighDown)
                .append("trendCloseUp", this.trendCloseUp)
                .append("trendCloseDown", this.trendCloseDown)
                .append("trendTotalUp", this.trendTotalUp)
                .append("trendTotalBuyUp", this.trendTotalBuyUp)
                .append("trendTotalSellUp", this.trendTotalSellUp)
                .append("trendVolumeUp", this.trendVolumeUp)
                .append("trendVolumeBuyUp", this.trendVolumeBuyUp)
                .append("trendVolumeSellUp", this.trendVolumeSellUp)
                .append("trendTotalDown", this.trendTotalDown)
                .append("trendTotalBuyDown", this.trendTotalBuyDown)
                .append("trendTotalSellDown", this.trendTotalSellDown)
                .append("trendVolumeDown", this.trendVolumeDown)
                .append("trendVolumeBuyDown", this.trendVolumeBuyDown)
                .append("trendVolumeSellDown", this.trendVolumeSellDown)
                .append("trendCountUp", this.trendCountUp)
                .append("trendCountBuyUp", this.trendCountBuyUp)
                .append("trendCountSellUp", this.trendCountSellUp)
                .append("trendCountDown", this.trendCountDown)
                .append("trendCountBuyDown", this.trendCountBuyDown)
                .append("trendCountSellDown", this.trendCountSellDown);

    }

    public boolean isCalcDelta() {
        return calcDelta;
    }

    public void setCalcDelta(boolean calcDelta) {
        this.calcDelta = calcDelta;
    }
    
    public Integer getDeltaCount() {
        return deltaCount;
    }

    public Integer getDeltaCountBuy() {
        return deltaCountBuy;
    }

    public Integer getDeltaCountSell() {
        return deltaCountSell;
    }

    public BigDecimal getDeltaOpen() {
        return deltaOpen;
    }

    public BigDecimal getDeltaLow() {
        return deltaLow;
    }

    public BigDecimal getDeltaHigh() {
        return deltaHigh;
    }

    public BigDecimal getDeltaClose() {
        return deltaClose;
    }

    public BigDecimal getDeltaTotal() {
        return deltaTotal;
    }

    public BigDecimal getDeltaTotalBuy() {
        return deltaTotalBuy;
    }

    public BigDecimal getDeltaTotalSell() {
        return deltaTotalSell;
    }

    public BigDecimal getDeltaVolume() {
        return deltaVolume;
    }

    public BigDecimal getDeltaVolumeBuy() {
        return deltaVolumeBuy;
    }

    public BigDecimal getDeltaVolumeSell() {
        return deltaVolumeSell;
    }

    public Integer getTrendOpenUp() {
        return trendOpenUp;
    }

    public Integer getTrendOpenDown() {
        return trendOpenDown;
    }

    public Integer getTrendHighUp() {
        return trendHighUp;
    }

    public Integer getTrendHighDown() {
        return trendHighDown;
    }

    public Integer getTrendLowUp() {
        return trendLowUp;
    }

    public Integer getTrendLowDown() {
        return trendLowDown;
    }

    public Integer getTrendCloseUp() {
        return trendCloseUp;
    }

    public Integer getTrendCloseDown() {
        return trendCloseDown;
    }

    public Integer getTrendTotalUp() {
        return trendTotalUp;
    }

    public Integer getTrendTotalBuyUp() {
        return trendTotalBuyUp;
    }

    public Integer getTrendTotalSellUp() {
        return trendTotalSellUp;
    }

    public Integer getTrendVolumeUp() {
        return trendVolumeUp;
    }

    public Integer getTrendVolumeBuyUp() {
        return trendVolumeBuyUp;
    }

    public Integer getTrendVolumeSellUp() {
        return trendVolumeSellUp;
    }

    public Integer getTrendTotalDown() {
        return trendTotalDown;
    }

    public Integer getTrendTotalBuyDown() {
        return trendTotalBuyDown;
    }

    public Integer getTrendTotalSellDown() {
        return trendTotalSellDown;
    }

    public Integer getTrendVolumeDown() {
        return trendVolumeDown;
    }

    public Integer getTrendVolumeBuyDown() {
        return trendVolumeBuyDown;
    }

    public Integer getTrendVolumeSellDown() {
        return trendVolumeSellDown;
    }

    public Integer getTrendCountUp() {
        return trendCountUp;
    }

    public Integer getTrendCountBuyUp() {
        return trendCountBuyUp;
    }

    public Integer getTrendCountSellUp() {
        return trendCountSellUp;
    }

    public Integer getTrendCountDown() {
        return trendCountDown;
    }

    public Integer getTrendCountBuyDown() {
        return trendCountBuyDown;
    }

    public Integer getTrendCountSellDown() {
        return trendCountSellDown;
    }

}
