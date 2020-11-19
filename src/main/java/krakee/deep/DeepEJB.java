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

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Sorts;
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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;
import krakee.ConfigEJB;
import krakee.MyException;

/**
 * Manage Deep Learning
 *
 * @author rgt
 */
@Stateless
public class DeepEJB {

    static final int BUYPOS = 0;
    static final int SELLPOS = 1;

    @EJB
    private ConfigEJB configEjb;
    @EJB
    private DeepInputEJB inputEjb;

    /**
     * Learn and test Neural network
     *
     * @param dto
     * @throws krakee.MyException
     */
    public void learnDeep(DeepDTO dto) throws MyException {
        //Get dataset
        TabularDataSet dataSet = inputEjb.fillTabularDataset(dto);

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
        dto.calcEvaluationMetrics(em);

        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        dto.calcConfusionMatrix(cm);
    }

    /**
     * Count Train data buy/sell
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
     * Count Test data buy/sell
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
     * Get list of the Deep names (ID)
     *
     * @return
     */
    public List<String> getDeepNames() {
        return configEjb.getDeepColl()
                .distinct("deepName", String.class)
                .into(new ArrayList<>());
    }

    /**
     * Get Deep collection filter by learnName
     *
     * @return
     */
    public List<DeepDTO> get() {
        return configEjb.getDeepColl()
                .find()
                .sort(Sorts.ascending("learnName"))
                .into(new ArrayList<>());
    }

    /**
     * Get Deep collection filter by deepName
     *
     * @param deepName
     * @return
     */
    public DeepDTO get(String deepName) {
        return configEjb.getDeepColl()
                .find(eq("deepName", deepName))
                .first();
    }

    /**
     * Add Deep item
     *
     * @param dto
     */
    public void add(DeepDTO dto) {
        configEjb.getDeepColl().insertOne(dto);
    }

    /**
     * Update Deep item
     *
     * @param dto
     */
    public void update(DeepDTO dto) {
        configEjb.getDeepColl().replaceOne(
                eq("_id", dto.getId()), dto);
    }

    /**
     * Delete Deep item
     *
     * @param dto
     */
    public void delete(DeepDTO dto) {
        //Delete inputs
        inputEjb.delete(dto);
        //Delete item
        configEjb.getDeepColl().deleteOne(eq("_id", dto.getId()));
    }

}
