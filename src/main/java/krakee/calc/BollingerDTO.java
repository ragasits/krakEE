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
public class BollingerDTO {

    private boolean calcBollinger;
    //Single Moving Average
    private BigDecimal sma;
    private BigDecimal deltaSma;
    //Bollinger
    private BigDecimal bollingerUpper;
    private BigDecimal bollingerLower;
    private BigDecimal bollingerBandWith;

    public BollingerDTO() {
        this.calcBollinger = false;
        this.sma = BigDecimal.ZERO;
        this.deltaSma = BigDecimal.ZERO;
        this.bollingerUpper = BigDecimal.ZERO;
        this.bollingerLower = BigDecimal.ZERO;
        this.bollingerBandWith = BigDecimal.ZERO;
    }

    public BollingerDTO(Document doc) {
        this.calcBollinger = doc.getBoolean("calcBollinger");
        this.sma = ((Decimal128) doc.get("sma")).bigDecimalValue();
        this.deltaSma = ((Decimal128) doc.get("deltaSma")).bigDecimalValue();
        this.bollingerUpper = ((Decimal128) doc.get("bollingerUpper")).bigDecimalValue();
        this.bollingerLower = ((Decimal128) doc.get("bollingerLower")).bigDecimalValue();
        this.bollingerBandWith = ((Decimal128) doc.get("bollingerBandWith")).bigDecimalValue();
    }

    public Document getBollinger() {
        return new Document("calcBollinger", this.calcBollinger)
                .append("sma", this.sma)
                .append("deltaSma", this.deltaSma)
                .append("bollingerUpper", this.bollingerUpper)
                .append("bollingerLower", this.bollingerLower)
                .append("bollingerBandWith", this.bollingerBandWith);
    }

    public void calcDelta(BollingerDTO prev) {
        this.deltaSma = this.sma.subtract(prev.getSma());
    }

    public boolean isCalcBollinger() {
        return calcBollinger;
    }

    public void setCalcBollinger(boolean calcBollinger) {
        this.calcBollinger = calcBollinger;
    }

    public BigDecimal getSma() {
        return sma;
    }

    public void setSma(BigDecimal sma) {
        this.sma = sma;
    }

    public BigDecimal getDeltaSma() {
        return deltaSma;
    }

    public BigDecimal getBollingerUpper() {
        return bollingerUpper;
    }

    public BigDecimal getBollingerLower() {
        return bollingerLower;
    }

    public BigDecimal getBollingerBandWith() {
        return bollingerBandWith;
    }

    
}
