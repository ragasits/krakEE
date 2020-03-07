/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import org.bson.Document;

/**
 * Calculate profit
 *
 * @author rgt
 */
@Stateless
public class ProfitEJB {

    static final Logger LOGGER = Logger.getLogger(ProfitEJB.class.getCanonicalName());

    private Boolean isBest;
    private ProfitBestDTO lastBest;

    @EJB
    private ConfigEJB config;
    @EJB
    private ProfitBestEJB bestEjb;

    /**
     * Get all data from profit collection
     *
     * @return
     */
    public List<ProfitDTO> get() {
        MongoCursor<Document> cursor = config.getProfitColl()
                .find()
                .iterator();

        List<ProfitDTO> list = new ArrayList<>();
        while (cursor.hasNext()) {
            ProfitDTO dto = new ProfitDTO(cursor.next());
            list.add(dto);
        }
        return list;
    }

    /**
     * Get profit filter by testNum
     *
     * @param testNum
     * @return
     */
    public List<ProfitDTO> get(Long testNum) {
        MongoCursor<Document> cursor = config.getProfitColl()
                .find(eq("testNum", testNum))
                .iterator();

        List<ProfitDTO> list = new ArrayList<>();
        while (cursor.hasNext()) {
            ProfitDTO dto = new ProfitDTO(cursor.next());
            list.add(dto);
        }
        return list;
    }

    /**
     * Get last X candles
     *
     * @param last
     * @return
     */
    private List<CandleDTO> getLastXCandles(int last) {
        CandleDTO candle;
        List<CandleDTO> candleList = new ArrayList<>();

        //Get first startDate
        Date first = config.getCandleColl()
                .find(eq("calcCandle", true))
                .sort(Sorts.descending("startDate"))
                .limit(last)
                .skip(last - 1)
                .first()
                .getDate("startDate");

        //Get candles from first startDate
        MongoCursor<Document> cursor = config.getCandleColl()
                .find(gte("startDate", first))
                .sort(Sorts.ascending("startDate"))
                .iterator();

        while (cursor.hasNext()) {
            candle = new CandleDTO(cursor.next());
            candleList.add(candle);
        }

        return candleList;
    }

    /**
     * Looking for the best Profit buy/sell combinations
     */
    @Asynchronous
    public void calcProfit() {
        this.isBest = false;
        List<CandleDTO> candleList = this.getLastXCandles(1000);
        ProfitBestDTO best = bestEjb.getMaxTest();
        Long testNum = 0L;
        if (best != null) {
            testNum = best.getTestNum() + 1L;
        }

        while (!this.isBest) {
            this.calcOneProfit(candleList, testNum++);
        }
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
        List<ProfitDTO> profitList = new ArrayList<>();

        //Set random
        Random random = new Random();

        for (CandleDTO candle : candleList) {
            trade = ProfitDTO.OP[random.nextInt(ProfitDTO.OP.length)];
            ProfitDTO dto = new ProfitDTO(candle, trade, testNum);

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
            this.lastBest = bestEjb.getBest();
            if (this.lastBest == null) {
                this.saveProfit(profitList, new ProfitBestDTO(testNum, lastEur));
            } else if (lastEur>lastBest.getEur()) {
                this.saveProfit(profitList, new ProfitBestDTO(testNum, lastEur));
            }
        } else if (lastEur>lastBest.getEur()) {
            this.lastBest = bestEjb.getBest();
            if (lastEur>lastBest.getEur()) {
                this.saveProfit(profitList, new ProfitBestDTO(testNum, lastEur));
            }
        }
    }

    /**
     * Save latest best profit to Mongo
     *
     * @param docList
     * @param dto
     */
    private void saveProfit(List<ProfitDTO> profitList, ProfitBestDTO dto) {
        //save best
        config.getProfitBestColl().insertOne(dto.getProfitBest());

        //Save items
        List<Document> docList = new ArrayList<>();
        profitList.forEach((profit) -> {
            docList.add(profit.getProfit());
        });

        config.getProfitColl().insertMany(docList, new InsertManyOptions());
        this.isBest = true;
        LOGGER.log(Level.SEVERE, "Add new best profit ({0}:{1}", new Object[]{dto.getTestNum(), dto.getEur()});
    }
}
