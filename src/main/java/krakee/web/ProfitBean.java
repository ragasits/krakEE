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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;
import krakee.profit.ProfitItemDTO;
import krakee.profit.ProfitDTO;
import krakee.profit.ProfitEJB;

/**
 * JSF bean for Profit page
 *
 * @author rgt
 */
@SessionScoped
@Named(value = "profitBean")
public class ProfitBean implements Serializable {

    private static final String LEARNNAME = "Első";

    private static final long serialVersionUID = 1L;
    private Long selectedTest;
    private String selectedName;

    private Long selectedBuyTime;
    private Long selectedSellTime;

    @EJB
    private ProfitEJB profitEjb;
    @EJB
    private LearnEJB learnEjb;

    /**
     * Default values
     */
    @PostConstruct
    public void init() {
        this.selectedBuyTime = learnEjb.getFirst(LEARNNAME).getStartDate().getTime();
        this.selectedSellTime = learnEjb.getLast(LEARNNAME).getStartDate().getTime();
    }

    /**
     * Calculate profit
     */
    public void onCalc() {
        Date buyDate = new Date(selectedBuyTime);
        Date sellDate = new Date(selectedSellTime);

        profitEjb.calcProfit(selectedName, buyDate, sellDate);
    }

    public void onDelete() {
        if (this.selectedTest != null) {
            ProfitDTO dto = profitEjb.get(selectedTest);
            profitEjb.delete(dto);
        }
    }

    /**
     * get profit list, filter by best
     *
     * @return
     */
    public List<ProfitItemDTO> getProfitList() {
        if (this.selectedTest != null) {
            ProfitDTO dto = profitEjb.get(selectedTest);
            if (dto!=null){
                return dto.getItems();
            }
        }
        return null;
    }

    /**
     * Get selected profit
     *
     * @return
     */
    public ProfitDTO getProfit() {
        if (this.selectedTest != null) {
            return profitEjb.get(selectedTest);
        }
        return null;
    }

    /**
     * Get best list
     *
     * @return
     */
    public List<ProfitDTO> getBestList() {
        return profitEjb.get();
    }

    public List<LearnDTO> getBuyList() {
        return learnEjb.getBuy();
    }

    public List<LearnDTO> getSellList() {
        return learnEjb.getSell();
    }

    public Long getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(Long selectedTest) {
        this.selectedTest = selectedTest;
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public Long getSelectedBuyTime() {
        return selectedBuyTime;
    }

    public void setSelectedBuyTime(Long selectedBuyTime) {
        this.selectedBuyTime = selectedBuyTime;
    }

    public Long getSelectedSellTime() {
        return selectedSellTime;
    }

    public void setSelectedSellTime(Long selectedSellTime) {
        this.selectedSellTime = selectedSellTime;
    }

}
