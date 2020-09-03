package krakee.web;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
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
    private DeepDTO detail;

    public void onLearn() {
        detail = new DeepDTO();
        detail.setLearnName(selectedName);
        this.detail = dlEjb.learndDl(detail);
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

}
