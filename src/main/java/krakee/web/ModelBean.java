package krakee.web;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import krakee.learn.ExportType;
import krakee.weka.ModelDTO;
import krakee.weka.ModelEJB;
import org.bson.types.Binary;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.file.UploadedFile;

import java.io.Serializable;
import java.util.List;

@SessionScoped
@Named(value = "modelBean")
public class ModelBean implements Serializable {

    private ModelDTO detail  = new ModelDTO();

    @EJB
    private ModelEJB modelEjb;

    public List<String> complete(String query) {
        return modelEjb.getNames();
    }

    public void onSelectedName(SelectEvent<String> event) {
        this.detail = modelEjb.get(event.getObject());
    }

    /**
     * Add / Update model
     */
    public void onSave() {
        ModelDTO dto = modelEjb.get(this.detail.getModelName());

        if (dto == null) {
            this.detail.setId(null);
            modelEjb.add(this.detail);
        } else {
            modelEjb.update(this.detail);
        }
    }

    /**
     * Delete model
     */
    public void onDelete() {
        ModelDTO dto = modelEjb.get(this.detail.getModelName());

        if (dto != null) {
            modelEjb.delete(this.detail);
            this.detail = new ModelDTO();

        }
    }

    /**
     * Upload model file
     * @param event 
     */
    public void onModelFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        org.bson.types.Binary bin = new Binary(file.getContent());
        detail.setModelFile(bin);
        detail.setModelFileName(file.getFileName());
    }

    public String getSelectedName() {
        if (this.detail != null) {
            return this.detail.getModelName();
        }
        return null;
    }

    public void setSelectedName(String selectedName) {
        if (this.detail != null) {
            this.detail.setModelName(selectedName);
        }
    }

    public ModelDTO getDetail() {
        return detail;
    }

    public ExportType getSelectedExportType() {
        if (detail!=null && this.detail.getExportType()!=null){
            return ExportType.valueOf(this.detail.getExportType());
        }
        return null;
    }

    public void setSelectedExportType(ExportType selectedExportType) {
        if (detail!=null){
            this.detail.setExportType(selectedExportType.toString());
        }
    }

    public Long getSelectedBuyTime() {
        if (detail!=null){
            return detail.getBuyTime();
        }
        return null;
    }

    public void setSelectedBuyTime(Long selectedBuyTime) {
        if (detail!=null){
            this.detail.setBuyTime(selectedBuyTime);
        }
    }

    public Long getSelectedSellTime() {
        if (this.detail!=null){
            return detail.getSellTime();
        }
        return null;
    }

    public void setSelectedSellTime(Long selectedSellTime) {
        if (this.detail!=null){
            this.detail.setSellTime(selectedSellTime);
        }
    }
}
