/*
 * Copyright (C) 2021 Ragasits Csaba
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package krakee.calc;

import java.math.BigDecimal;

/**
 * Calculated Bollinger values
 *
 * @author rgt
 */
public class BollingerDTO {

    private boolean calcBollinger = false;

    //Standard Deviation
    private BigDecimal stDev = BigDecimal.ZERO;

    //Bollinger
    private BigDecimal bollingerUpper = BigDecimal.ZERO;
    private BigDecimal bollingerLower = BigDecimal.ZERO;
    private BigDecimal bollingerBandWidth = BigDecimal.ZERO;

    //Trade positions
    private BigDecimal tradeUpper = BigDecimal.ZERO;
    private BigDecimal tradeLower = BigDecimal.ZERO;

    private boolean bollingerBuy = false;
    private boolean bollingerSell = false;

    public boolean isCalcBollinger() {
        return calcBollinger;
    }

    public void setCalcBollinger(boolean calcBollinger) {
        this.calcBollinger = calcBollinger;
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

    public boolean isBollingerBuy() {
        return bollingerBuy;
    }

    public void setBollingerBuy(boolean bollingerBuy) {
        this.bollingerBuy = bollingerBuy;
    }

    public boolean isBollingerSell() {
        return bollingerSell;
    }

    public void setBollingerSell(boolean bollingerSell) {
        this.bollingerSell = bollingerSell;
    }

}
