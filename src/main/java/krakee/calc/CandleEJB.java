package krakee.calc;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import com.mongodb.client.model.Sorts;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.get.TradePairDTO;
import org.bson.Document;

/**
 * Calculate and store candle elements
 *
 * @author rgt
 */
@Stateless
public class CandleEJB {

    static final Logger LOGGER = Logger.getLogger(CandleEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;

    /**
     * Call candle generation methods
     */
    @Asynchronous
    public void callCandle() {
        config.setRunCandle(false);
        this.calcDateList();
        //this.calcCandle();
        config.setRunCandle(true);
    }

    /**
     * Calculate candle values
     */
    private void calcCandle() {
        CandleDTO dto;

        FindIterable<Document> result = config.getCandleColl()
                .find(eq("count", 0L))
                .sort(Sorts.ascending("startDate"))
                .limit(10);
        MongoCursor<Document> cursor = result.iterator();
        while (cursor.hasNext()) {
            dto = new CandleDTO(cursor.next());

            LOGGER.log(Level.INFO, "startDate: " + dto.getStartDate() + "-" + dto.getStopDate());

            //this.calcCandleItem(dto);
        }
        cursor.close();
    }

    private void calcCandleItem(CandleDTO dto) {

        FindIterable<Document> result = config.getTradePairColl()
                .find(and(gte("timeDate", dto.getStartDate()), lt("timeDate", dto.getStopDate())))
                .sort(Sorts.ascending("timeDate"));
        MongoCursor<Document> cursor = result.iterator();
        
        while (cursor.hasNext()) {
            TradePairDTO trade = new TradePairDTO(cursor.next());
            //LOGGER.log(Level.INFO, "calcCandleItem: " + trade.getTimeDate() + "-" + trade.getPrice());
        }
    }

    /**
     * Calculate missing candle dates
     */
    private void calcDateList() {

        Calendar cal;
        Date startDate = this.getStartDate();

        //Stopdate
        Date stopDate = config.getTradePairColl().find()
                .sort(Sorts.descending("timeDate"))
                .first().get("timeDate", Date.class);

        //Store dates
        while (startDate.before(stopDate)) {
            LOGGER.log(Level.INFO, "calcDateList " + startDate + "--" + stopDate);            
            
            CandleDTO dto = new CandleDTO(startDate);
            config.getCandleColl().insertOne(dto.getCandle());
            cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.MINUTE, 30);
            startDate = cal.getTime();
        }
    }

    /**
     * Calculate Start date Get latest Date from the candle collection If it
     * does not exist, get the first from the tradepair and adjust it to 0 or 30
     * minute
     *
     * @return
     */
    private Date getStartDate() {
        Date startDate;
        Calendar cal;

        if (config.getCandleColl().countDocuments() > 0) {
            startDate = config.getCandleColl().find()
                    .sort(Sorts.descending("startDate"))
                    .first()
                    .get("startDate", Date.class);

            cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.MINUTE, 30);
        } else {
            startDate = config.getTradePairColl().find()
                    .sort(Sorts.ascending("timeDate"))
                    .first()
                    .get("timeDate", Date.class);

            cal = Calendar.getInstance();
            cal.setTime(startDate);
            int minute = cal.get(Calendar.MINUTE);

            // Set 30 minutes candle
            if (minute < 30) {
                cal.set(Calendar.MINUTE, 0);
            } else {
                cal.set(Calendar.MINUTE, 30);
            }

            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal.getTime();
    }

}
