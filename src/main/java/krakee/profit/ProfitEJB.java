/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
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
    private ConfigEJB config;
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
        return config.getProfitColl()
                .find()
                .sort(Sorts.descending("eur"))
                .into(new ArrayList<>());
    }
    

    /**
     * Get profit filter by learnName
     * @param learnName
     * @return 
     */
    public List<ProfitDTO> get(String learnName) {
        return config.getProfitColl()
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
        return config.getProfitColl()
                .find(eq("testNum", testNum))
                .first();
    }

    /**
     * Get best profit
     *
     * @return
     */
    public ProfitDTO getBest() {
        return config.getProfitColl()
                .find()
                .sort(Sorts.descending("eur"))
                .first();
    }

    /**
     * Get MAX testNum value
     * @return 
     */
    public Long getMaxTestNum() {
        Long testNum = 0L;
        ProfitDTO dto = this.config.getProfitColl()
                .find()
                .sort(Sorts.descending("testNum"))
                .first();
        if (dto != null && dto.getTestNum() != null) {
            testNum = dto.getTestNum();
        }
        return testNum;
    }

    /**
     * Calculate one learn profit
     *
     * @param learnName
     */
    public void calcProfit(String learnName) {
        double eur = 1000;
        double btc = 0;
        double lastEur = 0;
        List<ProfitItemDTO> profitList = new ArrayList<>();
        Long testNum = 1L + this.getMaxTestNum();

        List<LearnDTO> learnList = learnEjb.get(learnName);
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
        config.getProfitColl().insertOne(new ProfitDTO(learnName, testNum, lastEur, profitList));
    }
}
