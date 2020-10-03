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

import deepnetts.eval.ConfusionMatrix;
import javax.visrec.ml.eval.EvaluationMetrics;

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

    //EvaluationMetrics
    private Float emAccuracy;
    private Float emPrecision;
    private Float emRecall;
    private Float emF1Score;

    //ConfusionMatrix
    private String[] cmClassLabels;
    private int[][] cmValues;

    /**
     * Get metrics value, manage nulls
     *
     * @param em
     * @param key
     * @return
     */
    private Float getMetrics(EvaluationMetrics em, String key) {
        try {
            return em.get(key);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Store EvaluationMetrics values
     *
     * @param em 
     */
    public void setEvaluationMetrics(EvaluationMetrics em) {
        this.emAccuracy = getMetrics(em, "Accuracy");
        this.emPrecision = getMetrics(em, "Precision");
        this.emRecall = getMetrics(em, "Recall");
        this.emF1Score = getMetrics(em, "F1Score");
    }

    /**
     * Store ConfusionMatrix values
     * @param m 
     */
    public void setConfusionMatrix(ConfusionMatrix m) {
        this.cmClassLabels = m.getClassLabels();
        this.cmValues = new int[this.cmClassLabels.length][this.cmClassLabels.length];

        for (int i = 0; i < cmClassLabels.length; i++) {
            for (int j = 0; j < cmClassLabels.length; j++) {
                this.cmValues[i][j] = m.get(i, j);
            }
        }
    }

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

    public Float getEmAccuracy() {
        return emAccuracy;
    }

    public Float getEmPrecision() {
        return emPrecision;
    }

    public Float getEmRecall() {
        return emRecall;
    }

    public Float getEmF1Score() {
        return emF1Score;
    }

    public String[] getCmClassLabels() {
        return cmClassLabels;
    }

    public int[][] getCmValues() {
        return cmValues;
    }


    
    

}
