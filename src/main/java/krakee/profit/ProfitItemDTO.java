/*
 * Copyright (C) 2020 rgt
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
package krakee.profit;

import java.util.Date;
import krakee.calc.CandleDTO;
import org.bson.types.ObjectId;

/**
 *
 * @author rgt
 */
public class ProfitItemDTO {

    //Kranken Fees
    private static final double MAKER = 0.16;
    private static final double TAKER = 0.26;

    private Long testNum;
    private Date startDate;
    private String trade;
    private double close;
    private double eur;
    private double btc;
    private double fee;

    public ProfitItemDTO() {
    }

    public ProfitItemDTO(CandleDTO candle, String trade, Long testNum) {
        this.startDate = candle.getStartDate();
        this.close = candle.getClose().doubleValue();
        this.trade = trade;
        this.eur = 0;
        this.btc = 0;
        this.fee = 0;
        this.testNum = testNum;
    }

    /**
     * Buy BTC
     *
     * @param eur
     */
    public void buyBtc(double eur) {
        //Calculate Kraken Marker Fee
        this.fee = (eur / 100) * MAKER;
        this.btc = (eur - this.fee) / this.close;
        this.eur = 0;
    }

    /**
     * Sell BTC
     *
     *
     * @param btc
     */
    public void sellBtc(double btc) {
        this.eur = (btc * this.close);

        //Calculate Kraken Taker Fee
        this.fee = (eur / 100) * TAKER;
        this.eur = this.eur - this.fee;

        this.btc = 0;
    }

    public Long getTestNum() {
        return testNum;
    }

    public void setTestNum(Long testNum) {
        this.testNum = testNum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getEur() {
        return eur;
    }

    public void setEur(double eur) {
        this.eur = eur;
    }

    public double getBtc() {
        return btc;
    }

    public void setBtc(double btc) {
        this.btc = btc;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
