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
package krakee.web;

import deepnetts.data.TabularDataSet;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import krakee.deep.DeepEJB;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Show dataset
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "deepDatasetBean")
public class DeepDatasetBean implements Serializable {

    static final String FILENAME = "dataset.csv";
    private static final long serialVersionUID = 1L;
    private TabularDataSet dataset;
    private StreamedContent file;

    @EJB
    DeepEJB deepEjb;

    /**
     * Download dataset in CSV file format
     */
    public void onCSV() {
        ArrayList<String> csvList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        //Header
        String[] columns = this.dataset.getColumnNames();
        for (String column : columns) {
            if (sb.length() != 0) {
                sb.append(";");
            }
            sb.append(column);
        }
        csvList.add(sb.toString());

        //Rows
        for (int i = 0; i < dataset.size(); i++) {
            sb = new StringBuilder();
            TabularDataSet.Item items = (TabularDataSet.Item) dataset.get(i);
            //Input
            for (int j = 0; j < dataset.getNumInputs(); j++) {
                if (sb.length() != 0) {
                    sb.append(";");
                }
                sb.append(items.getInput().get(j));
            }

            //Output
            for (int j = 0; j < dataset.getNumOutputs(); j++) {
                if (sb.length() != 0) {
                    sb.append(";");
                }
                sb.append(items.getTargetOutput().get(j));
            }

            csvList.add(sb.toString());
        }

        //Create file
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String realPath = ctx.getRealPath("/WEB-INF/").concat("/" + FILENAME);

        //Save to file
        try {
            OutputStream fout = new FileOutputStream(realPath);
            OutputStream bout = new BufferedOutputStream(fout);
            try (OutputStreamWriter out = new OutputStreamWriter(bout, "ISO-8859-2")) {
                for (String s : csvList) {
                    out.write(s + "\n");
                }
            }
        } catch (IOException ex) {
            this.addMsg("Error: " + ex.getMessage());
            return;
        }

        this.file = DefaultStreamedContent.builder()
                .name(FILENAME)
                .contentType("application/csv")
                .stream(() -> FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/" + FILENAME))
                .build();
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    /**
     * Get rows from dataset
     *
     * @return
     */
    public List getDatasetRows() {
        if (dataset != null) {
            return dataset.getItems();
        }
        return null;
    }

    /**
     * Get value from dataset
     *
     * @param rowIdx
     * @param colIdx
     * @return
     */
    public Float getDatasetValue(Integer rowIdx, Integer colIdx) {
        if (dataset.getNumInputs() > colIdx) {
            return ((TabularDataSet.Item) dataset.get(rowIdx)).getInput().get(colIdx);
        } else {
            return ((TabularDataSet.Item) dataset.get(rowIdx)).getTargetOutput().get(colIdx - dataset.getNumInputs());
        }
    }

    /**
     * Get column names
     *
     * @return
     */
    public String[] getColumnNames() {
        if (dataset != null) {
            return dataset.getColumnNames();
        }
        return null;
    }

    public void setDataset(TabularDataSet dataset) {
        this.dataset = dataset;
    }

    public StreamedContent getFile() {
        return file;
    }

}
