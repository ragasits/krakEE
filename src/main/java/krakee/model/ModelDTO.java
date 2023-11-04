package krakee.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

public class ModelDTO {

    private ObjectId id;

    private String modelName;
    private Binary modelFile;

    private String modelFileName;

    private String exportType;
    private Long buyTime;
    private Long sellTime;

    private String removeAttributeIndices;
    private Boolean removeInvertSelection;

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

    @BsonIgnore
    public InputStream getModelFileStream() {
        return new ByteArrayInputStream(this.getModelFile().getData());
    }

    public String getModelFileName() {
        return modelFileName;
    }

    public void setModelFileName(String modelFileName) {
        this.modelFileName = modelFileName;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public Long getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Long buyTime) {
        this.buyTime = buyTime;
    }

    public Long getSellTime() {
        return sellTime;
    }

    public void setSellTime(Long sellTime) {
        this.sellTime = sellTime;
    }

    public String getRemoveAttributeIndices() {
        return removeAttributeIndices;
    }

    public void setRemoveAttributeIndices(String removeAttributeIndices) {
        this.removeAttributeIndices = removeAttributeIndices;
    }

    public Boolean getRemoveInvertSelection() {
        return removeInvertSelection;
    }

    public void setRemoveInvertSelection(Boolean removeInvertSelection) {
        this.removeInvertSelection = removeInvertSelection;
    }

}
