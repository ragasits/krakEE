/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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

    private Boolean isBest;
    private ProfitDTO lastBest;

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
     * Get last X candles
     *
     * @param last
     * @return
     */
    private List<CandleDTO> getLastXCandles(int last) {
        CandleDTO candle;

        //Get first startDate
        CandleDTO dto = config.getCandleColl()
                .find(eq("calcCandle", true))
                .sort(Sorts.descending("startDate"))
                .limit(last)
                .skip(last - 1)
                .first();

        Date first = dto.getStartDate();

        //Get candles from first startDate
        return config.getCandleColl()
                .find(gte("startDate", first))
                .sort(Sorts.ascending("startDate"))
                .into(new ArrayList<>());
    }


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
     * Looking for the best Profit buy/sell combinations
     */
    @Asynchronous
    public void calcProfit() {
        this.isBest = false;
        List<CandleDTO> candleList = this.getLastXCandles(1000);
        Long testNum = this.getMaxTestNum();

        while (!this.isBest) {
            this.calcOneProfit(candleList, testNum++);
        }
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

    /**
     * Simulate trades Looking for the best training data
     *
     * @param candleList
     * @param testNum
     */
    //@Asynchronous
    public void calcOneProfit(List<CandleDTO> candleList, Long testNum) {
        double eur = 1000;
        double btc = 0;
        double lastEur = 0;
        String trade;
        List<ProfitItemDTO> profitList = new ArrayList<>();

        //Set random
        Random random = new Random();

        for (CandleDTO candle : candleList) {
            trade = ProfitDTO.OP[random.nextInt(ProfitDTO.OP.length)];
            ProfitItemDTO dto = new ProfitItemDTO(candle, trade, testNum);

            switch (trade) {
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

        //Store better profit (cached version)
        if (this.lastBest == null) {
            this.lastBest = this.getBest();
            if (this.lastBest == null) {
                config.getProfitColl().insertOne(new ProfitDTO(testNum, lastEur, profitList));
            } else if (lastEur > lastBest.getEur()) {
                config.getProfitColl().insertOne(new ProfitDTO(testNum, lastEur, profitList));
            }
        } else if (lastEur > lastBest.getEur()) {
            this.lastBest = this.getBest();
            if (lastEur > lastBest.getEur()) {
                config.getProfitColl().insertOne(new ProfitDTO(testNum, lastEur, profitList));
            }
        }
    }
}
