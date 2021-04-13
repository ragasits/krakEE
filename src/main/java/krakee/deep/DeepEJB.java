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
import deepnetts.data.norm.DecimalScaleNormalizer;
import deepnetts.data.norm.MaxNormalizer;
import deepnetts.data.norm.MinMaxNormalizer;
import deepnetts.data.norm.Standardizer;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.FeedForwardNetwork.Builder;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.opt.OptimizerType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.data.Normalizer;
import javax.visrec.ml.eval.EvaluationMetrics;
import krakee.ConfigEJB;
import krakee.MyException;
import krakee.deep.input.AllCandleInputEJB;
import krakee.deep.input.TimeSeriesInputEJB;
import krakee.deep.input.TimeSeriesNormalizer;

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
    @EJB
    private AllCandleInputEJB allCandleInputEjb;
    @EJB
    private TimeSeriesInputEJB timeSeriesInputEjb;

    /**
     * Choose and execute normalization
     *
     * @param dto
     * @param dataSet
     * @return
     */
    public TabularDataSet normalize(DeepDTO dto, TabularDataSet dataSet) {
        Normalizer normalizer;

        switch (NormalizerType.valueOf(dto.getNormalizerType())) {
            case DecimalScaleNormalizer:
                normalizer = new DecimalScaleNormalizer(dataSet);
                break;
            case MaxNormalizer:
                normalizer = new MaxNormalizer(dataSet);
                break;
            case MinMaxNormalizer:
                normalizer = new MinMaxNormalizer(dataSet);
                break;
            case Standardizer:
                normalizer = new Standardizer(dataSet);
                break;
            case TimeSeries:
                normalizer = new TimeSeriesNormalizer();
                break;
            default:
                return null;
        }
        normalizer.normalize(dataSet);

        return dataSet;

    }

    /**
     * Generate dataset from the selected input
     *
     * @param dto
     * @return
     */
    public TabularDataSet fillDataset(DeepDTO dto) {

        switch (InputType.valueOf(dto.getInputType())) {
            case AllCandle:
                return allCandleInputEjb.fillDataset(dto);
            case TimeSeries:
                return timeSeriesInputEjb.fillDataset(dto);
            default:
                return null;
        }
    }

    /**
     * Learn and test Neural network
     *
     * @param dto
     * @throws krakee.MyException
     */
    public DeepDTO learnDeep(DeepDTO dto) throws MyException {
        //Get dataset
        TabularDataSet dataSet = this.fillDataset(dto);

        //Normalize data
        dataSet = this.normalize(dto, dataSet);

        DataSet[] trainTestSet = dataSet.split(0.6, 0.4);

        //Create statistics
        dto = this.calcSourceCount(dto, dataSet);
        dto = this.calcTrainCount(dto, (TabularDataSet) trainTestSet[0]);
        dto = this.calcTestCount(dto, (TabularDataSet) trainTestSet[1]);

        // create instance of multi addLayer percetpron using builder
        Builder builder = FeedForwardNetwork.builder()
                .addInputLayer(dto.getNumInputs());

        ArrayList<DeepLayerDTO> layerList = dto.getDeepLayer();
        layerList.sort(DeepLayerDTO.getCompByOrder());
       
        for (DeepLayerDTO layer : layerList) {
            builder = builder.addFullyConnectedLayer(layer.getWidths(), ActivationType.valueOf(layer.getActivationType()));
        }

        builder = builder
                .addOutputLayer(dto.getNumOutputs(), ActivationType.SOFTMAX)
                .lossFunction(LossType.valueOf(dto.getLossType()))
                .randomSeed(dto.getRandomSeed());

        FeedForwardNetwork neuralNet = builder.build();

        // create and configure instanceof backpropagation trainer
        neuralNet.getTrainer()
                .setMaxError(dto.getTrainerMaxError())
                .setLearningRate(dto.getTrainerLearningRate())
                .setMomentum(dto.getTrainerMomentum())
                .setOptimizer(OptimizerType.valueOf(dto.getOptimizerType()))
                .setMaxEpochs(dto.getTrainerMaxEpochs());

        neuralNet.train(trainTestSet[0]);

        // evaluate/test classifier
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        EvaluationMetrics em = evaluator.evaluate(neuralNet, trainTestSet[1]);
        dto.calcEvaluationMetrics(em);

        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        dto.calcConfusionMatrix(cm);
        
        return dto;
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
     * Count Source data buy/sell
     *
     * @param dto
     * @param dataSet
     * @return
     */
    private DeepDTO calcSourceCount(DeepDTO dto, TabularDataSet dataSet) {
        List<TabularDataSet.Item> itemList = dataSet.getItems();
        for (TabularDataSet.Item item : itemList) {
            dto.incSourceCount();

            if (item.getTargetOutput().get(BUYPOS) > 0f) {
                dto.incSourceBuy();
            } else if (item.getTargetOutput().get(SELLPOS) > 0f) {
                dto.incSourceSell();
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
