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
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import krakee.MyException;
import krakee.input.InputEJB;
import krakee.input.InputRowDTO;
import krakee.input.InputRowEJB;
import krakee.input.type.InputType;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "inputBean")
public class InputBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String FILENAME = "deepRow.csv";

    @EJB
    private InputEJB deepInputEjb;
    @EJB
    private InputRowEJB deepRowEjb;
    @Inject
    private InputStatBean deepStatBean;

    private ArrayList<InputRowDTO> rowList;
    private String selectedLearnName;
    private String selectedInputType;
    private StreamedContent file;

    /**
     * Show messages
     *
     * @param msg
     */
    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    /**
     * Fill DeepInput collection
     */
    public void onDataset() {
        try {
            deepInputEjb.fillDeepInput(selectedLearnName);
        } catch (MyException ex) {
            addMsg(ex.getMessage());
        }
    }

    /**
     * Fill DeepRow collection
     */
    public void onRow() {
        try {
            deepRowEjb.fillRow(selectedLearnName, selectedInputType);
        } catch (MyException ex) {
            addMsg(ex.getMessage());
        }
    }

    /**
     * Get rows filter by learnName and inputType
     */
    public void onGetRows() {
        this.rowList = deepRowEjb.get(this.selectedLearnName, this.selectedInputType);
    }

    /**
     * Navigate to stat page
     * @return 
     */
    public String onDeepStat() {
        return deepStatBean.onShow(this.selectedLearnName, this.selectedInputType);
    }

    /**
     * Download rows in CSV file format
     */
    public void onCSV() {

        ArrayList<String> csvList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        //Header
        InputRowDTO dto = this.rowList.get(0);
        ArrayList columns = dto.getColumnNames();
        for (Object column : columns) {
            if (sb.length() != 0) {
                sb.append(";");
            }
            sb.append(column);
        }
        csvList.add(sb.toString());

        //Rows
        ArrayList<InputRowDTO> rows = this.rowList;
        for (InputRowDTO row : rows) {
            sb = new StringBuilder();
            //Input
            for (Float input : row.getInputRow()) {
                if (sb.length() != 0) {
                    sb.append(";");
                }
                sb.append(input);
            }

            //Output
            for (Float output : row.getOutputRow()) {
                if (sb.length() != 0) {
                    sb.append(";");
                }
                sb.append(output);
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

    /**
     * Disable toCSV, stat button
     *
     * @return
     */
    public boolean isDisableButtons() {
        return this.rowList == null || this.rowList.isEmpty();
    }

    /**
     * Calculate input columns
     *
     * @return
     */
    public Integer getNumInputs() {
        if (this.rowList == null || this.rowList.isEmpty()) {
            return null;
        } else {
            return this.rowList.get(0).getInputRow().size();
        }
    }

    /**
     * Calculate output columns
     *
     * @return
     */
    public Integer getNumOutputs() {
        if (this.rowList == null || this.rowList.isEmpty()) {
            return null;
        } else {
            return this.rowList.get(0).getOutputRow().size();
        }
    }

    /**
     * Get rows from DeepRow
     *
     * @return
     */
    public ArrayList<InputRowDTO> getDeepRows() {
        return this.rowList;

    }

    /**
     * Get inputType names
     *
     * @return
     */
    public InputType[] getInputTypes() {
        return InputType.values();
    }

    /**
     * Get column names
     *
     * @return
     */
    public ArrayList<String> getColumnNames() {
        InputRowDTO dto = deepRowEjb.getFirst(this.selectedLearnName, this.selectedInputType);
        if (dto != null) {
            return dto.getColumnNames();
        }
        return null;
    }

    /**
     * Get value from InputRow
     *
     * @param rowIdx
     * @param colIdx
     * @return
     */
    public Float getRowValue(Integer rowIdx, Integer colIdx, InputRowDTO row) {
        if (row.getInputRow().size() > colIdx) {
            return row.getInputRow().get(colIdx);
        } else {
            return row.getOutputRow().get(colIdx - row.getInputRow().size());
        }
    }

    public String getSelectedLearnName() {
        return selectedLearnName;
    }

    public void setSelectedLearnName(String selectedLearnName) {
        this.selectedLearnName = selectedLearnName;
    }

    public InputEJB getDeepInputEjb() {
        return deepInputEjb;
    }

    public void setDeepInputEjb(InputEJB deepInputEjb) {
        this.deepInputEjb = deepInputEjb;
    }

    public String getSelectedInputType() {
        return selectedInputType;
    }

    public void setSelectedInputType(String selectedInputType) {
        this.selectedInputType = selectedInputType;
    }

    public StreamedContent getFile() {
        return file;
    }
}
