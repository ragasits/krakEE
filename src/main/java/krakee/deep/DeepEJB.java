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

import deepnetts.data.TabularDataSet;
import deepnetts.data.norm.MaxNormalizer;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.get.TradeEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;
import krakee.MyException;

/**
 * Manage neural network
 *
 * @author rgt
 */
@Stateless
public class DeepEJB {

    //Column indexes
    static final int BUYPOS = 0;
    static final int SELLPOS = 1;

    static final Logger LOGGER = Logger.getLogger(TradeEJB.class.getCanonicalName());

    @EJB
    LearnEJB learnEjb;
    @EJB
    CandleEJB candleEjb;

    /**
     * Count train cases
     *
     * @param dto
     * @param dataSet
     * @return
     */
    private DeepDTO calcTrainCount(DeepDTO dto, TabularDataSet dataSet) {
        List<TabularDataSet.Item> itemList = dataSet.getItems();
        for (TabularDataSet.Item item : itemList) {
            dto.incTrainCount();

            if (item.getTargetOutput().get(BUYPOS) > 0f) {
                dto.incTrainBuy();
            } else if (item.getTargetOutput().get(SELLPOS) > 0f) {
                dto.incTrainSell();
            }
        }
        return dto;
    }

    /**
     * Count test cases
     *
     * @param dto
     * @param dataSet
     * @return
     */
    private DeepDTO calcTestCount(DeepDTO dto, TabularDataSet dataSet) {
        List<TabularDataSet.Item> itemList = dataSet.getItems();
        for (TabularDataSet.Item item : itemList) {
            dto.incTestCount();

            if (item.getTargetOutput().get(BUYPOS) > 0f) {
                dto.incTestBuy();
            } else if (item.getTargetOutput().get(SELLPOS) > 0f) {
                dto.incTestSell();
            }
        }
        return dto;
    }

    /**
     * Learn and test Neural network
     *
     * @param dto
     * @throws krakee.MyException
     */
    public void learndDl(DeepDTO dto) throws MyException {
        //Get dataset
        TabularDataSet dataSet = dto.getDataset();

        //Normalize data
        MaxNormalizer norm = new MaxNormalizer(dataSet);
        norm.normalize(dataSet);

        DataSet[] trainTestSet = dataSet.split(0.6, 0.4);

        //Create statistics
        dto = this.calcTrainCount(dto, (TabularDataSet) trainTestSet[0]);
        dto = this.calcTestCount(dto, (TabularDataSet) trainTestSet[1]);

        // create instance of multi addLayer percetpron using builder
        FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                .addInputLayer(dto.getNumInputs())
                .addFullyConnectedLayer(50, ActivationType.TANH)
                .addFullyConnectedLayer(50, ActivationType.TANH)
                .addOutputLayer(dto.getNumOutputs(), ActivationType.SOFTMAX)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(456)
                .build();

        // create and configure instanceof backpropagation trainer
        BackpropagationTrainer trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.7f);
        trainer.setLearningRate(0.01f);
        trainer.setMomentum(0.9f);
        trainer.setOptimizer(OptimizerType.MOMENTUM);

        neuralNet.train(trainTestSet[0]);

        // evaluate/test classifier
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        EvaluationMetrics em = evaluator.evaluate(neuralNet, trainTestSet[1]);
        dto.setEvaluationMetrics(em);

        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        dto.setConfusionMatrix(cm);
    }

    /**
     * Create and store input, out values from the Candles and the Learning data
     * @param deep
     * @throws MyException 
     */
    public void createDlValues(DeepDTO deep) throws MyException {
        if (deep == null || deep.getLearnName() == null || deep.getLearnName().isEmpty()) {
            throw new MyException("Missing: learnname");
        }

        //Get Learning data
        LearnDTO firstLearn = learnEjb.getFirst(deep.getLearnName());
        LearnDTO lastLearn = learnEjb.getLast(deep.getLearnName());

        //Get Candles
        List<CandleDTO> candleList = candleEjb.get(firstLearn.getStartDate(), lastLearn.getStartDate());
        CandleDTO c = candleEjb.get(firstLearn.getStartDate());
        deep.setNumInputs(c.toValueList().size());
        deep.setNumOutputs(2);

        //Add column names
        ArrayList<String> columnNames = c.toColumnNameList();
        columnNames.add("buy");
        columnNames.add("sell");
        deep.setColumnNames(columnNames);

        float[][] inputValues = new float[candleList.size()][deep.getNumInputs()];
        float[][] outputValues = new float[candleList.size()][deep.getNumOutputs()];

        //Set input, output data
        int i = 0;
        for (CandleDTO candleDto : candleList) {
            deep.incSourceCount();

            float[] in = candleDto.tovalueArray();
            float[] out = new float[deep.getNumOutputs()];
            LearnDTO learnDto = learnEjb.get(deep.getLearnName(), candleDto.getStartDate());

            if (learnDto == null) {
                //Do nothing
                out[0] = 0f;
                out[1] = 0f;
            } else if (learnDto.getTrade().equals("buy")) {
                //Buy
                out[0] = 1f;
                out[1] = 0f;
                deep.incSourceBuy();
            } else if (learnDto.getTrade().equals("sell")) {
                //Sell
                out[0] = 0f;
                out[1] = 1f;
                deep.incSourceSell();
            } else {
                out[0] = 0f;
                out[1] = 0f;
            }

            System.arraycopy(in, 0, inputValues[i], 0, in.length);
            System.arraycopy(out, 0, outputValues[i], 0, out.length);
            i++;
        }

        //Store values
        deep.setInputValues(inputValues);
        deep.setOutputValues(outputValues);
    }
}
