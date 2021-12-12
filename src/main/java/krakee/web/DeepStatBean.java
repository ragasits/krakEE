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
import krakee.deep.DeepStatCountDTO;
import krakee.deep.DeepStatDTO;
import krakee.deep.DeepStatEJB;

/**
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "deepStatBean")
public class DeepStatBean implements Serializable {

    @EJB
    private DeepStatEJB deepStatEjb;

    private static final long serialVersionUID = 1L;
    private String learnName;
    private String inputType;

    public void onRowToggle(Integer columnId) {
        return;
    }

    public void onFillColumns() {
        deepStatEjb.fillColumns(this.learnName, this.inputType);
    }

    public void onAnalyzeColumns() {
        deepStatEjb.analyzeColumns(this.learnName, this.inputType);
    }

    public ArrayList<DeepStatDTO> getColumnList() {
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
    public ArrayList<DeepStatCountDTO> getUniqueList(Integer columnId) {
        DeepStatDTO dto = deepStatEjb.get(this.learnName, this.inputType, columnId);
        return dto.getValueCounts();
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
