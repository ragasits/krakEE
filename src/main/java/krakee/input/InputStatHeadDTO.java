/*
 * Copyright (C) 2022 rgt
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

import java.util.ArrayList;
import org.bson.types.ObjectId;

/**
 * Store input Stat global data
 * @author rgt
 */
public class InputStatHeadDTO {

    private ObjectId id;
    private String learnName;
    private String inputType;
    
    private ArrayList<ThresholdDTO> thresholds;
    private ArrayList<InputRowDTO> inputRows;

    public InputStatHeadDTO() {
    }
    
    public InputStatHeadDTO(String learnName, String inputType) {
        this.learnName = learnName;
        this.inputType = inputType;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getLearnName() {
        return learnName;
    }

    public void setLearnName(String learnName) {
        this.learnName = learnName;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public ArrayList<ThresholdDTO> getThresholds() {
        return thresholds;
    }

    public void setThresholds(ArrayList<ThresholdDTO> thresholds) {
        this.thresholds = thresholds;
    }

    public ArrayList<InputRowDTO> getInputRows() {
        return inputRows;
    }

    public void setInputRows(ArrayList<InputRowDTO> inputRows) {
        this.inputRows = inputRows;
    }
    
    
}