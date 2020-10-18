package krakee.web;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import krakee.MyException;
import krakee.deep.DeepDTO;
import krakee.deep.DeepEJB;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * JSF bean for Deep Learning
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "deepBean")
public class DeepBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private StreamedContent file;

    static final String FILENAME = "dataset.csv";

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
            this.disableBtn = false;
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

    /**
     * Download values as CSV file 
     */
    public void onCSV() {
        ArrayList<String> csvList = this.detail.inOutValuesToCsv();

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

    public StreamedContent getFile() {
        return file;
    }
    
    
}
