package krakee.web;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.get.TradePairDTO;
import org.bson.Document;
import org.bson.types.Decimal128;

/**
 *
 * @author rgt
 */
@Stateless
public class MongoEJB {

    @EJB
    ConfigEJB config;
    @EJB
    CandleEJB candle;

    /**
     * Check Candle consistency I. Seek missing dates
     *
     * @return
     */
    public List<Date> chkCandleDates() {
        Calendar cal = Calendar.getInstance();
        List<Date> list = new ArrayList<>();

        //get first trade date
        Date firstDate = config.getTradePairColl()
                .find()
                .sort(Sorts.ascending("timeDate"))
                .first().get("timeDate", Date.class);
        firstDate = candle.calcCandel30Min(firstDate);

        //Get last trade date
        Date lastDate = config.getTradePairColl()
                .find()
                .sort(Sorts.descending("timeDate"))
                .first().get("timeDate", Date.class);

        int i = 0;
        while (firstDate.before(lastDate)) {
            i++;
            //Seek date
            Document doc = config.getCandleColl()
                    .find(eq("startDate", firstDate))
                    .first();
            if (doc==null){
                list.add(firstDate);
            }

            //calc next value
            cal.setTime(firstDate);
            cal.add(Calendar.MINUTE, 30);
            firstDate = cal.getTime();
        }

        System.out.println("chkCandleDates: "+i);
        return list;
    }

    /**
     * Check Trade consistency
     *
     * @return
     */
    public List<String> chkTradePair() {
        //Chk last<>max(time)
        MongoCursor<Document> cursor = config.getTradePairColl().aggregate(
                Arrays.asList(
                        Aggregates.group("$last", Accumulators.max("max", "$time"))
                )
        ).cursor();

        List<String> list = new ArrayList();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String id = doc.getString("_id").substring(0, 14);
            String max = ((Decimal128) doc.get("max")).bigDecimalValue().toString().replace(".", "");

            if (!id.equals(max)) {
                list.add("last:" + id + " max($time):" + max);
            }
        }
        return list;
    }

    /**
     * get Last limit size trade pairs
     *
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
     *
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
