package krakee.calc;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.get.TradePairDTO;

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
    @EJB
    DeltaEJB delta;
    @EJB
    BollingerEJB bollinger;
    
    /**
     * Get a Candle
     * @param startDate
     * @return 
     */
    public CandleDTO get(Date startDate){
        return this.config.getCandleColl()
                .find(eq("startDate", startDate))
                .first();
    }

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
        delta.calculateDelta();
        bollinger.calculateBollinger();
        config.setRunCandle(true);
    }

    /**
     * Set the last Candle calcCandle value to false (Incremental running)
     */
    private void setLastCandleCalcToFalse() {

        //Get last startDate from Candle
        CandleDTO candle = config.getCandleColl()
                .find()
                .sort(Sorts.descending("startDate"))
                .first();
        if (candle == null || candle.getStartDate() == null) {
            return;
        }

        //Get last date from TradePair
        TradePairDTO trade = config.getTradePairColl()
                .find()
                .sort(Sorts.descending("timeDate"))
                .first();
        if (trade == null) {
            return;
        }

        //If the tradePair is newer then Candle - Reset
        if (trade.getTimeDate().after(candle.getStartDate())) {
            candle.setCalcCandle(false);
            candle.getDelta().setCalcDelta(false);
            candle.getBollinger().setCalcBollinger(false);

            //Update last Candle
            config.getCandleColl().replaceOne(
                    eq("_id", candle.getId()), candle);
        }
    }

    /**
     * Calculate candle values Max 10K rows (Prevent the timeout?)
     */
    private void calcCandle() {
        int i = 0;

        List<CandleDTO> candleList = config.getCandleColl()
                .find(eq("calcCandle", false))
                .limit(5000)
                .into(new ArrayList<>());

        for (CandleDTO dto : candleList) {
            this.calcCandleItem(dto);
            i++;
        }

        this.candleSize = i;
        LOGGER.log(Level.INFO, "calcCandle:{0}", i);
    }

    /**
     * Calculate CandelItem values
     *
     * @param dto
     */
    private void calcCandleItem(CandleDTO dto) {
        TradePairDTO trade;

        FindIterable<TradePairDTO> result = config.getTradePairColl()
                .find(and(gte("timeDate", dto.getStartDate()), lt("timeDate", dto.getStopDate())))
                .sort(Sorts.ascending("timeDate"));
        MongoCursor<TradePairDTO> cursor = result.iterator();

        Integer count = 0;
        Integer countBuy = 0;
        Integer countSell = 0;
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalBuy = BigDecimal.ZERO;
        BigDecimal totalSell = BigDecimal.ZERO;
        BigDecimal volume = BigDecimal.ZERO;
        BigDecimal volumeBuy = BigDecimal.ZERO;
        BigDecimal volumeSell = BigDecimal.ZERO;

        trade = result.sort(Sorts.ascending("timeDate")).first();
        if (trade != null) {
             dto.setOpen(trade.getPrice());
        }

        trade = result.sort(Sorts.descending("timeDate")).first();
        if (trade != null) {
            dto.setClose(trade.getPrice());
        }

        trade = result.sort(Sorts.ascending("price")).first();
        if (trade != null) {
            dto.setLow(trade.getPrice());
        }

        trade = result.sort(Sorts.descending("price")).first();
        if (trade != null) {
            dto.setHigh(trade.getPrice());
        }

        while (cursor.hasNext()) {
            trade = cursor.next();
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
                eq("_id", dto.getId()), dto);
    }

    /**
     * Calculate missing candle dates
     */
    private void calcDateList() {

        Calendar cal = Calendar.getInstance();
        Date startDate = this.getStartDate();

        //Stopdate
        TradePairDTO dto = config.getTradePairColl().find()
                .sort(Sorts.descending("timeDate"))
                .first();

        Date stopDate = dto.getTimeDate();

        //Store dates
        while (startDate.before(stopDate)) {
            LOGGER.log(Level.INFO, "calcDateList " + startDate + "-" + stopDate);

            config.getCandleColl().insertOne(new CandleDTO(startDate));
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

        if (config.getCandleColl().countDocuments() > 0) {
            CandleDTO dto = config.getCandleColl().find()
                    .sort(Sorts.descending("startDate"))
                    .first();
            startDate = dto.getStartDate();

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.MINUTE, 30);
            startDate = cal.getTime();

        } else {
            TradePairDTO dto = config.getTradePairColl().find()
                    .sort(Sorts.ascending("timeDate"))
                    .first();
            startDate = dto.getTimeDate();
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
