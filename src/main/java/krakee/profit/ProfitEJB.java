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
package krakee.profit;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;

/**
 * Calculate profit
 *
 * @author rgt
 */
@Stateless
public class ProfitEJB {

    @EJB
    private ConfigEJB configEjb;
    @EJB
    private LearnEJB learnEjb;
    @EJB
    private CandleEJB candleEjb;

    public List<String> getStrategyList() {
        List<String> stratList = new ArrayList<>();
        stratList.add("FirtSell");
        stratList.add("FirstProfit");
        stratList.add("FirstTreshold");
        return stratList;
    }

    /**
     * Get all data from profit collection
     *
     * @return
     */
    public List<ProfitDTO> get() {
        return configEjb.getProfitColl()
                .find()
                .sort(Sorts.descending("eur"))
                .into(new ArrayList<>());
    }

    /**
     * Get profit filter by learnName
     *
     * @param learnName
     * @return
     */
    public List<ProfitDTO> get(String learnName) {
        return configEjb.getProfitColl()
                .find(eq("learnName", learnName))
                .sort(Sorts.descending("eur"))
                .into(new ArrayList<>());
    }

    /**
     * Get profit filter by testNum
     *
     * @param testNum
     * @return
     */
    public ProfitDTO get(Long testNum) {
        return configEjb.getProfitColl()
                .find(eq("testNum", testNum))
                .first();
    }

    /**
     * Get best profit
     *
     * @return
     */
    public ProfitDTO getBest() {
        return configEjb.getProfitColl()
                .find()
                .sort(Sorts.descending("eur"))
                .first();
    }

    /**
     * Get MAX testNum value
     *
     * @return
     */
    public Long getMaxTestNum() {
        Long testNum = 0L;
        ProfitDTO dto = this.configEjb.getProfitColl()
                .find()
                .sort(Sorts.descending("testNum"))
                .first();
        if (dto != null && dto.getTestNum() != null) {
            testNum = dto.getTestNum();
        }
        return testNum;
    }

    /**
     * Delete one profit
     *
     * @param dto
     */
    public void delete(ProfitDTO dto) {
        configEjb.getProfitColl().deleteOne(eq("_id", dto.getId()));
    }
    
    
    /**
     * Delete all profits by learName
     * @param learnName 
     */
    public void delete(String learnName){
        configEjb.getProfitColl().deleteMany(
                eq("learnName",learnName)
        );
    }

    /**
     * Calculate profit, select strategy
     *
     * @param profit
     * @return
     */
    public ProfitDTO calcProfit(ProfitDTO profit) {
        switch (profit.getStrategy()) {
            case "FirtSell":
                return calcFirtSell(profit);
            case "FirstProfit":
                return calcFirstProfit(profit);
            case "FirstTreshold":
                return calcTresholdProfit(profit);
        }
        return null;
    }

    /**
     * Calculate profit - strategy: Fisrt sell
     *
     * @param profit
     * @return
     */
    public ProfitDTO calcFirtSell(ProfitDTO profit) {
        double eur = 1000;
        double btc = 0;
        double lastEur = 0;
        Long testNum = 1L + this.getMaxTestNum();

        List<LearnDTO> learnList = learnEjb.get(profit.getLearnName(), profit.getBuyDate(), profit.getSellDate());
        List<ProfitItemDTO> profitList = new ArrayList<>();

        for (LearnDTO learn : learnList) {
            CandleDTO candle = this.candleEjb.get(learn.getStartDate());
            ProfitItemDTO dto = new ProfitItemDTO(candle, learn.getTrade(), testNum);

            switch (learn.getTrade()) {
                case ProfitDTO.BUY:
                    if (eur > 0) {
                        dto.buyBtc(eur);
                        eur = dto.getEur();
                        btc = dto.getBtc();
                        profitList.add(dto);
                    }
                    break;
                case ProfitDTO.SELL:
                    if (btc > 0) {
                        dto.sellBtc(btc);
                        eur = dto.getEur();
                        lastEur = eur;
                        btc = dto.getBtc();
                        profitList.add(dto);
                    }
                    break;
                case ProfitDTO.NONE:
                    break;
            }
        }

        //Store profit
        profit.setTestNum(testNum);
        profit.setEur(lastEur);
        profit.setItems(profitList);
        configEjb.getProfitColl().insertOne(profit);

        return profit;
    }

    /**
     * *
     * Calculate profit - strategy: Fisrt profit (buy<sell)
     *
     * @param profit
     * @return
     */
    public ProfitDTO calcFirstProfit(ProfitDTO profit) {
        double eur = 1000;
        double btc = 0;
        double lastEur = 0;
        Long testNum = 1L + this.getMaxTestNum();

        LearnDTO buyLearn = null;
        LearnDTO sellLearn = null;

        List<ProfitItemDTO> profitList = new ArrayList<>();
        List<LearnDTO> learnList = learnEjb.get(profit.getLearnName(), profit.getBuyDate(), profit.getSellDate());
        for (LearnDTO learnDto : learnList) {

            //Fist Buy
            if (learnDto.isBuy() && buyLearn == null) {
                buyLearn = learnDto;
            }

            //First Profit (sell>buy)
            if (learnDto.isSell() && buyLearn != null && sellLearn == null) {
                if (buyLearn.getClose().compareTo(learnDto.getClose()) == -1) {
                    sellLearn = learnDto;
                }
            }

            //Calculate profit
            if (buyLearn != null && sellLearn != null) {

                //Buy
                CandleDTO candle = this.candleEjb.get(buyLearn.getStartDate());
                ProfitItemDTO profitDto = new ProfitItemDTO(candle, buyLearn.getTrade(), testNum);

                profitDto.buyBtc(eur);
                eur = profitDto.getEur();
                btc = profitDto.getBtc();
                profitList.add(profitDto);

                //Sell
                candle = this.candleEjb.get(sellLearn.getStartDate());
                profitDto = new ProfitItemDTO(candle, sellLearn.getTrade(), testNum);

                profitDto.sellBtc(btc);
                eur = profitDto.getEur();
                lastEur = eur;
                btc = profitDto.getBtc();
                profitList.add(profitDto);

                //reset pair
                buyLearn = null;
                sellLearn = null;
            }
        }

        //Store profit
        profit.setTestNum(testNum);
        profit.setEur(lastEur);
        profit.setItems(profitList);
        configEjb.getProfitColl().insertOne(profit);

        return profit;
    }

    /**
     * Calculate profit - strategy: Treshold (sell-buy>treshold)
     *
     * @param profit
     * @return
     */
    public ProfitDTO calcTresholdProfit(ProfitDTO profit) {
        double eur = 1000;
        double btc = 0;
        double lastEur = 0;
        Long testNum = 1L + this.getMaxTestNum();

        LearnDTO buyLearn = null;
        LearnDTO sellLearn = null;

        List<ProfitItemDTO> profitList = new ArrayList<>();
        List<LearnDTO> learnList = learnEjb.get(profit.getLearnName(), profit.getBuyDate(), profit.getSellDate());
        for (LearnDTO learnDto : learnList) {

            //Fist Buy
            if (learnDto.isBuy() && buyLearn == null) {
                buyLearn = learnDto;
            }

            //First Profit (sell>buy)
            if (learnDto.isSell() && this.sellCondition(buyLearn, sellLearn, learnDto, profit.getTreshold())) {
                sellLearn = learnDto;
            }

            //Calculate profit
            if (buyLearn != null && sellLearn != null) {

                //Buy
                CandleDTO candle = this.candleEjb.get(buyLearn.getStartDate());
                ProfitItemDTO profitDto = new ProfitItemDTO(candle, buyLearn.getTrade(), testNum);

                profitDto.buyBtc(eur);
                eur = profitDto.getEur();
                btc = profitDto.getBtc();
                profitList.add(profitDto);

                //Sell
                candle = this.candleEjb.get(sellLearn.getStartDate());
                profitDto = new ProfitItemDTO(candle, sellLearn.getTrade(), testNum);

                profitDto.sellBtc(btc);
                eur = profitDto.getEur();
                lastEur = eur;
                btc = profitDto.getBtc();
                profitList.add(profitDto);

                //reset pair
                buyLearn = null;
                sellLearn = null;
            }
        }

        //Store profit
        profit.setTestNum(testNum);
        profit.setEur(lastEur);
        profit.setItems(profitList);
        configEjb.getProfitColl().insertOne(profit);

        return profit;
    }

    /**
     * Calculate sell Condition (Treshold)
     * @param buyLearn
     * @param sellLearn
     * @param learnDto
     * @param treshold
     * @return 
     */
    private boolean sellCondition(LearnDTO buyLearn, LearnDTO sellLearn, LearnDTO learnDto, Integer treshold) {
        if (buyLearn != null && sellLearn == null) {

            CandleDTO candle = this.candleEjb.get(buyLearn.getStartDate());
            BigDecimal minProfit = candle.getClose()
                    .divide(BigDecimal.valueOf(100L), 0, RoundingMode.CEILING)
                    .multiply(BigDecimal.valueOf(treshold));

            BigDecimal diff = learnDto.getClose()
                    .subtract(buyLearn.getClose());

            if (diff.signum() > 0 && diff.compareTo(minProfit) > 0) {
                return true;
            }
        }
        return false;
    }

}
