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
                .sort(Sorts.ascending("startDate"))
                .iterator();
        
        List<CandleDTO> list = new ArrayList<>();
         while (cursor.hasNext()) {
             CandleDTO dto = new CandleDTO(cursor.next());
             list.add(dto);
         }
        
        return list;
    }
}
