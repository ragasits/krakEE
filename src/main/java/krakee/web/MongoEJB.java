package krakee.web;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.get.TradePairDTO;
import org.bson.Document;

/**
 *
 * @author rgt
 */
@Stateless
public class MongoEJB {

    @EJB
    ConfigEJB config;

    /**
     * get Last limit size trade pairs
     * @param limit
     * @return 
     */
    public List<TradePairDTO> getLastTrades(int limit) {
        MongoCursor<Document> cursor = config.getTradePairColl()
                .find()
                .sort(Sorts.descending("timeDate"))
                .limit(limit)
                .iterator();
        
        List<TradePairDTO> list = new ArrayList<>();
         while (cursor.hasNext()) {
             TradePairDTO dto = new TradePairDTO(cursor.next());
             list.add(dto);
         }
        
        return list;
    }    
    
    
    
    
    /**
     * get last "limit" size candles
     * @param limit
     * @return 
     */
    public List<CandleDTO> getLastCandles(int limit) {
        MongoCursor<Document> cursor = config.getCandleColl()
                .find()
                .sort(Sorts.descending("startDate"))
                .limit(limit)
                .iterator();
        
        List<CandleDTO> list = new ArrayList<>();
         while (cursor.hasNext()) {
             CandleDTO dto = new CandleDTO(cursor.next());
             list.add(dto);
         }
        
        return list;
    }    
    
    

    /**
     * Get latest date value from Candle collection
     *
     * @return
     */
    public Date getLatesDateFromCandle() {
        try {
            return config.getCandleColl()
                    .find()
                    .sort(Sorts.descending("startDate"))
                    .first()
                    .getDate("startDate");
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Get candles
     *
     * @param startDate
     * @param stopDate
     * @return
     */
    public List<CandleDTO> getCandleChartFromCandle(Date startDate, Date stopDate) {
        MongoCursor<Document> cursor = config.getCandleColl()
                .find(and(gte("startDate", startDate), lte("startDate", stopDate)))
                .sort(Sorts.descending("startDate"))
                .iterator();
        
        List<CandleDTO> list = new ArrayList<>();
         while (cursor.hasNext()) {
             CandleDTO dto = new CandleDTO(cursor.next());
             list.add(dto);
         }
        
        return list;
    }
}
