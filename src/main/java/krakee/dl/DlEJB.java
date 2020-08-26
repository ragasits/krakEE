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
package krakee.dl;

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

/**
 * Manage neural network
 *
 * @author rgt
 */
@Stateless
public class DlEJB {

    static final Logger LOGGER = Logger.getLogger(TradeEJB.class.getCanonicalName());

    @EJB
    LearnEJB learnEjb;
    @EJB
    CandleEJB candleEjb;

    int numInputs;
    int numOutputs = 3;

    /**
     * Learn and test Neural network
     *
     * @param learnName
     */
    public void learndDl(String learnName) {
        //Get dataset
        TabularDataSet dataSet = this.getDlDataSet(learnName);

        //Normalize data
        MaxNormalizer norm = new MaxNormalizer(dataSet);
        norm.normalize(dataSet);

        DataSet[] trainTestSet = dataSet.split(0.6, 0.4);

        // create instance of multi addLayer percetpron using builder
        FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                .addInputLayer(numInputs)
                .addFullyConnectedLayer(100, ActivationType.TANH)
                .addFullyConnectedLayer(100, ActivationType.TANH)
                .addFullyConnectedLayer(100, ActivationType.TANH)
                .addOutputLayer(numOutputs, ActivationType.SOFTMAX)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(456)
                .build();

        // create and configure instanceof backpropagation trainer
        BackpropagationTrainer trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.04f);
        trainer.setLearningRate(0.01f);
        trainer.setMomentum(0.9f);
        trainer.setOptimizer(OptimizerType.MOMENTUM);

        neuralNet.train(trainTestSet[0]);

        // evaluate/test classifier
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        EvaluationMetrics em = evaluator.evaluate(neuralNet, trainTestSet[1]);
        System.out.println("CLASSIFIER EVALUATION METRICS");
        System.out.println(em);
        System.out.println("CONFUSION MATRIX");
        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        System.out.println(cm);

    }

    /**
     * Collect Deep learning dataSet
     *
     * @param learnName
     * @return
     */
    private TabularDataSet getDlDataSet(String learnName) {
        //Get Learning data
        LearnDTO firstLearn = learnEjb.getFirst(learnName);
        LearnDTO lastLearn = learnEjb.getLast(learnName);

        //Get Candles
        List<CandleDTO> candleList = candleEjb.get(firstLearn.getStartDate(), lastLearn.getStartDate());
        CandleDTO c = candleEjb.get(firstLearn.getStartDate());
        this.numInputs = c.toValueList().size();

        TabularDataSet dataSet = new TabularDataSet(this.numInputs, this.numOutputs);

        //Add column names
        List<String> columnNames = c.toColumnNameList();
        columnNames.add("out_nothing");
        columnNames.add("out_buy");
        columnNames.add("out_sell");
        dataSet.setColumnNames(columnNames.toArray(new String[0]));

        //Set inpuit, output data
        for (CandleDTO candleDto : candleList) {
            ArrayList<Float> i = candleDto.toValueList();
            ArrayList<Float> o = new ArrayList<>();
            LearnDTO learnDto = learnEjb.get(learnName, candleDto.getStartDate());

            if (learnDto == null) {
                //Do nothing
                o.add(1f);
                o.add(0f);
                o.add(0f);
            } else if (learnDto.getTrade().equals("buy")) {
                //Buy
                o.add(0f);
                o.add(1f);
                o.add(0f);
            } else if (learnDto.getTrade().equals("sell")) {
                //Sell
                o.add(0f);
                o.add(0f);
                o.add(1f);
            } else {
                o.add(1f);
                o.add(0f);
                o.add(0f);
            }

            //Make dataset
            dataSet.add(new TabularDataSet.Item(Common.convert(i), Common.convert(o)));
        }
        return dataSet;
    }
}
