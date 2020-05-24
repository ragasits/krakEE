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
package krakee.learn;

import java.util.Date;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

/**
 * Store Learning data
 * @author rgt
 */
public class LearnDTO {
    private ObjectId id;
    private String name;
    private Date startDate;
    private ObjectId candleId;
    private String trade;

    public LearnDTO() {  
    }
    
    /**
     * Get trade name
     * @return 
     */
    @BsonIgnore
    public String getTradeName(){
        if (this.trade==null){
            return null;
        }
        
        switch (this.trade) {
            case "b":
                return "Buy";
            case "s":
                return "Sell";
            default:
                return "";
        }
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getCandleId() {
        return candleId;
    }

    public void setCandleId(ObjectId candleId) {
        this.candleId = candleId;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    
    
}
