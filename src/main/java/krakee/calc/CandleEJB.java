package krakee.calc;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
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
import org.bson.types.Decimal128;

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

    private int candleSize = 5000;

    /**
     * Call candle generation methods
     */
    @Asynchronous
    public void callCandle() {
        config.setRunCandle(false);
        this.setLastCandleCalcToFalse();
        this.calcDateList();
        this.calcCandle();
        config.setRunCandle(true);
    }

    /**
     * Set the last Candle calcCandle value to false (Incremental running)
     */
    private void setLastCandleCalcToFalse() {
        //Get the last Candle
        Document doc = config.getCandleColl()
                .find()
                .sort(Sorts.descending("startDate"))
                .first();
        CandleDTO dto = new CandleDTO(doc);
        dto.setCalcCandle(false);

        //Update last Candle
        config.getCandleColl().replaceOne(
                eq("_id", dto.getId()), dto.getCandle());
    }

    /**
     * Calculate candle values Max 10K rows (Prevent the timeout?)
     */
    private void calcCandle() {
        CandleDTO dto;
        int i = 0;

        FindIterable<Document> result = config.getCandleColl()
                .find(eq("calcCandle", false))
                .limit(5000);

        try (MongoCursor<Document> cursor = result.iterator()) {
            while (cursor.hasNext()) {
                dto = new CandleDTO(cursor.next());
                this.calcCandleItem(dto);
                i++;
            }
        }
        this.candleSize = i;
        LOGGER.log(Level.INFO, "calcCandle:" + i);
    }

    /**
     * Calculate CandelItem values
     *
     * @param dto
     */
    private void calcCandleItem(CandleDTO dto) {
        Document doc;

        FindIterable<Document> result = config.getTradePairColl()
                .find(and(gte("timeDate", dto.getStartDate()), lt("timeDate", dto.getStopDate())))
                .sort(Sorts.ascending("timeDate"));
        MongoCursor<Document> cursor = result.iterator();

        Integer count = 0;
        Integer countBuy = 0;
        Integer countSell = 0;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalBuy = BigDecimal.ZERO;
        BigDecimal totalSell = BigDecimal.ZERO;
        BigDecimal volume = BigDecimal.ZERO;
        BigDecimal volumeBuy = BigDecimal.ZERO;
        BigDecimal volumeSell = BigDecimal.ZERO;

        doc = result.sort(Sorts.ascending("timeDate")).first();
        if (doc != null) {
            dto.setOpen(((Decimal128) doc.get("price")).bigDecimalValue());
        }

        doc = result.sort(Sorts.descending("timeDate")).first();
        if (doc != null) {
            dto.setClose(((Decimal128) doc.get("price")).bigDecimalValue());
        }

        doc = result.sort(Sorts.ascending("price")).first();
        if (doc != null) {
            dto.setLow(((Decimal128) doc.get("price")).bigDecimalValue());
        }

        doc = result.sort(Sorts.descending("price")).first();
        if (doc != null) {
            dto.setHigh(((Decimal128) doc.get("price")).bigDecimalValue());
        }

        while (cursor.hasNext()) {
            TradePairDTO trade = new TradePairDTO(cursor.next());
            count++;
            total = total.add(trade.getTotal());
            volume = volume.add(trade.getVolume());

            if (String.valueOf("b").equals(trade.getBuySel())) {
                countBuy++;
                totalBuy = totalBuy.add(trade.getTotal());
                volumeBuy = volumeBuy.add(trade.getVolume());
            } else if (String.valueOf("s").equals(trade.getBuySel())) {
                countSell++;
                totalSell = totalSell.add(trade.getTotal());
                volumeSell = volumeBuy.add(trade.getVolume());
            }
        }
        dto.setCount(count);
        dto.setCountBuy(countBuy);
        dto.setCountSell(countSell);
        dto.setTotal(total);
        dto.setTotalBuy(totalBuy);
        dto.setTotalSell(totalSell);
        dto.setVolume(volume);
        dto.setVolumeBuy(volumeBuy);
        dto.setVolumeSell(volumeSell);
        dto.setCalcCandle(true);

        config.getCandleColl().replaceOne(
                eq("_id", dto.getId()),
                dto.getCandle());
    }

    /**
     * Calculate missing candle dates
     */
    private void calcDateList() {

        Calendar cal = Calendar.getInstance();
        Date startDate = this.getStartDate();

        //Stopdate
        Date stopDate = config.getTradePairColl().find()
                .sort(Sorts.descending("timeDate"))
                .first().get("timeDate", Date.class);

        //Store dates
        while (startDate.before(stopDate)) {
            LOGGER.log(Level.INFO, "calcDateList " + startDate + "-" + stopDate);

            CandleDTO dto = new CandleDTO(startDate);
            config.getCandleColl().insertOne(dto.getCandle());
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
                    .getDate("startDate");
        } else {
            startDate = config.getTradePairColl().find()
                    .sort(Sorts.ascending("timeDate"))
                    .first()
                    .getDate("timeDate");
        }
        return calcCandel30Min(startDate);
    }

    /**
     * Calculate 30min dates
     *
     * @param date
     * @return
     */
    public Date calcCandel30Min(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int minute = cal.get(Calendar.MINUTE);

        // Set 30 minutes candle
        if (minute < 30) {
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.MINUTE, 30);
        }

        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public int getCandleSize() {
        return candleSize;
    }

    @Override
    public String toString() {
        return "CandleEJB{" + "config=" + config + ", candleSize=" + candleSize + '}';
    }

    
    
}
