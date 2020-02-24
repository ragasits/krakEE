/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.util.ArrayList;
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
 *
 * @author rgt
 */
@Stateless
public class ProfitEJB {

    static final Logger LOGGER = Logger.getLogger(DeltaEJB.class.getCanonicalName());

    private enum Trade {
        BUY, SELL, NONE
    }
    
    @EJB
    ConfigEJB config;
    
    public List<ProfitDTO> get(){
        MongoCursor<Document> cursor = config.getTrainColl()
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
     * Simulate trades
     * Looking for the best training data
     */
    public void calcTrain() {
        CandleDTO candle;
        ProfitSumDTO sum = new ProfitSumDTO(1, BigDecimal.valueOf(1000L));
        
        //Set random to ENUM
        Trade[] values = Trade.values();
        int size = values.length;
        Random random = new Random();
        Trade op;

        //Get last 1k candles
        FindIterable<Document> result = config.getCandleColl()
                .find(eq("calcCandle", true))
                .sort(Sorts.descending("startDate"))
                .limit(1000);

        result = result.sort(Sorts.ascending("startDate"));

        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            candle = new CandleDTO(cursor.next());

            op = values[random.nextInt(size)];
            switch(op){
                case BUY:
                    sum = this.buy(sum, candle);
                    break;
                case SELL:
                    break;
                case NONE:
                    break;
            }          
        }
    }
    
    /**
     * Buy BTC
     * @param sum
     * @param candle
     * @return 
     */
    private ProfitSumDTO buy(ProfitSumDTO sum, CandleDTO candle){
        if (sum.getEuro().compareTo(BigDecimal.ZERO)==1){
            sum.setBtc(sum.getEuro().divide(candle.getClose()));
            sum.setEuro(BigDecimal.ZERO);
        }
        
        return sum;
    }
    
     private ProfitSumDTO sell(ProfitSumDTO sum, CandleDTO candle){
        if (sum.getBtc().compareTo(BigDecimal.ZERO)==1){
            sum.setEuro(sum.getBtc().multiply(candle.getClose()));
            sum.setBtc(BigDecimal.ZERO);
        }
        
        return sum;
    }
}
