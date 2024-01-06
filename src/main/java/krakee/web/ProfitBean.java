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
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.util.Date;
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

    private static final long serialVersionUID = 1L;
    private Long selectedBuyTime;
    private Long selectedSellTime;
    private Long selectedTestNum;
    private String selectedLearnName;
    private String selectedStrategy;
    private Integer treshold;

    @EJB
    private ProfitEJB profitEjb;
    @EJB
    private LearnEJB learnEjb;

    /**
     * Update profit GUI
     */
    public void updateLists() {
        if (this.selectedTestNum != null) {
            ProfitDTO dto = profitEjb.get(selectedTestNum);
            if (dto != null) {
                this.selectedBuyTime = dto.getBuyDate().getTime();
                this.selectedSellTime = dto.getSellDate().getTime();
                this.selectedLearnName = dto.getLearnName();
                this.selectedStrategy = dto.getStrategy();
                this.treshold = dto.getTreshold();
            }
        } else if (this.selectedLearnName != null) {
            this.selectedBuyTime = learnEjb.getFirst(selectedLearnName).getStartDate().getTime();
            this.selectedSellTime = learnEjb.getLast(selectedLearnName).getStartDate().getTime();
        }
    }

    /**
     * Calculate profit
     */
    public void onCalc() {
        ProfitDTO detail = new ProfitDTO();
        detail.setLearnName(selectedLearnName);
        detail.setBuyDate(new Date(this.selectedBuyTime));
        detail.setSellDate(new Date(this.selectedSellTime));
        detail.setStrategy(selectedStrategy);
        detail.setTreshold(treshold);
        
        this.selectedTestNum = profitEjb.calcProfit(detail);     
    }

    public void onDelete() {
        if (this.selectedTestNum != null) {
            ProfitDTO dto = profitEjb.get(selectedTestNum);
            profitEjb.delete(dto);
            
            this.selectedLearnName = null;
        }
    }

    public List<ProfitDTO> getProfitByTestnum() {
        return profitEjb.get();
    }

    /**
     * get profit list, filter by best
     *
     * @return
     */
    public List<ProfitItemDTO> getProfitList() {
        if (this.selectedTestNum != null) {
            ProfitDTO dto = profitEjb.get(selectedTestNum);
            if (dto != null) {
                return dto.getItems();
            }
        }
        return null;
    }

    public boolean isTresholdDisabled() {
        if (selectedStrategy != null) {
            return !"FirstTreshold".equals(this.selectedStrategy);
        }
        return false;
    }

    /**
     * Get best list
     *
     * @return
     */
    public List<ProfitDTO> getBestList() {
        if (this.selectedLearnName != null) {
            return profitEjb.get(this.selectedLearnName);
        }
        return null;
    }

    public List<LearnDTO> getBuyList() {
        if (this.selectedLearnName != null) {
            return learnEjb.getBuy(this.selectedLearnName);
        }
        return null;
    }

    public List<LearnDTO> getSellList() {
        if (this.selectedLearnName != null) {
            return learnEjb.getSell(this.selectedLearnName);
        }
        return null;
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

    public List<String> getStrategyList() {
        return profitEjb.getStrategyList();
    }

    public Long getSelectedTestNum() {
        return selectedTestNum;
    }

    public void setSelectedTestNum(Long selectedTestNum) {
        this.selectedTestNum = selectedTestNum;
    }

    public String getSelectedLearnName() {
        return selectedLearnName;
    }

    public void setSelectedLearnName(String selectedLearnName) {
        this.selectedLearnName = selectedLearnName;
    }

    public String getSelectedStrategy() {
        return selectedStrategy;
    }

    public void setSelectedStrategy(String selectedStrategy) {
        this.selectedStrategy = selectedStrategy;
    }

    public Integer getTreshold() {
        return treshold;
    }

    public void setTreshold(Integer treshold) {
        this.treshold = treshold;
    }
}
