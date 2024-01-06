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
import jakarta.inject.Inject;
import jakarta.inject.Named;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import java.io.Serializable;
import java.util.Collections;
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
    private long selectedBuyTime;
    private long  selectedSellTime;
    private String selectedLearn;

    @EJB
    private LearnEJB learnEjb;
    @EJB
    private CandleEJB candleEjb;

    @Inject
    private CandleDetailBean candleBean;


    public void updateLists(){
        this.selectedBuyTime = learnEjb.getFirst(this.selectedLearn).getStartDate().getTime();
        this.selectedSellTime = learnEjb.getLast(this.selectedLearn).getStartDate().getTime();
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
        learnEjb.chkLearnPairs(this.selectedLearn);
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
}
