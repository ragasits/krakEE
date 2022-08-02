/*
 * Copyright (C) 2021 rgt
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
 * Store MACD values
 *
 * @author rgt
 */
public class MacdDTO {

    private boolean calcMacd = false;
    private BigDecimal macdLine = BigDecimal.ZERO;
    private BigDecimal signalLine = BigDecimal.ZERO;
    private BigDecimal macdHistogram = BigDecimal.ZERO;
    
    private boolean bullMarket = false;
    private boolean bearMarket = false; 
    private boolean crossover = false;

    public boolean isCalcMacd() {
        return calcMacd;
    }

    public void setCalcMacd(boolean calcMacd) {
        this.calcMacd = calcMacd;
    }

    public BigDecimal getMacdLine() {
        return macdLine;
    }

    public void setMacdLine(BigDecimal macdLine) {
        this.macdLine = macdLine;
    }

    public BigDecimal getSignalLine() {
        return signalLine;
    }

    public void setSignalLine(BigDecimal signalLine) {
        this.signalLine = signalLine;
    }

    public BigDecimal getMacdHistogram() {
        return macdHistogram;
    }

    public void setMacdHistogram(BigDecimal macdHistogram) {
        this.macdHistogram = macdHistogram;
    }  

    public boolean isBullMarket() {
        return bullMarket;
    }

    public void setBullMarket(boolean bullMarket) {
        this.bullMarket = bullMarket;
    }

    public boolean isBearMarket() {
        return bearMarket;
    }

    public void setBearMarket(boolean bearMarket) {
        this.bearMarket = bearMarket;
    }

    public boolean isCrossover() {
        return crossover;
    }

    public void setCrossover(boolean crossover) {
        this.crossover = crossover;
    }

    
    
}
