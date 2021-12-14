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
package krakee.input;

import krakee.calc.CandleDTO;
import org.bson.types.ObjectId;

/**
 * Deep Learning input
 *
 * @author rgt
 */
public class InputDTO {

    private ObjectId id;
    private String learnName;
    private CandleDTO candle;
    private String trade;

    public InputDTO() {
    }

    public InputDTO(String learnName, CandleDTO candle, String trade) {
        this.learnName = learnName;
        this.candle = candle;
        this.trade = trade;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public CandleDTO getCandle() {
        return candle;
    }

    public void setCandle(CandleDTO candle) {
        this.candle = candle;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getLearnName() {
        return learnName;
    }

    public void setLearnName(String learnName) {
        this.learnName = learnName;
    }

}
