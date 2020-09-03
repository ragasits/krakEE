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
package krakee.deep;

/**
 * Store Neural Network parameters and results
 *
 * @author rgt
 */
public class DeepDTO {

    private String learnName;
    private int numInputs;
    private int numOutputs;

    //Learning data
    private int sourceCount = 0;
    private int sourceBuy = 0;
    private int sourceSell = 0;

    private int trainCount = 0;
    private int trainBuy = 0;
    private int trainSell = 0;

    private int testCount = 0;
    private int testBuy = 0;
    private int testSell = 0;

    public int getTrainCount() {
        return trainCount;
    }

    public int getTrainBuy() {
        return trainBuy;
    }

    public int getTrainSell() {
        return trainSell;
    }

    public int getTestCount() {
        return testCount;
    }

    public int getTestBuy() {
        return testBuy;
    }

    public int getTestSell() {
        return testSell;
    }

    public int getSourceCount() {
        return sourceCount;
    }

    public int getSourceBuy() {
        return sourceBuy;
    }

    public int getSourceSell() {
        return sourceSell;
    }

    public void incTrainCount() {
        this.trainCount++;
    }

    public void incTrainBuy() {
        this.trainBuy++;
    }

    public void incTrainSell() {
        this.trainSell++;
    }

    public void incTestCount() {
        this.testCount++;
    }

    public void incTestBuy() {
        this.testBuy++;
    }

    public void incTestSell() {
        this.testSell++;
    }

    public void incSourceCount() {
        this.sourceCount++;
    }

    public void incSourceBuy() {
        this.sourceBuy++;
    }

    public void incSourceSell() {
        this.sourceSell++;
    }

    public String getLearnName() {
        return learnName;
    }

    public void setLearnName(String learnName) {
        this.learnName = learnName;
    }

    public int getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
    }

    public int getNumOutputs() {
        return numOutputs;
    }

    public void setNumOutputs(int numOutputs) {
        this.numOutputs = numOutputs;
    }
    
    

}
