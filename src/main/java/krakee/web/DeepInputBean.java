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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import krakee.deep.DeepDTO;
import krakee.deep.DeepInputDTO;
import krakee.deep.DeepInputEJB;
import krakee.deep.input.AllCandleEJB;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "deepInputBean")
public class DeepInputBean implements Serializable {

    private static final long serialVersionUID = 1L;
    static final String FILENAME = "dataset.csv";
    private ArrayList<DeepInputDTO> inputList;
    private StreamedContent file;
    private DeepDTO detail;

    @EJB
    private DeepInputEJB inputEjb;
    @EJB
    private AllCandleEJB allCandleEjb;

    /**
     * Download values as CSV file
     */
    public void onCSV() {
        ArrayList<String> csvList = inputEjb.inOutValuesToCsv(detail);

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
     * Get one value
     *
     * @param rowIdx
     * @param colIdx
     * @return
     */
    public Float getInputOutputValue(Integer rowIdx, Integer colIdx) {
        return this.allCandleEjb.getInputValue(inputList, rowIdx, colIdx);
    }

    /**
     * Create input list
     *
     * @param deep
     */
    public void fillInputList(DeepDTO deep) {
        this.inputList = inputEjb.get(deep.getDeepName());
    }

    public ArrayList<DeepInputDTO> getInputList() {
        return inputList;
    }

    public void setInputList(ArrayList<DeepInputDTO> inputList) {
        this.inputList = inputList;
    }

    public DeepDTO getDetail() {
        return detail;
    }

    public void setDetail(DeepDTO detail) {
        this.detail = detail;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

}