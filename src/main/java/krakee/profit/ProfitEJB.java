/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.calc.DeltaEJB;
import org.bson.Document;

/**
 * Calculate profit
 *
 * @author rgt
 */
@Stateless
public class ProfitEJB {

    static final Logger LOGGER = Logger.getLogger(DeltaEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;

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
     * Simulate trades Looking for the best training data
     */
    public void calcProfit() {
        CandleDTO candle;
        BigDecimal eur = BigDecimal.valueOf(1000L).setScale(5, RoundingMode.HALF_UP);
        BigDecimal btc = BigDecimal.ZERO.setScale(5,RoundingMode.HALF_UP);
        
        
        String op;

        //Set random
        Random random = new Random();

        //Get last 100 candles
        Date first = config.getCandleColl()
                .find(eq("calcCandle", true))
                .sort(Sorts.descending("startDate"))
                .limit(100)
                .skip(99)
                .first()
                .getDate("startDate");

        MongoCursor<Document> cursor = config.getCandleColl()
                .find(gte("startDate", first))
                .sort(Sorts.ascending("startDate"))
                .iterator();

        while (cursor.hasNext()) {
            candle = new CandleDTO(cursor.next());

            op = ProfitDTO.OP[random.nextInt(ProfitDTO.OP.length)];
            ProfitDTO dto = new ProfitDTO(candle, op);

            switch (op) {
                case ProfitDTO.BUY:
                    if (eur.compareTo(BigDecimal.ZERO) == 1) {
                        dto.buyBtc(eur);
                        eur = dto.getEur();
                        btc = dto.getBtc();
                    }
                    break;
                case ProfitDTO.SELL:
                    if (btc.compareTo(BigDecimal.ZERO) == 1) {
                        dto.sellBtc(btc);
                        eur = dto.getEur();
                        btc = dto.getBtc();
                    }
                    break;
                case ProfitDTO.NONE:
                    break;
            }
            config.getProfitColl().insertOne(dto.getProfit());
        }
    }
}
