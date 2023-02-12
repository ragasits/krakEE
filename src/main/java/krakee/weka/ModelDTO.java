package krakee.weka;

import org.bson.types.Binary;
import org.bson.types.ObjectId;

public class ModelDTO {

    private ObjectId id;

    private String modelName;
    private Binary modelFile;

    private String modelFileName;

    public ModelDTO() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Binary getModelFile() {
        return modelFile;
    }

    public void setModelFile(Binary modelFile) {
        this.modelFile = modelFile;
    }

    public String getModelFileName() {
        return modelFileName;
    }

    public void setModelFileName(String modelFileName) {
        this.modelFileName = modelFileName;
    }
}
