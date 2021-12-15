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
import krakee.input.InputStatCountDTO;
import krakee.input.InputStatDTO;
import krakee.input.InputStatEJB;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "inputStatBean")
public class InputStatBean implements Serializable {

    @EJB
    private InputStatEJB deepStatEjb;

    private static final long serialVersionUID = 1L;
    private String learnName;
    private String inputType;

    public void onFillColumns() {
        deepStatEjb.fillColumns(this.learnName, this.inputType);
    }

    public void onAnalyzeColumns() {
        deepStatEjb.analyzeColumns(this.learnName, this.inputType);
    }

    public void oneleteColumn(Integer columnId) {
        deepStatEjb.deleteColumn(this.learnName, this.inputType, columnId);
    }

    public ArrayList<InputStatDTO> getColumnList() {
        return deepStatEjb.get(this.learnName, this.inputType);
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
