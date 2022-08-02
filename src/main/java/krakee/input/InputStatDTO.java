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
    private Float meanStd;
    private Float std;
    private Float cutOffStd;
    private Float lowerStd;
    private Float upperStd;
    private ArrayList<Float> outliersStd;
    
    //Outliers - Interquartile Range Method
    private Float q25Iqr;
    private Float q75Iqr;
    private Float irqIqr;
    private Float cutOffIqr;
    private Float lowerIqr;
    private Float upperIqr;
    private ArrayList<Float> outliersIqr;    
    
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

    public Float getMeanStd() {
        return meanStd;
    }

    public void setMeanStd(Float meanStd) {
        this.meanStd = meanStd;
    }

    public Float getStd() {
        return std;
    }

    public void setStd(Float std) {
        this.std = std;
    }

    public Float getCutOffStd() {
        return cutOffStd;
    }

    public void setCutOffStd(Float cutOffStd) {
        this.cutOffStd = cutOffStd;
    }

    public Float getLowerStd() {
        return lowerStd;
    }

    public void setLowerStd(Float lowerStd) {
        this.lowerStd = lowerStd;
    }

    public Float getUpperStd() {
        return upperStd;
    }

    public void setUpperStd(Float upperStd) {
        this.upperStd = upperStd;
    }

    public ArrayList<Float> getOutliersStd() {
        return outliersStd;
    }

    public void setOutliersStd(ArrayList<Float> outliersStd) {
        this.outliersStd = outliersStd;
    }

    public Float getQ25Iqr() {
        return q25Iqr;
    }

    public void setQ25Iqr(Float q25Iqr) {
        this.q25Iqr = q25Iqr;
    }

    public Float getQ75Iqr() {
        return q75Iqr;
    }

    public void setQ75Iqr(Float q75Iqr) {
        this.q75Iqr = q75Iqr;
    }

    public Float getIrqIqr() {
        return irqIqr;
    }

    public void setIrqIqr(Float irqIqr) {
        this.irqIqr = irqIqr;
    }

    public Float getCutOffIqr() {
        return cutOffIqr;
    }

    public void setCutOffIqr(Float cutOffIqr) {
        this.cutOffIqr = cutOffIqr;
    }

    public Float getLowerIqr() {
        return lowerIqr;
    }

    public void setLowerIqr(Float lowerIqr) {
        this.lowerIqr = lowerIqr;
    }

    public Float getUpperIqr() {
        return upperIqr;
    }

    public void setUpperIqr(Float upperIqr) {
        this.upperIqr = upperIqr;
    }

    public ArrayList<Float> getOutliersIqr() {
        return outliersIqr;
    }

    public void setOutliersIqr(ArrayList<Float> outliersIqr) {
        this.outliersIqr = outliersIqr;
    }
}
