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
    private ProfitDTO detail = new ProfitDTO();

    @EJB
    private ProfitEJB profitEjb;
    @EJB
    private LearnEJB learnEjb;

    /**
     * Update Buy, Sell date
     */
    public void updateLearn() {
        detail.setBuyDate(learnEjb.getFirst(detail.getLearnName()).getStartDate());
        detail.setSellDate(learnEjb.getLast(detail.getLearnName()).getStartDate());
    }

    /**
     * Get tests by LearnName
     * @return 
     */
    public List<ProfitDTO> getTestNumList() {
        if (this.detail.getLearnName() != null) {
            return profitEjb.get(detail.getLearnName());
        }
        return null;
    }

    /**
     * Calculate profit
     */
    public void onCalc() {
        this.detail = profitEjb.calcProfit(detail.clone());
    }

    /**
     * Calculate profits with different strategies and Tresholds
     */
    public void onCalcAll() {
        this.onDeleteAll();

        //FirtSell
        detail.setStrategy("FirtSell");
        this.onCalc();

        //FirstProfit
        detail.setStrategy("FirstProfit");
        this.onCalc();

        //FirstTreshold
        detail.setStrategy("FirstTreshold");

        for (int i = 0; i < 10; i++) {
            detail.setTreshold(i+1);
            this.onCalc();
        }
    }

    /**
     * Delete One profit
     */
    public void onDelete() {
        if (this.detail.getTestNum() != null) {
            ProfitDTO dto = profitEjb.get(this.detail.getTestNum());
            profitEjb.delete(dto);
        }
    }

    /**
     * Delete the all profit by LearName
     */
    public void onDeleteAll() {
        if (this.detail.getLearnName() != null) {
            profitEjb.delete(this.detail.getLearnName());
        }
    }

    public ProfitDTO getDetail() {
        return detail;
    }

    /**
     * get profit list, filter by best
     *
     * @return
     */
    public List<ProfitItemDTO> getProfitList() {
        if (this.detail.getTestNum() != null) {
            ProfitDTO dto = profitEjb.get(this.detail.getTestNum());
            if (dto != null) {
                return dto.getItems();
            }
        }
        return null;
    }

    public boolean isTresholdDisabled() {
        if (detail.getStrategy() != null) {
            return !"FirstTreshold".equals(detail.getStrategy());
        }
        return false;
    }

    public List<LearnDTO> getBuyList() {
        if (this.detail != null && this.detail.getLearnName() != null) {
            return learnEjb.getBuy(this.detail.getLearnName());
        }
        return null;
    }

    public List<LearnDTO> getSellList() {
        if (this.detail != null && this.detail.getLearnName() != null) {
            return learnEjb.getSell(this.detail.getLearnName());
        }
        return null;
    }

    public Long getSelectedBuyTime() {
        if (detail.getBuyDate() != null) {
            return detail.getBuyDate().getTime();
        }
        return null;
    }

    public void setSelectedBuyTime(Long selectedBuyTime) {
        if (selectedBuyTime != null) {
            detail.setBuyDate(new Date(selectedBuyTime));
        } else {
            detail.setBuyDate(null);
        }
    }

    public Long getSelectedSellTime() {
        if (detail.getSellDate() != null) {
            return detail.getSellDate().getTime();
        }
        return null;
    }

    public void setSelectedSellTime(Long selectedSellTime) {
        if (selectedSellTime != null) {
            detail.setSellDate(new Date(selectedSellTime));
        } else {
            detail.setSellDate(null);
        }
    }

    public List<String> getStrategyList() {
        return profitEjb.getStrategyList();
    }

    public Long getSelectedTestNum() {
        if (detail != null) {
            return detail.getTestNum();
        }
        return null;
    }

    public void setSelectedTestNum(Long selectedTestNum) {
        if (selectedTestNum != null) {
            this.detail = profitEjb.get(selectedTestNum);
        } else {
            String learName = detail.getLearnName();
            this.detail = new ProfitDTO();
            detail.setLearnName(learName);
        }
    }

    public String getSelectedLearnName() {
        if (detail != null) {
            return this.detail.getLearnName();
        }
        return null;
    }

    public void setSelectedLearnName(String selectedLearnName) {
        if (selectedLearnName != null) {
            detail.setLearnName(selectedLearnName);
        } else {
            detail.setLearnName(null);
        }
    }

    public String getSelectedStrategy() {
        return detail.getStrategy();
    }

    public void setSelectedStrategy(String selectedStrategy) {
        if (selectedStrategy != null) {
            detail.setStrategy(selectedStrategy);
        } else {
            detail.setStrategy(null);
        }
    }

    public Integer getTreshold() {
        return detail.getTreshold();
    }

    public void setTreshold(Integer treshold) {
        if (treshold != null) {
            this.detail.setTreshold(treshold);
        } else {
            this.detail.setTreshold(null);
        }
    }
}
