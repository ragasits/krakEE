package krakee.weka;

import com.mongodb.client.model.Sorts;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import krakee.ConfigEJB;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Stateless
public class ModelEJB {
    private static final String MODELNAME = "modelName";  
    
    @EJB
    private ConfigEJB configEjb;

    /**
     * Get Models
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
     * @return 
     */
    public List<String> getNames() {
        return configEjb.getModelColl()
                .distinct(MODELNAME, String.class)
                .into(new ArrayList<>());
    }

    /**
     * Add Model to mongo
     * @param dto 
     */
    public void add(ModelDTO dto) {
        configEjb.getModelColl().insertOne(dto);
    }

    /**
     * Update Model from mongo
     * @param dto 
     */
    public void update(ModelDTO dto) {
        configEjb.getModelColl().replaceOne(
                eq("_id", dto.getId()), dto);
    }

    /**
     * Delete model from mongo
     * @param dto 
     */
    public void delete(ModelDTO dto) {
        //Delete item
        configEjb.getModelColl().deleteOne(eq("_id", dto.getId()));
    }
}
