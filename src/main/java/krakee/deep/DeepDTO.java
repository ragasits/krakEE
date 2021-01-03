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
import deepnetts.net.train.opt.OptimizerType;
import java.util.ArrayList;
import java.util.Arrays;
import javax.visrec.ml.eval.EvaluationMetrics;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

/**
 * Deep learning parameters
 *
 * @author rgt
 */
public class DeepDTO {

    private ObjectId id;
    private String deepName;
    private String learnName;

    private String inputType;
    private int numInputs;
    private int numOutputs;
    private ArrayList<String> columnNames;

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

    //Normalizer
    private String normalizerType;

    //Trainer   
    private float trainerMaxError = 0.7f;
    private float trainerLearningRate = 0.01f;
    private float trainerMomentum = 0.9f;
    private String optimizerType = OptimizerType.MOMENTUM.toString();

    //EvaluationMetrics
    private Float emAccuracy;
    private Float emPrecision;
    private Float emRecall;
    private Float emF1Score;

    //ConfusionMatrix
    private ArrayList<String> cmClassLabels;
    private ArrayList<ArrayList<Integer>> cmValues;

    /**
     * get ConfusionMatrix value
     *
     * @param rowIdx
     * @param colIdx
     * @return
     */
    public Integer getCmValue(Integer rowIdx, Integer colIdx) {
        return this.cmValues.get(rowIdx).get(colIdx);
    }

    /**
     * Store ConfusionMatrix values
     *
     * @param m
     */
    @BsonIgnore
    public void calcConfusionMatrix(ConfusionMatrix m) {
        this.cmClassLabels = new ArrayList(Arrays.asList(m.getClassLabels()));
        this.cmValues = new ArrayList<>();

        for (int i = 0; i < cmClassLabels.size(); i++) {
            ArrayList<Integer> rowValue = new ArrayList<>();
            for (int j = 0; j < cmClassLabels.size(); j++) {
                rowValue.add(m.get(i, j));
            }

            this.cmValues.add(rowValue);
        }
    }

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
    @BsonIgnore
    public void calcEvaluationMetrics(EvaluationMetrics em) {
        this.emAccuracy = getMetrics(em, "Accuracy");
        this.emPrecision = getMetrics(em, "Precision");
        this.emRecall = getMetrics(em, "Recall");
        this.emF1Score = getMetrics(em, "F1Score");
    }

    /**
     * Get enumList to DropDown
     *
     * @return
     */
    @BsonIgnore
    public OptimizerType[] getOptimizerTypes() {
        return OptimizerType.values();
    }

    @BsonIgnore
    public NormalizerType[] getNormalizerTypes() {
        return NormalizerType.values();
    }

    @BsonIgnore
    public InputType[] getInputTypes() {
        return InputType.values();
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

    public String getDeepName() {
        return deepName;
    }

    public void setDeepName(String deepName) {
        this.deepName = deepName;
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

    public int getSourceCount() {
        return sourceCount;
    }

    public void incSourceCount() {
        this.sourceCount++;
    }

    public int getSourceBuy() {
        return sourceBuy;
    }

    public void incSourceBuy() {
        this.sourceBuy++;
    }

    public int getSourceSell() {
        return sourceSell;
    }

    public void incSourceSell() {
        this.sourceSell++;
    }

    public int getTrainCount() {
        return trainCount;
    }

    public void incTrainCount() {
        this.trainCount++;
    }

    public int getTrainBuy() {
        return trainBuy;
    }

    public void incTrainBuy() {
        this.trainBuy++;
    }

    public int getTrainSell() {
        return trainSell;
    }

    public void incTrainSell() {
        this.trainSell++;
    }

    public int getTestCount() {
        return testCount;
    }

    public void incTestCount() {
        this.testCount++;
    }

    public int getTestBuy() {
        return testBuy;
    }

    public void incTestBuy() {
        this.testBuy++;
    }

    public int getTestSell() {
        return testSell;
    }

    public void incTestSell() {
        this.testSell++;
    }

    public ArrayList<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(ArrayList<String> columnNames) {
        this.columnNames = columnNames;
    }

    public Float getEmAccuracy() {
        return emAccuracy;
    }

    public void setEmAccuracy(Float emAccuracy) {
        this.emAccuracy = emAccuracy;
    }

    public Float getEmPrecision() {
        return emPrecision;
    }

    public void setEmPrecision(Float emPrecision) {
        this.emPrecision = emPrecision;
    }

    public Float getEmRecall() {
        return emRecall;
    }

    public void setEmRecall(Float emRecall) {
        this.emRecall = emRecall;
    }

    public Float getEmF1Score() {
        return emF1Score;
    }

    public void setEmF1Score(Float emF1Score) {
        this.emF1Score = emF1Score;
    }

    public ArrayList<String> getCmClassLabels() {
        return cmClassLabels;
    }

    public void setCmClassLabels(ArrayList<String> cmClassLabels) {
        this.cmClassLabels = cmClassLabels;
    }

    public ArrayList<ArrayList<Integer>> getCmValues() {
        return cmValues;
    }

    public void setCmValues(ArrayList<ArrayList<Integer>> cmValues) {
        this.cmValues = cmValues;
    }

    public float getTrainerMaxError() {
        return trainerMaxError;
    }

    public void setTrainerMaxError(float trainerMaxError) {
        this.trainerMaxError = trainerMaxError;
    }

    public float getTrainerLearningRate() {
        return trainerLearningRate;
    }

    public void setTrainerLearningRate(float trainerLearningRate) {
        this.trainerLearningRate = trainerLearningRate;
    }

    public float getTrainerMomentum() {
        return trainerMomentum;
    }

    public void setTrainerMomentum(float trainerMomentum) {
        this.trainerMomentum = trainerMomentum;
    }

    public String getOptimizerType() {
        return optimizerType;
    }

    public void setOptimizerType(String optimizerType) {
        this.optimizerType = optimizerType;
    }

    public String getNormalizerType() {
        return normalizerType;
    }

    public void setNormalizerType(String normalizerType) {
        this.normalizerType = normalizerType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

}
