/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import java.math.BigDecimal;

/**
 * Calculated Bollinger values
 *
 * @author rgt
 */
public class BollingerDTO {

    private boolean calcBollinger;
    //Single Moving Average
    private BigDecimal sma;

    //Standard Deviation
    private BigDecimal stDev;

    //Bollinger
    private BigDecimal bollingerUpper;
    private BigDecimal bollingerLower;
    private BigDecimal bollingerBandWidth;

    //Trade positions
    private BigDecimal tradeUpper;
    private BigDecimal tradeLower;

    public BollingerDTO() {
        this.calcBollinger = false;
        this.sma = BigDecimal.ZERO;
        this.stDev = BigDecimal.ZERO;
        this.bollingerUpper = BigDecimal.ZERO;
        this.bollingerLower = BigDecimal.ZERO;
        this.bollingerBandWidth = BigDecimal.ZERO;
        this.tradeUpper = BigDecimal.ZERO;
        this.tradeLower = BigDecimal.ZERO;
        this.tradeUpper = BigDecimal.ZERO;
    }

    /**
     * Calculate trend and delta
     *
     * @param candle
     * @param prev
     */
    public void calcDeltaAndTrend(CandleDTO candle, BollingerDTO prev) {
        this.bollingerBandWidth = this.bollingerUpper.subtract(this.bollingerLower);
        this.tradeUpper = this.calcTradeUpper(this.bollingerUpper, candle.getHigh());
        this.tradeLower = this.calcTradeLower(this.bollingerLower, candle.getLow());
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
     * @param bl - Bollinger lower
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

    public BigDecimal getBollingerBandWidth() {
        return bollingerBandWidth;
    }

    public BigDecimal getTradeUpper() {
        return tradeUpper;
    }

    public BigDecimal getTradeLower() {
        return tradeLower;
    }

    public void setBollingerBandWidth(BigDecimal bollingerBandWith) {
        this.bollingerBandWidth = bollingerBandWith;
    }

    public void setTradeUpper(BigDecimal tradeUpper) {
        this.tradeUpper = tradeUpper;
    }

    public void setTradeLower(BigDecimal tradeLower) {
        this.tradeLower = tradeLower;
    }

}
