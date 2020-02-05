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

    private final boolean calcDelta;

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
    private final Integer deltaOpenUp;
    private final Integer deltaOpenDown;
    private final Integer deltaHighUp;
    private final Integer deltaHighDown;
    private final Integer deltaLowUp;
    private final Integer deltaLowDown;
    private final Integer deltaCloseUp;
    private final Integer deltaCloseDown;

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

        this.deltaOpenUp = 0;
        this.deltaOpenDown = 0;
        this.deltaHighUp = 0;
        this.deltaHighDown = 0;
        this.deltaLowUp = 0;
        this.deltaLowDown = 0;
        this.deltaCloseUp = 0;
        this.deltaCloseDown = 0;
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

        this.deltaOpenUp = doc.getInteger("deltaOpenUp");
        this.deltaOpenDown = doc.getInteger("deltaOpenDown");
        this.deltaHighUp = doc.getInteger("deltaHighUp");
        this.deltaHighDown = doc.getInteger("deltaHighDown");
        this.deltaLowUp = doc.getInteger("deltaLowUp");
        this.deltaLowDown = doc.getInteger("deltaLowDown");
        this.deltaCloseUp = doc.getInteger("deltaCloseUp");
        this.deltaCloseDown = doc.getInteger("deltaCloseDown");
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
        this.deltaVolumeBuy = last.getVolumeBuy().subtract(prev.getTotalBuy());
        this.deltaVolumeSell = last.getVolumeSell().subtract(prev.getVolumeSell());

        this.deltaOpenUp = this.calcTrendUp(last.getOpen(), prev.getOpen(), prev.getDelta().deltaOpenUp);
        this.deltaOpenDown = this.calcTrendDown(last.getOpen(), prev.getOpen(), prev.getDelta().deltaOpenDown);
        this.deltaHighUp = this.calcTrendUp(last.getHigh(), prev.getHigh(), prev.getDelta().deltaHighUp);
        this.deltaHighDown = this.calcTrendDown(last.getHigh(), prev.getHigh(), prev.getDelta().deltaHighDown);
        this.deltaLowUp = this.calcTrendUp(last.getLow(), prev.getLow(), prev.getDelta().deltaLowUp);
        this.deltaLowDown = this.calcTrendDown(last.getLow(), prev.getLow(), prev.getDelta().deltaLowDown);      
        this.deltaCloseUp = this.calcTrendUp(last.getClose(), prev.getClose(), prev.getDelta().deltaCloseUp);
        this.deltaCloseDown = this.calcTrendDown(last.getClose(), prev.getClose(), prev.getDelta().deltaCloseDown);
    }

    /**
     * Calculate Trend up value
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
     * Calculate Trend down value
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
                .append("deltaOpenUp", this.deltaOpenUp)
                .append("deltaOpenDown", this.deltaOpenDown)
                .append("deltaLowUp", this.deltaLowUp)
                .append("deltaLowDown", this.deltaLowDown)
                .append("deltaHighUp", this.deltaHighUp)
                .append("deltaHighDown", this.deltaHighDown)
                .append("deltaCloseUp", this.deltaCloseUp)
                .append("deltaCloseDown", this.deltaCloseDown);
    }

    public boolean isCalcDelta() {
        return calcDelta;
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

    public Integer getDeltaOpenUp() {
        return deltaOpenUp;
    }

    public Integer getDeltaOpenDown() {
        return deltaOpenDown;
    }

    public Integer getDeltaHighUp() {
        return deltaHighUp;
    }

    public Integer getDeltaHighDown() {
        return deltaHighDown;
    }

    public Integer getDeltaLowUp() {
        return deltaLowUp;
    }

    public Integer getDeltaLowDown() {
        return deltaLowDown;
    }

    public Integer getDeltaCloseUp() {
        return deltaCloseUp;
    }

    public Integer getDeltaCloseDown() {
        return deltaCloseDown;
    }
    
    
}
