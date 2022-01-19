/*
 * Copyright (C) 2021 rgt
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
package krakee.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import krakee.input.InputRowDTO;
import krakee.input.InputStatCountDTO;
import krakee.input.InputStatDTO;
import krakee.input.InputStatEJB;
import krakee.input.InputStatHeadDTO;
import krakee.input.InputStatHeadEJB;
import krakee.input.ThresholdDTO;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "inputStatBean")
public class InputStatBean implements Serializable {

    @EJB
    private InputStatEJB deepStatEjb;
    @EJB
    private InputStatHeadEJB inputStatHeadEjb;

    private static final long serialVersionUID = 1L;
    private String learnName;
    private String inputType;
    private LineChartModel lineModel;

    /**
     * Show stat page
     *
     * @param learName
     * @param inputType
     * @return
     */
    public String onShow(String learName, String inputType) {
        this.learnName = learName;
        this.inputType = inputType;
        this.createLineModel();
        return "inputStat.xhtml?faces-redirect=true";
    }

    /**
     * Fill columns with default values
     */
    public void onFillColumns() {
        deepStatEjb.fillColumns(this.learnName, this.inputType);
    }

    /**
     * Analyze + store columns
     */
    public void onAnalyzeColumns() {
        deepStatEjb.analyzeColumns(this.learnName, this.inputType);
    }

    /**
     * Analyze head + create chart
     */
    public void onAnalyzeHead() {
        inputStatHeadEjb.analyzeHead(this.learnName, this.inputType);
        this.createLineModel();
    }

    /**
     * Delete selected column
     *
     * @param columnId
     */
    public void onDeleteColumn(Integer columnId) {
        deepStatEjb.deleteColumn(this.learnName, this.inputType, columnId);
    }

    /**
     * Create chart line model
     */
    public void createLineModel() {
        this.lineModel = new LineChartModel();

        //Options
        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Variance Threshold Versus Number of Selected Features");
        options.setTitle(title);
        lineModel.setOptions(options);

        InputStatHeadDTO head = this.inputStatHeadEjb.get(this.learnName, this.inputType);
        if (head == null) {
            return;
        }

        ArrayList<ThresholdDTO> thresholdList = head.getThresholds();
        if (thresholdList == null) {
            return;
        }

        List<Object> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (ThresholdDTO dto : thresholdList) {
            values.add(dto.getFeatures());
            labels.add(String.valueOf(dto.getThreshold()));
        }

        LineChartDataSet dataSet = new LineChartDataSet();

        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("Features");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setTension(0.1);

        ChartData data = new ChartData();
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        lineModel.setData(data);
    }

    public ArrayList<InputStatDTO> getColumnList() {
        return deepStatEjb.get(this.learnName, this.inputType);
    }

    /**
     * Get duplicates from head
     * @return 
     */
    public ArrayList<InputRowDTO> getDuplicates() {
        InputStatHeadDTO dto = inputStatHeadEjb.get(this.learnName, this.inputType);
        if (dto != null) {
            return dto.getInputRows();
        }
        return null;
    }

    public String getLearnName() {
        return learnName;
    }

    public void setLearnName(String learnName) {
        this.learnName = learnName;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }

    /**
     * Get list of the values
     *
     * @param columnId
     * @return
     */
    public ArrayList<InputStatCountDTO> getUniqueList(Integer columnId) {
        InputStatDTO dto = deepStatEjb.get(this.learnName, this.inputType, columnId);
        return dto.getValueCounts();
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
