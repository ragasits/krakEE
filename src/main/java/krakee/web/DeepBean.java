package krakee.web;

import java.io.Serializable;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import krakee.MyException;
import krakee.deep.DeepDTO;
import krakee.deep.DeepEJB;

/**
 * JSF bean for Deep Learning
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "deepBean")
public class DeepBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private DeepEJB dlEjb;

    private String selectedName;
    private DeepDTO detail = new DeepDTO();
    private boolean disableBtn = true;

    /**
     * Show input data
     *
     * @return
     */
    public String onInput() {
        try {
            dlEjb.createDlValues(detail);
            return "deepInput.xhtml?faces-redirect=true";
        } catch (MyException ex) {
            addMsg(ex.getMessage());
        }
        return null;
    }

    /**
     * Fill data set
     */
    public void onDataset() {
        try {
            detail = new DeepDTO();
            detail.setLearnName(selectedName);
            dlEjb.createDlValues(detail);
            this.disableBtn=false;
        } catch (MyException ex) {
            addMsg(ex.getMessage());
        }
    }

    /**
     * Starting learning, testing process
     */
    public void onLearn() {
        try {
            dlEjb.learndDl(detail);
        } catch (MyException ex) {
            this.addMsg(ex.getMessage());
        }
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public DeepDTO getDetail() {
        return detail;
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public boolean isDisableBtn() {
        return disableBtn;
    }

    public void setDisableBtn(boolean disableBtn) {
        this.disableBtn = disableBtn;
    }
    
    
}
