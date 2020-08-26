package krakee.web;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import krakee.dl.DlEJB;

/**
 * JSF bean for Deep Learning
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "dlBean")
public class DlBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private DlEJB dlEjb;

    private String selectedName;
    
    public void onLearn(){
        dlEjb.learndDl(selectedName);
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    
}
