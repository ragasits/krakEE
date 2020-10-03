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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.get.TradeEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;
import krakee.Common;
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
     * Convert dataSet into csv line
     *
     * @param deep
     * @return
     * @throws krakee.MyException
     */
    public LinkedList<String> dataSetToCsv(DeepDTO deep) throws MyException {
        TabularDataSet dataSet = this.getDlDataSet(deep);
        StringBuilder sb = new StringBuilder();
        LinkedList<String> csvList = new LinkedList<>();

        //Get header
        String[] columnNames = dataSet.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            sb.append(columnNames[i]);
            if (i != columnNames.length - 1) {
                sb.append(";");
            }
        }
        csvList.add(sb.toString());

        //Get Lines
        float[] values;
        List<TabularDataSet.Item> itemList = dataSet.getItems();
        for (TabularDataSet.Item item : itemList) {
            sb = new StringBuilder();
            //Input values
            values = item.getInput().getValues();
            for (int i = 0; i < values.length; i++) {
                sb.append(values[i]).append(";");
            }

            //Output values
            values = item.getTargetOutput().getValues();
            for (int i = 0; i < values.length; i++) {
                sb.append(values[i]);
                if (i != values.length - 1) {
                    sb.append(";");
                }
            }

            csvList.add(sb.toString());
        }
        return csvList;
    }

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
     * @return
     * @throws krakee.MyException
     */
    public DeepDTO learndDl(DeepDTO dto) throws MyException {
        //Get dataset
        TabularDataSet dataSet = this.getDlDataSet(dto);

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
        
        return dto;
    }

    /**
     * Collect Deep learning dataSet
     *
     * @param learnName
     * @return
     */
    private TabularDataSet getDlDataSet(DeepDTO deep) throws MyException {
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

        TabularDataSet dataSet = new TabularDataSet(deep.getNumInputs(), deep.getNumOutputs());

        //Add column names
        List<String> columnNames = c.toColumnNameList();
        columnNames.add("buy");
        columnNames.add("sell");
        dataSet.setColumnNames(columnNames.toArray(new String[0]));

        //Set inpuit, output data
        for (CandleDTO candleDto : candleList) {
            deep.incSourceCount();

            ArrayList<Float> i = candleDto.toValueList();
            ArrayList<Float> o = new ArrayList<>();
            LearnDTO learnDto = learnEjb.get(deep.getLearnName(), candleDto.getStartDate());

            if (learnDto == null) {
                //Do nothing
                o.add(0f);
                o.add(0f);
            } else if (learnDto.getTrade().equals("buy")) {
                //Buy
                o.add(1f);
                o.add(0f);
                deep.incSourceBuy();
            } else if (learnDto.getTrade().equals("sell")) {
                //Sell
                o.add(0f);
                o.add(1f);
                deep.incSourceSell();
            } else {
                o.add(0f);
                o.add(0f);
            }

            //Make dataset
            dataSet.add(new TabularDataSet.Item(Common.convert(i), Common.convert(o)));
        }
        return dataSet;
    }
}
