package krakee.model;

import com.mongodb.client.model.Sorts;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import krakee.ConfigEJB;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import java.util.Date;
import krakee.MyException;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.export.ExportOneCandleEJB;
import krakee.export.ExportType;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

@Stateless
public class ModelEJB {

    private static final String MODELNAME = "modelName";

    @EJB
    private ConfigEJB configEjb;
    @EJB
    private ExportOneCandleEJB exportEjb;
    @EJB
    private LearnEJB learnEjb;
    @EJB
    private CandleEJB candleEJb;

    /**
     * Get Models
     *
     * @return
     */
    public List<ModelDTO> get() {
        return configEjb.getModelColl()
                .find()
                .sort(Sorts.ascending(MODELNAME))
                .into(new ArrayList<>());
    }

    /**
     * Get one model
     *
     * @param modelName
     * @return
     */
    public ModelDTO get(String modelName) {
        return configEjb.getModelColl()
                .find(eq(MODELNAME, modelName))
                .first();
    }

    /**
     * Get Model names
     *
     * @return
     */
    public List<String> getNames() {
        return configEjb.getModelColl()
                .distinct(MODELNAME, String.class)
                .into(new ArrayList<>());
    }

    /**
     * Add Model to mongo
     *
     * @param dto
     */
    public void add(ModelDTO dto) {
        configEjb.getModelColl().insertOne(dto);
    }

    /**
     * Update Model from mongo
     *
     * @param dto
     */
    public void update(ModelDTO dto) {
        configEjb.getModelColl().replaceOne(
                eq("_id", dto.getId()), dto);
    }

    /**
     * Delete model from mongo
     *
     * @param dto
     */
    public void delete(ModelDTO dto) {
        //Delete item
        configEjb.getModelColl().deleteOne(eq("_id", dto.getId()));
    }

    /**
     * Execute WEKA prediction
     *
     * @param model
     * @throws MyException
     */
    public void runWeka(ModelDTO model) throws MyException {
        //Delete old learn
        learnEjb.delete(model.getModelName());

        //Create instance
        Date buyDate = model.getFirstBuyDate();
        Date sellDate = model.getLastSellDate();
        Instances dataset = exportEjb.toInstances(ExportType.valueOf(model.getExportType()), buyDate, sellDate);

        //Save new learn - we save only the trades (buy, sell)
        List<CandleDTO> candleList = candleEJb.get(buyDate, sellDate);

        //Run remove
        if (!model.getRemoveAttributeIndices().isEmpty()) {
            Remove remove = new Remove();
            remove.setAttributeIndices(model.getRemoveAttributeIndices());
            remove.setInvertSelection(model.getRemoveInvertSelection());
            try {
                remove.setInputFormat(dataset);
                dataset = Filter.useFilter(dataset, remove);
            } catch (Exception ex) {
                throw new MyException("Weka error", ex);
            }

        }
        dataset.setClassIndex(dataset.numAttributes() - 1);

        try {
            //Run model
            Classifier classifier = (Classifier) SerializationHelper.read(model.getModelFileStream());
            for (int i = 0; i < dataset.numInstances(); i++) {
                Instance instance = dataset.instance(i);
                double prediction = classifier.classifyInstance(instance);
                instance.setClassValue(prediction);

                String trade = instance.stringValue(instance.classIndex());

                if (!trade.equals("none")) {
                    //Add new learn
                    CandleDTO candle = candleList.get(i);
                    LearnDTO dto = new LearnDTO();
                    dto.setName(model.getModelName());
                    dto.setStartDate(candle.getStartDate());
                    dto.setTrade(trade);
                    dto.setClose(candle.getClose());
                    dto.setChkMessage(instance.toString());
                    learnEjb.add(dto);
                }
            }

        } catch (Exception ex) {
            throw new MyException("Weka error", ex);
        }
    }
}
