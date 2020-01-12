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
                .append("deltaVolumeSell", this.deltaVolumeSell);
    }

}
