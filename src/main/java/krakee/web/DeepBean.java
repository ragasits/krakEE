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

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import krakee.MyException;
import krakee.deep.DeepDTO;
import krakee.deep.DeepEJB;
import krakee.deep.DeepInputEJB;
import org.primefaces.event.SelectEvent;

/**
 * JSF bean for Deep
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "deepBean")
public class DeepBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String selectedLearnName;
    private String selectedDeepName;
    private DeepDTO detail = new DeepDTO();
    private boolean disableBtn = true;

    @EJB
    private DeepEJB deepEjb;
    @EJB
    private DeepInputEJB deepInputEjb;
    @Inject
    private DeepInputBean inputBean;

    /**
     * Show input data
     *
     * @return
     */
    public String onInput() {
        if (this.detail != null) {
            inputBean.fillInputList(detail);
            inputBean.setDetail(detail);
            return "deepInput.xhtml?faces-redirect=true";
        }
        return null;

    }

    /**
     * Fill data set
     */
    public void onDataset() {
        try {
            detail.setLearnName(this.selectedLearnName);
            detail.setDeepName(this.selectedDeepName);
            deepInputEjb.fillDataset(this.detail);
            this.disableBtn = false;
        } catch (MyException ex) {
            addMsg(ex.getMessage());
        }
    }

    /**
     * Save (Insert / update) Deep item
     *
     */
    public void onSave() {
        DeepDTO dto = deepEjb.get(this.selectedDeepName);

        if (dto == null) {
            deepEjb.add(this.detail);
        } else {
            deepEjb.update(this.detail);
        }
    }

    /**
     * Delete Deep item
     */
    public void onDelete() {
        DeepDTO dto = deepEjb.get(this.selectedDeepName);

        if (dto != null) {
            deepEjb.delete(this.detail);
            this.detail = new DeepDTO();
            
        }
    }

    /**
     * Starting learning, testing process
     */
    public void onLearn() {
        try {
            deepEjb.learnDeep(detail);
        } catch (MyException ex) {
            this.addMsg(ex.getMessage());
        }
    }

    /**
     * Listener for p:autoComplete event
     *
     * @param event
     */
    public void onSelectedDeep(SelectEvent<String> event) {
        this.detail = deepEjb.get(event.getObject());

        if (this.detail != null) {
            this.selectedLearnName = this.detail.getLearnName();
        }

    }

    /**
     * Show message
     *
     * @param msg
     */
    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public List<String> complete(String query) {
        return deepEjb.getDeepNames();
    }

    public String getSelectedLearnName() {
        return selectedLearnName;
    }

    public void setSelectedLearnName(String selectedLearnName) {
        this.selectedLearnName = selectedLearnName;
    }

    public DeepDTO getDetail() {
        return detail;
    }

    public void setDetail(DeepDTO detail) {
        this.detail = detail;
    }

    public String getSelectedDeepName() {
        return selectedDeepName;
    }

    public void setSelectedDeepName(String selectedDeepName) {
        this.selectedDeepName = selectedDeepName;
    }

    public boolean isDisableBtn() {
        return disableBtn;
    }

    public void setDisableBtn(boolean disableBtn) {
        this.disableBtn = disableBtn;
    }
}
