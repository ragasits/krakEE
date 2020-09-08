package krakee.web;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
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

    static final String FILENAME = "dataset.csv";

    private static final long serialVersionUID = 1L;

    @EJB
    private DeepEJB dlEjb;

    private String selectedName;
    private DeepDTO detail = new DeepDTO();
    private StreamedContent file;

    /**
     * Export DataSet into CSV file
     */
    public void onCSV() {
        LinkedList<String> csvList;
        detail.setLearnName(selectedName);

        try {
            csvList = dlEjb.dataSetToCsv(detail);
        } catch (MyException ex) {
            this.addMsg(ex.getMessage());
            return;
        }

        //Create file
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String realPath = ctx.getRealPath("/WEB-INF/").concat("/"+FILENAME);

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
                .name("dataset.csv")
                .contentType("application/csv")
                .stream(() -> FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/dataset.csv"))
                .build();
    }

    /**
     * Starting learning, testing process
     */
    public void onLearn() {
        detail = new DeepDTO();
        detail.setLearnName(selectedName);
        try {
            this.detail = dlEjb.learndDl(detail);
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

    public StreamedContent getFile() {
        return file;
    }

}
