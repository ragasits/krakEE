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

import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author rgt
 */
public class ProfitDTO {

    // Random values
    static final String BUY = "buy";
    static final String SELL = "sell";
    static final String NONE = "none";
    static final String[] OP = {BUY, SELL, NONE};

    private ObjectId id;
    private Long testNum;
    private String learnName;
    private double eur;
    private List<ProfitItemDTO> items;
    

    public ProfitDTO() {
    }
    
    public ProfitDTO(Long testNum, double eur, List<ProfitItemDTO> items) {
        this.testNum = testNum;
        this.eur = eur;
        this.items = items;
    }
    

    public ProfitDTO(String learnName, Long testNum, double eur, List<ProfitItemDTO> items) {
        this.testNum = testNum;
        this.eur = eur;
        this.items = items;
        this.learnName = learnName;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getTestNum() {
        return testNum;
    }

    public void setTestNum(Long testNum) {
        this.testNum = testNum;
    }

    public double getEur() {
        return eur;
    }

    public void setEur(double eur) {
        this.eur = eur;
    }

    public List<ProfitItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ProfitItemDTO> items) {
        this.items = items;
    }

    public String getLearnName() {
        return learnName;
    }

    public void setLearnName(String learnName) {
        this.learnName = learnName;
    }
}
