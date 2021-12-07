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
package krakee.deep;

import java.util.ArrayList;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

/**
 * Store Core Row elements
 *
 * @author rgt
 */
public class DeepRowDTO {

    private ObjectId id;
    private String learnName;
    private String inputType;

    private ArrayList<String> inputColumnNames;
    private ArrayList<String> outputColumnNames;

    private ArrayList<Float> inputRow;
    private ArrayList<Float> outputRow;

    public DeepRowDTO() {
    }

    public DeepRowDTO(String learnName, String inputType, ArrayList<String> inputColumnNames, ArrayList<String> outputColumnNames, ArrayList<Float> inputRow, ArrayList<Float> outputRow) {
        this.learnName = learnName;
        this.inputType = inputType;
        this.inputColumnNames = inputColumnNames;
        this.outputColumnNames = outputColumnNames;
        this.inputRow = inputRow;
        this.outputRow = outputRow;
    }

    /**
     * Get column names 
     * Add input + output columns
     * @return 
     */
    @BsonIgnore
    public ArrayList<String> getColumnNames() {
        ArrayList<String> names = new ArrayList<>();
        names.addAll(this.getInputColumnNames());
        names.addAll(this.getOutputColumnNames());
        return names;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public ArrayList<Float> getInputRow() {
        return inputRow;
    }

    public void setInputRow(ArrayList<Float> inputRow) {
        this.inputRow = inputRow;
    }

    public ArrayList<Float> getOutputRow() {
        return outputRow;
    }

    public void setOutputRow(ArrayList<Float> outputRow) {
        this.outputRow = outputRow;
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

    public ArrayList<String> getInputColumnNames() {
        return inputColumnNames;
    }

    public void setInputColumnNames(ArrayList<String> inputColumnNames) {
        this.inputColumnNames = inputColumnNames;
    }

    public ArrayList<String> getOutputColumnNames() {
        return outputColumnNames;
    }

    public void setOutputColumnNames(ArrayList<String> outputColumnNames) {
        this.outputColumnNames = outputColumnNames;
    }

}
