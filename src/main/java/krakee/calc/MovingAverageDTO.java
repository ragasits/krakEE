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
 *
 * @author rgt
 */
public class MovingAverageDTO {

    private boolean calcMovingAverage = false;

    //Single Moving Average 20
    private BigDecimal sma20 = BigDecimal.ZERO;

    //Exponential Moving Average
    private BigDecimal ema9 = BigDecimal.ZERO;
    private BigDecimal ema12 = BigDecimal.ZERO;
    private BigDecimal ema26 = BigDecimal.ZERO;

    /**
     * Select EMA value
     *
     * @param limit
     * @return
     */
    public BigDecimal getEMA(int limit) {
        switch (limit) {
            case 9:
                return this.ema9;
            case 12:
                return this.ema12;
            case 26:
                return this.ema26;
            default:
                return BigDecimal.ZERO;
        }
    }

    public boolean isCalcMovingAverage() {
        return calcMovingAverage;
    }

    public void setCalcMovingAverage(boolean calcMovingAverage) {
        this.calcMovingAverage = calcMovingAverage;
    }

    public BigDecimal getSma20() {
        return sma20;
    }

    public void setSma20(BigDecimal sma20) {
        this.sma20 = sma20;
    }

    public BigDecimal getEma9() {
        return ema9;
    }

    public void setEma9(BigDecimal ema9) {
        this.ema9 = ema9;
    }

    public BigDecimal getEma12() {
        return ema12;
    }

    public void setEma12(BigDecimal ema12) {
        this.ema12 = ema12;
    }

    public BigDecimal getEma26() {
        return ema26;
    }

    public void setEma26(BigDecimal ema26) {
        this.ema26 = ema26;
    }

}
