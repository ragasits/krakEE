/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import java.math.BigDecimal;
import krakee.Common;

/**
 * Calculated Bollinger values
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

    //Trade positions
    private BigDecimal tradeUpper;
    private BigDecimal deltaTradeUpper;
    private Integer trendTradeUpperUp;
    private Integer trendTradeUpperDown;

    private BigDecimal tradeLower;
    private BigDecimal deltaTradeLower;
    private Integer trendTradeLowerUp;
    private Integer trendTradeLowerDown;

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

        this.tradeUpper = BigDecimal.ZERO;
        this.deltaTradeUpper = BigDecimal.ZERO;
        this.trendTradeUpperUp = 0;
        this.trendTradeUpperDown = 0;

        this.tradeLower = BigDecimal.ZERO;
        this.deltaTradeLower = BigDecimal.ZERO;
        this.trendTradeLowerUp = 0;
        this.trendTradeLowerDown = 0;

        this.tradeUpper = BigDecimal.ZERO;
        this.deltaTradeUpper = BigDecimal.ZERO;
        this.trendTradeUpperUp = 0;
        this.trendTradeUpperDown = 0;
    }

    /**
     * Calculate trend and delta
     *
     * @param prev
     */
    public void calcDeltaAndTrend(CandleDTO candle, BollingerDTO prev) {
        this.deltaSma = this.sma.subtract(prev.getSma());
        this.trendSmaUp = Common.calcTrendUp(this.sma, prev.getSma(), prev.getTrendSmaUp());
        this.trendSmaDown = Common.calcTrendDown(this.sma, prev.getSma(), prev.getTrendSmaDown());

        this.deltaStDev = this.stDev.subtract(prev.getStDev());
        this.trendStDevUp = Common.calcTrendUp(this.stDev, prev.getStDev(), prev.getTrendStDevUp());
        this.trendStDevDown = Common.calcTrendDown(this.stDev, prev.getStDev(), prev.getTrendStDevDown());

        this.deltaBollingerUpper = this.bollingerUpper.subtract(prev.getBollingerUpper());
        this.trendBollingerUpperUp = Common.calcTrendUp(this.bollingerUpper, prev.getBollingerUpper(), prev.getTrendBollingerUpperUp());
        this.trendBollingerUpperDown = Common.calcTrendDown(this.bollingerUpper, prev.getBollingerUpper(), prev.getTrendBollingerUpperDown());

        this.deltaBollingerLower = this.bollingerLower.subtract(prev.getBollingerLower());
        this.trendBollingerLowerUp = Common.calcTrendUp(this.bollingerLower, prev.getBollingerLower(), prev.getTrendBollingerLowerUp());
        this.trendBollingerLowerDown = Common.calcTrendDown(this.bollingerLower, prev.getBollingerLower(), prev.getTrendBollingerLowerDown());

        this.bollingerBandWith = this.bollingerUpper.subtract(this.bollingerLower);
        this.deltaBollingerBandWith = this.bollingerBandWith.subtract(prev.getBollingerBandWith());
        this.trendBollingerBandWithUp = Common.calcTrendUp(this.bollingerBandWith, prev.getBollingerBandWith(), prev.getTrendBollingerBandWithUp());
        this.trendBollingerBandWithDown = Common.calcTrendDown(this.bollingerBandWith, prev.getBollingerBandWith(), prev.getTrendBollingerBandWithDown());

        this.tradeUpper = this.calcTradeUpper(this.bollingerUpper, candle.getHigh());
        this.deltaTradeUpper = this.tradeUpper.subtract(prev.getTradeUpper());
        this.trendTradeUpperUp = Common.calcTrendUp(this.tradeUpper, prev.getTradeUpper(), prev.getTrendTradeUpperUp());
        this.trendTradeUpperDown = Common.calcTrendDown(this.tradeUpper, prev.getTradeUpper(), prev.getTrendTradeUpperDown());

        this.tradeLower = this.calcTradeLower(this.bollingerLower, candle.getLow());
        this.deltaTradeLower = this.tradeLower.subtract(prev.getTradeLower());
        this.trendTradeLowerUp = Common.calcTrendUp(this.tradeLower, prev.getTradeLower(), prev.getTrendTradeLowerUp());
        this.trendTradeLowerDown = Common.calcTrendDown(this.tradeLower, prev.getTradeLower(), prev.getTrendTradeLowerDown());

    }

    /**
     * Calculate trade upper value The Candle High value more then bollinger
     * upper
     *
     * @param bl - Boolinger upper
     * @param high - Candle high
     * @return
     */
    private BigDecimal calcTradeUpper(BigDecimal bl, BigDecimal high) {
        if (high.compareTo(bl) == 1) {
            return high.subtract(bl);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calculate trade lower value The candle lower is less then bollinger lower
     *
     * @param bl - Bollonger lower
     * @param low candle.low
     * @return
     */
    private BigDecimal calcTradeLower(BigDecimal bl, BigDecimal low) {
        if (low.compareTo(bl) == -1) {
            return bl.subtract(low);
        }
        return BigDecimal.ZERO;
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

    public BigDecimal getTradeUpper() {
        return tradeUpper;
    }

    public BigDecimal getDeltaTradeUpper() {
        return deltaTradeUpper;
    }

    public Integer getTrendTradeUpperUp() {
        return trendTradeUpperUp;
    }

    public Integer getTrendTradeUpperDown() {
        return trendTradeUpperDown;
    }

    public BigDecimal getTradeLower() {
        return tradeLower;
    }

    public BigDecimal getDeltaTradeLower() {
        return deltaTradeLower;
    }

    public Integer getTrendTradeLowerUp() {
        return trendTradeLowerUp;
    }

    public Integer getTrendTradeLowerDown() {
        return trendTradeLowerDown;
    }

    public void setDeltaSma(BigDecimal deltaSma) {
        this.deltaSma = deltaSma;
    }

    public void setTrendSmaUp(Integer trendSmaUp) {
        this.trendSmaUp = trendSmaUp;
    }

    public void setTrendSmaDown(Integer trendSmaDown) {
        this.trendSmaDown = trendSmaDown;
    }

    public void setDeltaStDev(BigDecimal deltaStDev) {
        this.deltaStDev = deltaStDev;
    }

    public void setTrendStDevUp(Integer trendStDevUp) {
        this.trendStDevUp = trendStDevUp;
    }

    public void setTrendStDevDown(Integer trendStDevDown) {
        this.trendStDevDown = trendStDevDown;
    }

    public void setDeltaBollingerUpper(BigDecimal deltaBollingerUpper) {
        this.deltaBollingerUpper = deltaBollingerUpper;
    }

    public void setTrendBollingerUpperUp(Integer trendBollingerUpperUp) {
        this.trendBollingerUpperUp = trendBollingerUpperUp;
    }

    public void setTrendBollingerUpperDown(Integer trendBollingerUpperDown) {
        this.trendBollingerUpperDown = trendBollingerUpperDown;
    }

    public void setDeltaBollingerLower(BigDecimal deltaBollingerLower) {
        this.deltaBollingerLower = deltaBollingerLower;
    }

    public void setTrendBollingerLowerUp(Integer trendBollingerLowerUp) {
        this.trendBollingerLowerUp = trendBollingerLowerUp;
    }

    public void setTrendBollingerLowerDown(Integer trendBollingerLowerDown) {
        this.trendBollingerLowerDown = trendBollingerLowerDown;
    }

    public void setBollingerBandWith(BigDecimal bollingerBandWith) {
        this.bollingerBandWith = bollingerBandWith;
    }

    public void setDeltaBollingerBandWith(BigDecimal deltaBollingerBandWith) {
        this.deltaBollingerBandWith = deltaBollingerBandWith;
    }

    public void setTrendBollingerBandWithUp(Integer trendBollingerBandWithUp) {
        this.trendBollingerBandWithUp = trendBollingerBandWithUp;
    }

    public void setTrendBollingerBandWithDown(Integer trendBollingerBandWithDown) {
        this.trendBollingerBandWithDown = trendBollingerBandWithDown;
    }

    public void setTradeUpper(BigDecimal tradeUpper) {
        this.tradeUpper = tradeUpper;
    }

    public void setDeltaTradeUpper(BigDecimal deltaTradeUpper) {
        this.deltaTradeUpper = deltaTradeUpper;
    }

    public void setTrendTradeUpperUp(Integer trendTradeUpperUp) {
        this.trendTradeUpperUp = trendTradeUpperUp;
    }

    public void setTrendTradeUpperDown(Integer trendTradeUpperDown) {
        this.trendTradeUpperDown = trendTradeUpperDown;
    }

    public void setTradeLower(BigDecimal tradeLower) {
        this.tradeLower = tradeLower;
    }

    public void setDeltaTradeLower(BigDecimal deltaTradeLower) {
        this.deltaTradeLower = deltaTradeLower;
    }

    public void setTrendTradeLowerUp(Integer trendTradeLowerUp) {
        this.trendTradeLowerUp = trendTradeLowerUp;
    }

    public void setTrendTradeLowerDown(Integer trendTradeLowerDown) {
        this.trendTradeLowerDown = trendTradeLowerDown;
    }
    
    

}
