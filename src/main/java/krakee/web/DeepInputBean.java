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
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import krakee.MyException;
import krakee.deep.DeepInputEJB;
import krakee.deep.DeepRowDTO;
import krakee.deep.DeepRowEJB;
import krakee.deep.InputType;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "deepInputBean")
public class DeepInputBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private DeepInputEJB deepInputEjb;
    @EJB
    private DeepRowEJB deepRowEjb;

    private String selectedLearnName;
    private String selectedInputType;

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public void onDataset() {
        try {
            deepInputEjb.fillDeepInput(selectedLearnName);
        } catch (MyException ex) {
            addMsg(ex.getMessage());
        }
    }

    public void onRow() {
        try {
            deepRowEjb.fillRow(selectedLearnName, selectedInputType);
        } catch (MyException ex) {
            addMsg(ex.getMessage());
        }
    }

    /**
     * Get rows from DeepRow
     *
     * @return
     */
    public ArrayList<DeepRowDTO> getDeepRows() {
        return deepRowEjb.get(this.selectedLearnName, this.selectedInputType);

    }

    /**
     * Get inputType names
     * @return 
     */
    public InputType[] getInputTypes() {
        return InputType.values();
    }
    
    /**
     * Get column names
     * @return 
     */
    public ArrayList<String> getColumnNames(){
        try {
            return deepRowEjb.getColumnNames(selectedInputType);
        } catch (MyException ex) {
            this.addMsg("getColumnList error: "+ex.getMessage());
            return null;
        }
    }
    
    /**
     * Get value from InputRow
     *
     * @param rowIdx
     * @param colIdx
     * @return
     */
    public Float getRowValue(Integer rowIdx, Integer colIdx, DeepRowDTO row) {
        if (row.getInputRow().size() > colIdx){
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

    public DeepInputEJB getDeepInputEjb() {
        return deepInputEjb;
    }

    public void setDeepInputEjb(DeepInputEJB deepInputEjb) {
        this.deepInputEjb = deepInputEjb;
    }

    public String getSelectedInputType() {
        return selectedInputType;
    }

    public void setSelectedInputType(String selectedInputType) {
        this.selectedInputType = selectedInputType;
    }
}
