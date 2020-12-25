/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import java.math.BigDecimal;
import krakee.Common;

/**
 * DTO for deltas
 *
 * @author rgt
 */
public class DeltaDTO {

    private boolean calcDelta;

    private Integer deltaCount;
    private Integer deltaCountBuy;
    private Integer deltaCountSell;
    private BigDecimal deltaOpen;
    private BigDecimal deltaLow;
    private BigDecimal deltaHigh;
    private BigDecimal deltaClose;
    private BigDecimal deltaTotal;
    private BigDecimal deltaTotalBuy;
    private BigDecimal deltaTotalSell;
    private BigDecimal deltaVolume;
    private BigDecimal deltaVolumeBuy;
    private BigDecimal deltaVolumeSell;
    //Trends
    private Integer trendOpenUp;
    private Integer trendOpenDown;
    private Integer trendHighUp;
    private Integer trendHighDown;
    private Integer trendLowUp;
    private Integer trendLowDown;
    private Integer trendCloseUp;
    private Integer trendCloseDown;

    private Integer trendTotalUp;
    private Integer trendTotalBuyUp;
    private Integer trendTotalSellUp;
    private Integer trendVolumeUp;
    private Integer trendVolumeBuyUp;
    private Integer trendVolumeSellUp;

    private Integer trendTotalDown;
    private Integer trendTotalBuyDown;
    private Integer trendTotalSellDown;
    private Integer trendVolumeDown;
    private Integer trendVolumeBuyDown;
    private Integer trendVolumeSellDown;

    private Integer trendCountUp;
    private Integer trendCountBuyUp;
    private Integer trendCountSellUp;

    private Integer trendCountDown;
    private Integer trendCountBuyDown;
    private Integer trendCountSellDown;

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
        this.deltaVolume = last.getVolume().subtract(prev.getVolume());
        this.deltaVolumeBuy = last.getVolumeBuy().subtract(prev.getVolumeBuy());
        this.deltaVolumeSell = last.getVolumeSell().subtract(prev.getVolumeSell());

        this.trendOpenUp = Common.calcTrendUp(last.getOpen(), prev.getOpen(), prev.getDelta().trendOpenUp);
        this.trendOpenDown = Common.calcTrendDown(last.getOpen(), prev.getOpen(), prev.getDelta().trendOpenDown);
        this.trendHighUp = Common.calcTrendUp(last.getHigh(), prev.getHigh(), prev.getDelta().trendHighUp);
        this.trendHighDown = Common.calcTrendDown(last.getHigh(), prev.getHigh(), prev.getDelta().trendHighDown);
        this.trendLowUp = Common.calcTrendUp(last.getLow(), prev.getLow(), prev.getDelta().trendLowUp);
        this.trendLowDown = Common.calcTrendDown(last.getLow(), prev.getLow(), prev.getDelta().trendLowDown);
        this.trendCloseUp = Common.calcTrendUp(last.getClose(), prev.getClose(), prev.getDelta().trendCloseUp);
        this.trendCloseDown = Common.calcTrendDown(last.getClose(), prev.getClose(), prev.getDelta().trendCloseDown);

        this.trendTotalUp = Common.calcTrendUp(last.getTotal(), prev.getTotal(), prev.getDelta().trendTotalUp);
        this.trendTotalBuyUp = Common.calcTrendUp(last.getTotalBuy(), prev.getTotalBuy(), prev.getDelta().trendTotalBuyUp);
        this.trendTotalSellUp = Common.calcTrendUp(last.getTotalSell(), prev.getTotalSell(), prev.getDelta().trendTotalSellUp);
        this.trendVolumeUp = Common.calcTrendUp(last.getVolume(), prev.getVolume(), prev.getDelta().trendVolumeUp);
        this.trendVolumeBuyUp = Common.calcTrendUp(last.getVolumeBuy(), prev.getVolumeBuy(), prev.getDelta().trendVolumeBuyUp);
        this.trendVolumeSellUp = Common.calcTrendUp(last.getVolumeSell(), prev.getVolumeSell(), prev.getDelta().trendVolumeSellUp);

        this.trendTotalDown = Common.calcTrendDown(last.getTotal(), prev.getTotal(), prev.getDelta().trendTotalDown);
        this.trendTotalBuyDown = Common.calcTrendDown(last.getTotalBuy(), prev.getTotalBuy(), prev.getDelta().trendTotalBuyDown);
        this.trendTotalSellDown = Common.calcTrendDown(last.getTotalSell(), prev.getTotalSell(), prev.getDelta().trendTotalSellDown);
        this.trendVolumeDown = Common.calcTrendDown(last.getVolume(), prev.getVolume(), prev.getDelta().trendVolumeDown);
        this.trendVolumeBuyDown = Common.calcTrendDown(last.getVolumeBuy(), prev.getVolumeBuy(), prev.getDelta().trendVolumeBuyDown);
        this.trendVolumeSellDown = Common.calcTrendDown(last.getVolumeSell(), prev.getVolumeSell(), prev.getDelta().trendVolumeSellDown);

        this.trendCountUp = Common.calcTrendUp(last.getCount(), prev.getCount(), prev.getDelta().trendCountUp);
        this.trendCountBuyUp = Common.calcTrendUp(last.getCountBuy(), prev.getCountBuy(), prev.getDelta().trendCountBuyUp);
        this.trendCountSellUp = Common.calcTrendUp(last.getCountSell(), prev.getCountSell(), prev.getDelta().trendCountSellUp);

        this.trendCountDown = Common.calcTrendDown(last.getCount(), prev.getCount(), prev.getDelta().trendCountDown);
        this.trendCountBuyDown = Common.calcTrendDown(last.getCountBuy(), prev.getCountBuy(), prev.getDelta().trendCountBuyDown);
        this.trendCountSellDown = Common.calcTrendDown(last.getCountSell(), prev.getCountSell(), prev.getDelta().trendCountSellDown);
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

    public void setDeltaCount(Integer deltaCount) {
        this.deltaCount = deltaCount;
    }

    public void setDeltaCountBuy(Integer deltaCountBuy) {
        this.deltaCountBuy = deltaCountBuy;
    }

    public void setDeltaCountSell(Integer deltaCountSell) {
        this.deltaCountSell = deltaCountSell;
    }

    public void setDeltaOpen(BigDecimal deltaOpen) {
        this.deltaOpen = deltaOpen;
    }

    public void setDeltaLow(BigDecimal deltaLow) {
        this.deltaLow = deltaLow;
    }

    public void setDeltaHigh(BigDecimal deltaHigh) {
        this.deltaHigh = deltaHigh;
    }

    public void setDeltaClose(BigDecimal deltaClose) {
        this.deltaClose = deltaClose;
    }

    public void setDeltaTotal(BigDecimal deltaTotal) {
        this.deltaTotal = deltaTotal;
    }

    public void setDeltaTotalBuy(BigDecimal deltaTotalBuy) {
        this.deltaTotalBuy = deltaTotalBuy;
    }

    public void setDeltaTotalSell(BigDecimal deltaTotalSell) {
        this.deltaTotalSell = deltaTotalSell;
    }

    public void setDeltaVolume(BigDecimal deltaVolume) {
        this.deltaVolume = deltaVolume;
    }

    public void setDeltaVolumeBuy(BigDecimal deltaVolumeBuy) {
        this.deltaVolumeBuy = deltaVolumeBuy;
    }

    public void setDeltaVolumeSell(BigDecimal deltaVolumeSell) {
        this.deltaVolumeSell = deltaVolumeSell;
    }

    public void setTrendOpenUp(Integer trendOpenUp) {
        this.trendOpenUp = trendOpenUp;
    }

    public void setTrendOpenDown(Integer trendOpenDown) {
        this.trendOpenDown = trendOpenDown;
    }

    public void setTrendHighUp(Integer trendHighUp) {
        this.trendHighUp = trendHighUp;
    }

    public void setTrendHighDown(Integer trendHighDown) {
        this.trendHighDown = trendHighDown;
    }

    public void setTrendLowUp(Integer trendLowUp) {
        this.trendLowUp = trendLowUp;
    }

    public void setTrendLowDown(Integer trendLowDown) {
        this.trendLowDown = trendLowDown;
    }

    public void setTrendCloseUp(Integer trendCloseUp) {
        this.trendCloseUp = trendCloseUp;
    }

    public void setTrendCloseDown(Integer trendCloseDown) {
        this.trendCloseDown = trendCloseDown;
    }

    public void setTrendTotalUp(Integer trendTotalUp) {
        this.trendTotalUp = trendTotalUp;
    }

    public void setTrendTotalBuyUp(Integer trendTotalBuyUp) {
        this.trendTotalBuyUp = trendTotalBuyUp;
    }

    public void setTrendTotalSellUp(Integer trendTotalSellUp) {
        this.trendTotalSellUp = trendTotalSellUp;
    }

    public void setTrendVolumeUp(Integer trendVolumeUp) {
        this.trendVolumeUp = trendVolumeUp;
    }

    public void setTrendVolumeBuyUp(Integer trendVolumeBuyUp) {
        this.trendVolumeBuyUp = trendVolumeBuyUp;
    }

    public void setTrendVolumeSellUp(Integer trendVolumeSellUp) {
        this.trendVolumeSellUp = trendVolumeSellUp;
    }

    public void setTrendTotalDown(Integer trendTotalDown) {
        this.trendTotalDown = trendTotalDown;
    }

    public void setTrendTotalBuyDown(Integer trendTotalBuyDown) {
        this.trendTotalBuyDown = trendTotalBuyDown;
    }

    public void setTrendTotalSellDown(Integer trendTotalSellDown) {
        this.trendTotalSellDown = trendTotalSellDown;
    }

    public void setTrendVolumeDown(Integer trendVolumeDown) {
        this.trendVolumeDown = trendVolumeDown;
    }

    public void setTrendVolumeBuyDown(Integer trendVolumeBuyDown) {
        this.trendVolumeBuyDown = trendVolumeBuyDown;
    }

    public void setTrendVolumeSellDown(Integer trendVolumeSellDown) {
        this.trendVolumeSellDown = trendVolumeSellDown;
    }

    public void setTrendCountUp(Integer trendCountUp) {
        this.trendCountUp = trendCountUp;
    }

    public void setTrendCountBuyUp(Integer trendCountBuyUp) {
        this.trendCountBuyUp = trendCountBuyUp;
    }

    public void setTrendCountSellUp(Integer trendCountSellUp) {
        this.trendCountSellUp = trendCountSellUp;
    }

    public void setTrendCountDown(Integer trendCountDown) {
        this.trendCountDown = trendCountDown;
    }

    public void setTrendCountBuyDown(Integer trendCountBuyDown) {
        this.trendCountBuyDown = trendCountBuyDown;
    }

    public void setTrendCountSellDown(Integer trendCountSellDown) {
        this.trendCountSellDown = trendCountSellDown;
    }

}
