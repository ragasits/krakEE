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
package krakee.input;

import java.util.ArrayList;
import org.bson.types.ObjectId;

/**
 * Column data + Result of the analysis
 *
 * @author rgt
 */
public class InputStatDTO {

    private ObjectId id;
    private String learnName;
    private String inputType;

    private Integer columnId;
    private String columnName;
    private ArrayList<InputStatCountDTO> valueCounts;
    private Float valueAvg;
    private Float variance;
    
    private Integer uniqueCount;
    private Float uniquePercent;
    private ArrayList<String> resultList;      
    
    //Outliers - std
    private Float mean;
    private Float std;
    private Float cutOff;
    private Float lower;
    private Float upper;
    private String outliers;
    
    public InputStatDTO() {
    }

    public InputStatDTO(String learnName, String inputType, Integer columnId, String columnName) {
        this.learnName = learnName;
        this.inputType = inputType;
        this.columnId = columnId;
        this.columnName = columnName;
        this.resultList = new ArrayList<>();
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

    public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ArrayList<InputStatCountDTO> getValueCounts() {
        return valueCounts;
    }

    public void setValueCounts(ArrayList<InputStatCountDTO> valueCounts) {
        this.valueCounts = valueCounts;
    }

    public Integer getUniqueCount() {
        return uniqueCount;
    }

    public void setUniqueCount(Integer uniqueCount) {
        this.uniqueCount = uniqueCount;
    }

    public Float getUniquePercent() {
        return uniquePercent;
    }

    public void setUniquePercent(Float uniquePercent) {
        this.uniquePercent = uniquePercent;
    }

    public ArrayList<String> getResultList() {
        return resultList;
    }

    public void setResultList(ArrayList<String> resultList) {
        this.resultList = resultList;
    }

    public Float getValueAvg() {
        return valueAvg;
    }

    public void setValueAvg(Float valueAvg) {
        this.valueAvg = valueAvg;
    }

    public Float getVariance() {
        return variance;
    }

    public void setVariance(Float variance) {
        this.variance = variance;
    }

    public Float getMean() {
        return mean;
    }

    public void setMean(Float mean) {
        this.mean = mean;
    }

    public Float getStd() {
        return std;
    }

    public void setStd(Float std) {
        this.std = std;
    }

    public Float getCutOff() {
        return cutOff;
    }

    public void setCutOff(Float cutOff) {
        this.cutOff = cutOff;
    }

    public Float getLower() {
        return lower;
    }

    public void setLower(Float lower) {
        this.lower = lower;
    }

    public Float getUpper() {
        return upper;
    }

    public void setUpper(Float upper) {
        this.upper = upper;
    }

    public String getOutliers() {
        return outliers;
    }

    public void setOutliers(String outliers) {
        this.outliers = outliers;
    }

    
   
}
