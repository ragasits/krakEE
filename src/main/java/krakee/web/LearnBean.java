/*
 * Copyright (C) 2021 Ragasits Csaba
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
import krakee.learn.ExportType;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * JSF bean for one Candle
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "learnBean")
public class LearnBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private StreamedContent file;
    private long selectedBuyTime;
    private long  selectedSellTime;
    private ExportType selectedExportType;
    private String selectedLearn;

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
     */
    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    public void updateLists(){
        this.selectedBuyTime = learnEjb.getFirst(this.selectedLearn).getStartDate().getTime();
        this.selectedSellTime = learnEjb.getLast(this.selectedLearn).getStartDate().getTime();
    }
    
    public ExportType[] getExportTypes(){
        return ExportType.values();
    }

    /**
     * Get all Learn
     *
     */
    public List<LearnDTO> getLearnList() {
        if (this.selectedLearn!=null){
            return learnEjb.get(this.selectedLearn);
        }
        return Collections.emptyList();
            }

    /**
     * Get Names (Distinct)
     *
     */
    public List<String> getLearnNameList() {
        return learnEjb.getNames();
    }

    public String getSelectedLearn() {
        return selectedLearn;
    }

    public void setSelectedLearn(String selectedLearn) {
        this.selectedLearn = selectedLearn;
    }

    /**
     * Link to candleDetail
     *
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
        String filename = this.getSelectedExportType().toString()+".csv";
        
        ArrayList<String> csvList = (ArrayList<String>) exportEjb.candleToCSV(this.selectedLearn,
                buyDate, sellDate, this.getSelectedExportType());

        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String realPath = ctx.getRealPath("/WEB-INF/").concat("/").concat(filename);

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
                .name(filename)
                .contentType("application/csv")
                .stream(() -> FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/" + filename))
                .build();

    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public List<LearnDTO> getBuyList() {
        if (this.selectedLearn!=null){
            return learnEjb.getBuy(this.selectedLearn);
        }
        return Collections.emptyList();
    }

    public List<LearnDTO> getSellList() {
        if (this.selectedLearn!=null){
            return learnEjb.getSell(this.selectedLearn);
        }
        return Collections.emptyList();
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

    public ExportType getSelectedExportType() {
        return selectedExportType;
    }

    public void setSelectedExportType(ExportType selectedExportType) {
        this.selectedExportType = selectedExportType;
    }
}
