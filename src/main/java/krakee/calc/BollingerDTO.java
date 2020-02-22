/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import java.math.BigDecimal;
import krakee.Common;
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
    private Integer trendSmaUp;
    private Integer trendSmaDown;

    //Standard Deviation
    private BigDecimal stDev;
    private BigDecimal deltaStDev;
    private Integer trendStDevUp;
    private Integer trendStDevDown;

    //Bollinger
    private BigDecimal bollingerUpper;
    private BigDecimal deltaBollingerUpper;
    private Integer trendBollingerUpperUp;
    private Integer trendBollingerUpperDown;

    private BigDecimal bollingerLower;
    private BigDecimal deltaBollingerLower;
    private Integer trendBollingerLowerUp;
    private Integer trendBollingerLowerDown;

    private BigDecimal bollingerBandWith;
    private BigDecimal deltaBollingerBandWith;
    private Integer trendBollingerBandWithUp;
    private Integer trendBollingerBandWithDown;    

    public BollingerDTO() {
        this.calcBollinger = false;
        this.sma = BigDecimal.ZERO;
        this.deltaSma = BigDecimal.ZERO;
        this.trendSmaUp = 0;
        this.trendSmaDown = 0;

        this.stDev = BigDecimal.ZERO;
        this.deltaStDev = BigDecimal.ZERO;
        this.trendStDevUp = 0;
        this.trendStDevDown = 0;

        this.bollingerUpper = BigDecimal.ZERO;
        this.deltaBollingerUpper = BigDecimal.ZERO;
        this.trendBollingerUpperUp = 0;
        this.trendBollingerUpperDown = 0;

        this.bollingerLower = BigDecimal.ZERO;
        this.deltaBollingerLower = BigDecimal.ZERO;
        this.trendBollingerLowerUp = 0;
        this.trendBollingerLowerDown = 0;

        this.bollingerBandWith = BigDecimal.ZERO;
        this.deltaBollingerBandWith = BigDecimal.ZERO;
        this.trendBollingerBandWithUp = 0;
        this.trendBollingerBandWithDown = 0;        

    }

    public BollingerDTO(Document doc) {
        this.calcBollinger = doc.getBoolean("calcBollinger");

        this.sma = ((Decimal128) doc.get("sma")).bigDecimalValue();
        this.deltaSma = ((Decimal128) doc.get("deltaSma")).bigDecimalValue();
        this.trendSmaUp = doc.getInteger("trendSmaUp");
        this.trendSmaDown = doc.getInteger("trendSmaDown");

        this.stDev = ((Decimal128) doc.get("stDev")).bigDecimalValue();
        this.deltaStDev = ((Decimal128) doc.get("deltaStDev")).bigDecimalValue();
        this.trendStDevUp = doc.getInteger("trendStDevUp");
        this.trendStDevDown = doc.getInteger("trendStDevDown");

        this.bollingerUpper = ((Decimal128) doc.get("bollingerUpper")).bigDecimalValue();
        this.deltaBollingerUpper = ((Decimal128) doc.get("deltaBollingerUpper")).bigDecimalValue();
        this.trendBollingerUpperUp = doc.getInteger("trendBollingerUpperUp");
        this.trendBollingerUpperDown = doc.getInteger("trendBollingerUpperDown");

        this.bollingerLower = ((Decimal128) doc.get("bollingerLower")).bigDecimalValue();
        this.deltaBollingerLower = ((Decimal128) doc.get("deltaBollingerLower")).bigDecimalValue();
        this.trendBollingerLowerUp = doc.getInteger("trendBollingerLowerUp");
        this.trendBollingerLowerDown = doc.getInteger("trendBollingerLowerDown");

        this.bollingerBandWith = ((Decimal128) doc.get("bollingerBandWith")).bigDecimalValue();
        this.deltaBollingerBandWith = ((Decimal128) doc.get("deltaBollingerBandWith")).bigDecimalValue();
        this.trendBollingerBandWithUp = doc.getInteger("trendBollingerBandWithUp");
        this.trendBollingerBandWithDown = doc.getInteger("trendBollingerBandWithDown");        
    }

    public Document getBollinger() {
        return new Document("calcBollinger", this.calcBollinger)
                .append("sma", this.sma)
                .append("deltaSma", this.deltaSma)
                .append("trendSmaUp", this.trendSmaUp)
                .append("trendSmaDown", this.trendSmaDown)
                .append("stDev", this.stDev)
                .append("deltaStDev", this.deltaStDev)
                .append("trendStDevUp", this.trendStDevUp)
                .append("trendStDevDown", this.trendStDevDown)
                .append("bollingerUpper", this.bollingerUpper)                
                .append("deltaBollingerUpper", this.deltaBollingerUpper)
                .append("trendBollingerUpperUp", this.trendBollingerUpperUp)
                .append("trendBollingerUpperDown", this.trendBollingerUpperDown)
                .append("bollingerLower", this.bollingerLower)
                .append("deltaBollingerLower", this.deltaBollingerLower)
                .append("trendBollingerLowerUp", this.trendBollingerLowerUp)
                .append("trendBollingerLowerDown", this.trendBollingerLowerDown)
                .append("bollingerBandWith", this.bollingerBandWith)
                .append("deltaBollingerBandWith", this.deltaBollingerBandWith)
                .append("trendBollingerBandWithUp", this.trendBollingerBandWithUp)
                .append("trendBollingerBandWithDown", this.trendBollingerBandWithDown)                
                ;
    }

    /**
     * Calculate trend and delta
     *
     * @param prev
     */
    public void calcDeltaAndTrend(BollingerDTO prev) {
        this.deltaSma = this.sma.subtract(prev.getSma());
        this.trendSmaUp = Common.calcTrendUp(this.sma, prev.getSma(), prev.getTrendSmaUp());
        this.trendSmaDown = Common.calcTrendDown(this.sma, prev.getSma(), prev.getTrendSmaDown());

        this.deltaStDev = this.stDev.subtract(prev.getStDev());
        this.trendStDevUp = Common.calcTrendUp(this.stDev, prev.getStDev(), prev.getTrendStDevUp());
        this.trendStDevDown = Common.calcTrendDown(this.stDev, prev.getStDev(), prev.getTrendStDevDown());

        this.deltaBollingerUpper = this.deltaBollingerUpper.subtract(prev.getBollingerUpper());
        this.trendBollingerUpperUp = Common.calcTrendUp(this.bollingerUpper, prev.getBollingerUpper(), prev.getTrendBollingerUpperUp());
        this.trendBollingerUpperDown = Common.calcTrendDown(this.bollingerUpper, prev.getBollingerUpper(), prev.getTrendBollingerUpperDown());
        
        this.deltaBollingerLower = this.bollingerLower.subtract(prev.getBollingerLower());
        this.trendBollingerLowerUp = Common.calcTrendUp(this.bollingerLower, prev.getBollingerLower(), prev.getTrendBollingerLowerUp());
        this.trendBollingerLowerDown = Common.calcTrendDown(this.bollingerLower, prev.getBollingerLower(), prev.getTrendBollingerLowerDown());        
        
        this.bollingerBandWith = this.bollingerUpper.subtract(this.bollingerLower);
        this.deltaBollingerBandWith = this.bollingerBandWith.subtract(prev.getBollingerBandWith());
        this.trendBollingerBandWithUp = Common.calcTrendUp(this.bollingerBandWith, prev.getBollingerBandWith(), prev.getTrendBollingerBandWithUp());
        this.trendBollingerBandWithDown = Common.calcTrendDown(this.bollingerBandWith, prev.getBollingerBandWith(), prev.getTrendBollingerBandWithDown());        
        
        
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

    public Integer getTrendSmaUp() {
        return trendSmaUp;
    }

    public Integer getTrendSmaDown() {
        return trendSmaDown;
    }

    public BigDecimal getStDev() {
        return stDev;
    }

    public void setStDev(BigDecimal stDev) {
        this.stDev = stDev;
    }

    public BigDecimal getBollingerUpper() {
        return bollingerUpper;
    }

    public void setBollingerUpper(BigDecimal bollingerUpper) {
        this.bollingerUpper = bollingerUpper;
    }

    public BigDecimal getBollingerLower() {
        return bollingerLower;
    }

    public void setBollingerLower(BigDecimal bollingerLower) {
        this.bollingerLower = bollingerLower;
    }

    public BigDecimal getDeltaStDev() {
        return deltaStDev;
    }

    public Integer getTrendStDevUp() {
        return trendStDevUp;
    }

    public Integer getTrendStDevDown() {
        return trendStDevDown;
    }

    public BigDecimal getDeltaBollingerUpper() {
        return deltaBollingerUpper;
    }

    public Integer getTrendBollingerUpperUp() {
        return trendBollingerUpperUp;
    }

    public Integer getTrendBollingerUpperDown() {
        return trendBollingerUpperDown;
    }

    public BigDecimal getDeltaBollingerLower() {
        return deltaBollingerLower;
    }

    public Integer getTrendBollingerLowerUp() {
        return trendBollingerLowerUp;
    }

    public Integer getTrendBollingerLowerDown() {
        return trendBollingerLowerDown;
    }

    public BigDecimal getBollingerBandWith() {
        return bollingerBandWith;
    }

    public BigDecimal getDeltaBollingerBandWith() {
        return deltaBollingerBandWith;
    }

    public Integer getTrendBollingerBandWithUp() {
        return trendBollingerBandWithUp;
    }

    public Integer getTrendBollingerBandWithDown() {
        return trendBollingerBandWithDown;
    }
    
    

}
