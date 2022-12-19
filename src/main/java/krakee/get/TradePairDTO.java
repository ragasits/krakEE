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
package krakee.get;

import java.math.BigDecimal;
import java.util.Date;
import org.bson.types.ObjectId;

/**
 * DTO for Kraken trade pairs
 *
 * @author rgt
 */
public class TradePairDTO {

    private ObjectId id;
    
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal time;
    private String buySel;
    private String marketLimit;
    private String miscellaneous;

    private String error;
    private String last;
    private String pair;

    public TradePairDTO() {
    }

    public TradePairDTO(BigDecimal price, BigDecimal volume, BigDecimal time, String buySel, String marketLimit, String miscellaneous, String error, String last, String pair) {
        this.price = price;
        this.volume = volume;
        this.time = time;
        this.buySel = buySel;
        this.marketLimit = marketLimit;
        this.miscellaneous = miscellaneous;
        this.error = error;
        this.last = last;
        this.pair = pair;
    }

    /**
     * Convert Time value to Date
     *
     * @return
     */
    public Date getTimeDate() {
        Date date = new Date();
        long milis = time.multiply(BigDecimal.valueOf(1000)).longValue();
        date.setTime(milis);
        return date;
    }

    /**
     * Convert Last value to Date
     *
     * @return
     */
    public Date getLastDate() {
        Date date = new Date();
        date.setTime(Long.parseLong(this.last.substring(0, 13)));
        return date;
    }

    /**
     * calculate total value
     *
     * @return
     */
    public BigDecimal getTotal() {
        return this.price.multiply(this.volume);
    }

    public BigDecimal getTime() {
        return time;
    }

    public String getLast() {
        return last;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getBuySel() {
        return buySel;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "TradePairDTO{" + "price=" + price + ", volume=" + volume + ", time=" + time + ", buySel=" + buySel + ", marketLimit=" + marketLimit + ", miscellaneous=" + miscellaneous + ", error=" + error + ", last=" + last + ", pair=" + pair + '}';
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public void setTime(BigDecimal time) {
        this.time = time;
    }

    public void setBuySel(String buySel) {
        this.buySel = buySel;
    }

    public void setMarketLimit(String marketLimit) {
        this.marketLimit = marketLimit;
    }

    public void setMiscellaneous(String miscellaneous) {
        this.miscellaneous = miscellaneous;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMarketLimit() {
        return marketLimit;
    }

    public String getMiscellaneous() {
        return miscellaneous;
    }

    public String getError() {
        return error;
    }

    public String getPair() {
        return pair;
    }
    
    

}
