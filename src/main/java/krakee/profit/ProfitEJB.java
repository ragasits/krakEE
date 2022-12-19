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
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
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

    static final Logger LOGGER = Logger.getLogger(ProfitEJB.class.getCanonicalName());

    @EJB
    private ConfigEJB configEjb;
    @EJB
    private LearnEJB learnEjb;
    @EJB
    private CandleEJB candleEjb;

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
     * @param dto 
     */
    public void delete(ProfitDTO dto) {
        configEjb.getProfitColl().deleteOne(eq("_id", dto.getId()));
    }

    /**
     * Calculate one learn profit
     *
     * @param learnName
     * @param buyDate
     * @param sellDate
     */
    public void calcProfit(String learnName, Date buyDate, Date sellDate) {
        double eur = 1000;
        double btc = 0;
        double lastEur = 0;
        List<ProfitItemDTO> profitList = new ArrayList<>();
        Long testNum = 1L + this.getMaxTestNum();

        List<LearnDTO> learnList = learnEjb.get(learnName, buyDate, sellDate);
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
        configEjb.getProfitColl().insertOne(new ProfitDTO(learnName, testNum, lastEur, profitList));
    }
}
