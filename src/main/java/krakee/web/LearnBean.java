/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.web;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.learn.ExportEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * JSF bean for one Candle
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "learnBean")
public class LearnBean implements Serializable {

    private static final String FILENAME = "candleLearn.csv";
    private static final String LEARNNAME = "Első";
    
    private static final long serialVersionUID = 1L;
    private StreamedContent file;
    private long selectedBuyTime;
    private long  selectedSellTime;

    @EJB
    private LearnEJB learnEjb;
    @EJB
    private ExportEJB exportEjb;
    @EJB
    private CandleEJB candleEjb;

    @Inject
    private CandleDetailBean candleBean;

    /**
     * Show messages
     *
     * @param msg
     */
    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
    
    /**
     * Default values
     */
    @PostConstruct
    public void init(){
        this.selectedBuyTime = learnEjb.getFirst(LEARNNAME).getStartDate().getTime();
        this.selectedSellTime = learnEjb.getLast(LEARNNAME).getStartDate().getTime();
    }

    /**
     * Get all Learn
     *
     * @return
     */
    public List<LearnDTO> getLearnList() {
        return learnEjb.get();
    }

    /**
     * Get Names (Distinct)
     *
     * @return
     */
    public List<String> getLearnNameList() {
        return learnEjb.getNames();
    }

    /**
     * Link to candleDetail
     *
     * @param learn
     * @return
     */
    public String showDetail(LearnDTO learn) {

        if (learn != null) {
            candleBean.setSelectedDate(learn.getStartDate());

            CandleDTO dto = candleEjb.get(learn.getStartDate());
            candleBean.setSelectedIdHexa(dto.getIdHexa());

            return "candleDetail?faces-redirect=true";
        }
        return null;
    }

    //Check1
    public void chkLearnPeaks() {
        learnEjb.chkLearnPeaks();
    }

    //Check2
    public void chkLearnPairs() {
        learnEjb.chkLearnPairs();
    }

    /**
     * Create, download CSV file
     */
    public void onCSV() {
        Date buyDate = new Date(selectedBuyTime);
        Date sellDate = new Date(selectedSellTime);
        
        ArrayList<String> csvList = (ArrayList<String>) exportEjb.candleToCSV(LEARNNAME,
                buyDate, sellDate);

        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String realPath = ctx.getRealPath("/WEB-INF/").concat("/" + FILENAME);

        //Save to file
        try {
            OutputStream fout = new FileOutputStream(realPath);
            OutputStream bout = new BufferedOutputStream(fout);
            try ( OutputStreamWriter out = new OutputStreamWriter(bout, "ISO-8859-2")) {
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

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public List<LearnDTO> getBuyList() {
        return learnEjb.getBuy();
    }

    public List<LearnDTO> getSellList() {
        return learnEjb.getSell();
    }

    public long getSelectedBuyTime() {
        return selectedBuyTime;
    }

    public void setSelectedBuyTime(long selectedBuyTime) {
        this.selectedBuyTime = selectedBuyTime;
    }

    public long getSelectedSellTime() {
        return selectedSellTime;
    }

    public void setSelectedSellTime(long selectedSellTime) {
        this.selectedSellTime = selectedSellTime;
    }
}
