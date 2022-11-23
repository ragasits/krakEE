package krakee.calc;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.MyException;
import krakee.get.TradePairDTO;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Calculate and store candle elements
 *
 * @author rgt
 */
@Stateless
public class CandleEJB {
    
    static final Logger LOGGER = Logger.getLogger(CandleEJB.class.getCanonicalName());
    private static final String STARTDATE = "startDate";
    private static final String TIMEDATE = "timeDate";
    
    @EJB
    ConfigEJB configEjb;
    @EJB
    BollingerEJB bollingerEjb;
    @EJB
    RsiEJB rsiEjb;
    @EJB
    MovingAverageEJB maEjb;
    @EJB
    MacdEJB macdEjb;
    @EJB
    CciEJB cciEjb;
    
    private int candleSize = 5000;

    /**
     * Get one Candle by startDate
     *
     * @param startDate
     * @return
     */
    public CandleDTO get(Date startDate) {
        return this.configEjb.getCandleColl()
                .find(eq(STARTDATE, startDate))
                .first();
    }

    /**
     * Get one Candle by ID
     *
     * @param id
     * @return
     */
    public CandleDTO get(ObjectId id) {
        return configEjb.getCandleColl()
                .find(eq("_id", id))
                .first();
    }

    /**
     * Get Candles
     *
     * @param first
     * @param last
     * @return
     */
    public List<CandleDTO> get(Date first, Date last) {
        return configEjb.getCandleColl()
                .find(and(gte(STARTDATE, first), lte(STARTDATE, last)))
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());
    }

    /**
     * Get previous candle
     *
     * @param startDate
     * @return
     */
    public CandleDTO getPrev(Date startDate) {
        return this.configEjb.getCandleColl()
                .find(lt(STARTDATE, startDate))
                .sort(Sorts.descending(STARTDATE))
                .limit(1)
                .first();
    }

    /**
     * get last "limit" size candles
     *
     * @param limit
     * @return
     */
    public List<CandleDTO> getLasts(int limit) {
        return configEjb.getCandleColl()
                .find()
                .sort(Sorts.descending(STARTDATE))
                .limit(limit)
                .into(new ArrayList<>());
    }

    /**
     * Get latest date value from Candle collection
     *
     * @return
     */
    public Date getLatesDate() {
        CandleDTO dto = configEjb.getCandleColl()
                .find()
                .sort(Sorts.descending(STARTDATE))
                .first();
        
        if (dto == null) {
            return null;
        }
        return dto.getStartDate();
    }

    /**
     * Get first date from the candle collection
     *
     * @return
     */
    public Date getFirstDate() {
        CandleDTO dto = configEjb.getCandleColl()
                .find()
                .sort(Sorts.ascending(STARTDATE))
                .first();
        
        if (dto == null) {
            return null;
        }
        
        return dto.getStartDate();
    }

    /**
     * Get one day's all Candles
     *
     * @param startDate
     * @return
     */
    public List<CandleDTO> getOneDay(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date stopDate = cal.getTime();
        
        return configEjb.getCandleColl()
                .find(and(gte(STARTDATE, startDate), lt(STARTDATE, stopDate)))
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());
        
    }

    /**
     * Call candle generation methods
     */
    @Asynchronous
    public void callCandle() {
        configEjb.setRunCandle(false);
        this.setLastCandleCalcToFalse();
        this.calcDateList();
        this.calcCandle();
        this.deleteEmptyCandles();
        maEjb.calculateMovingAverage();
        bollingerEjb.calculateBollinger();
        rsiEjb.calculateRsi();
        macdEjb.calculateMacd();
        cciEjb.calculateCci();
        configEjb.setRunCandle(true);
    }

    /**
     * Delete empty candles open = 0
     */
    private void deleteEmptyCandles() {
        configEjb.getCandleColl()
                .deleteMany(
                        and(
                                eq("calcCandle", true),
                                eq("open", 0)
                        )
                );
    }

    /**
     * Set the last Candle calcCandle value to false (Incremental running)
     */
    private void setLastCandleCalcToFalse() {

        //Get last startDate from Candle
        CandleDTO candle = configEjb.getCandleColl()
                .find()
                .sort(Sorts.descending(STARTDATE))
                .first();
        if (candle == null || candle.getStartDate() == null) {
            return;
        }

        //Get last date from TradePair
        TradePairDTO trade = configEjb.getTradePairColl()
                .find()
                .sort(Sorts.descending(TIMEDATE))
                .first();
        if (trade == null) {
            return;
        }

        //If the tradePair is newer then Candle - Reset
        if (trade.getTimeDate().after(candle.getStartDate())) {
            candle.setCalcCandle(false);
            candle.getBollinger().setCalcBollinger(false);

            //Update last Candle
            configEjb.getCandleColl().replaceOne(
                    eq("_id", candle.getId()), candle);
        }
    }

    /**
     * Calculate candle values Max 10K rows (Prevent the timeout?)
     */
    private void calcCandle() {
        int i = 0;
        Date lastDate;
        
        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(eq("calcCandle", false))
                .sort(Sorts.ascending(STARTDATE))
                .limit(5000)
                .into(new ArrayList<>());
        
        for (CandleDTO dto : candleList) {
            this.calcCandleItem(dto);
            i++;
        }
        
        lastDate = candleList.get(candleList.size() - 1).getStartDate();
        this.candleSize = i;
        LOGGER.log(Level.INFO, "calcCandle: {0} {1}", new Object[]{i, lastDate});
    }

    /**
     * Calculate CandelItem values
     *
     * @param dto
     */
    private void calcCandleItem(CandleDTO dto) {
        TradePairDTO trade;
        
        FindIterable<TradePairDTO> result = configEjb.getTradePairColl()
                .find(and(gte(TIMEDATE, dto.getStartDate()), lt(TIMEDATE, dto.getStopDate())))
                .sort(Sorts.ascending(TIMEDATE));
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
        
        trade = result.sort(Sorts.ascending(TIMEDATE)).first();
        if (trade != null) {
            dto.setOpen(trade.getPrice());
        }
        
        trade = result.sort(Sorts.descending(TIMEDATE)).first();
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
            
            if ("b".equals(trade.getBuySel())) {
                countBuy++;
                totalBuy = totalBuy.add(trade.getTotal());
                volumeBuy = volumeBuy.add(trade.getVolume());
            } else if ("s".equals(trade.getBuySel())) {
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
        
        configEjb.getCandleColl().replaceOne(
                eq("_id", dto.getId()), dto);
    }

    /**
     * Calculate missing candle dates
     */
    private void calcDateList() {
        
        Calendar cal = Calendar.getInstance();
        Date startDate;
        try {
            startDate = this.getStartDate();
        } catch (MyException ex) {
            Logger.getLogger(CandleEJB.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        //Stopdate
        TradePairDTO dto = configEjb.getTradePairColl().find()
                .sort(Sorts.descending(TIMEDATE))
                .first();
        
        if (dto == null) {
            Logger.getLogger(CandleEJB.class.getName()).log(Level.SEVERE, null, "Missing: stopDate");
            return;
        }
        
        Date stopDate = dto.getTimeDate();

        //Store dates
        while (startDate.before(stopDate)) {
            LOGGER.log(Level.INFO, "calcDateList {0}-{1}", new Object[]{startDate, stopDate});
            
            configEjb.getCandleColl().insertOne(new CandleDTO(startDate));
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
    private Date getStartDate() throws MyException {
        Date startDate;
        
        if (configEjb.getCandleColl().countDocuments() > 0) {
            CandleDTO dto = configEjb.getCandleColl().find()
                    .sort(Sorts.descending(STARTDATE))
                    .first();
            
            if (dto == null) {
                throw new MyException("Missing: startDate");
            }
            
            startDate = dto.getStartDate();
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.MINUTE, 30);
            startDate = cal.getTime();
            
        } else {
            TradePairDTO dto = configEjb.getTradePairColl().find()
                    .sort(Sorts.ascending(TIMEDATE))
                    .first();
            
            if (dto == null) {
                throw new MyException("Missing: startDate");
            }
            
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
        return "CandleEJB{" + "config=" + configEjb + ", candleSize=" + candleSize + '}';
    }

    /**
     * Check Candle consistency I.Seek missing dates
     *
     * @return
     * @throws krakee.MyException
     */
    public ArrayList<String> chkDates() throws MyException {
        Calendar cal = Calendar.getInstance();
        ArrayList<String> list = new ArrayList<>();
        TradePairDTO dto;

        //get first trade date
        dto = configEjb.getTradePairColl()
                .find()
                .sort(Sorts.ascending(TIMEDATE))
                .first();
        
        if (dto == null) {
            throw new MyException("Missing: first trade");
        }
        
        Date firstDate = this.calcCandel30Min(dto.getTimeDate());

        //Get last trade date
        dto = configEjb.getTradePairColl()
                .find()
                .sort(Sorts.descending(TIMEDATE))
                .first();
        
        if (dto == null) {
            throw new MyException("Missing: last trade");
        }
        
        Date lastDate = dto.getTimeDate();
        
        int i = 0;
        while (firstDate.before(lastDate)) {
            i++;
            //Seek date
            CandleDTO candle = configEjb.getCandleColl()
                    .find(eq(STARTDATE, firstDate))
                    .first();
            if (candle == null) {
                list.add(firstDate.toString());
            }

            //calc next value
            cal.setTime(firstDate);
            cal.add(Calendar.MINUTE, 30);
            firstDate = cal.getTime();
        }
        
        LOGGER.log(Level.INFO, "chkCandleDates: {0}", i);
        return list;
    }

    /**
     * Check Candle consistency II. Count trades
     *
     * @return
     */
    public ArrayList<String> chkTradeCount() {
        ArrayList<String> list = new ArrayList<>();
        //Get candles
        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find()
                .into(new ArrayList<>());
        
        for (CandleDTO dto : candleList) {
            //Count trades    
            Document tradeDto = configEjb.getTradePairColl().aggregate(
                    Arrays.asList(
                            Aggregates.match(and(gte(TIMEDATE, dto.getStartDate()), lt(TIMEDATE, dto.getStopDate()))),
                            Aggregates.group("pair", Accumulators.sum("count", 1))
                    ), Document.class
            ).first();

            //Check
            if (tradeDto == null) {
                if (dto.getCount() != 0) {
                    list.add(dto.getStartDate().toString() + " 0, " + dto.getCount());
                }
            } else {
                Integer cnt = tradeDto.getInteger("count");
                if (cnt == null || !cnt.equals(dto.getCount())) {
                    list.add(dto.getStartDate().toString() + " " + cnt + "," + dto.getCount());
                }
            }
        }
        
        return list;
    }

    /**
     * Check Open=0 missing trades
     *
     * @return
     */
    public ArrayList<String> chkZeroOpen() {
        ArrayList<String> list = new ArrayList<>();
        
        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(eq("open", 0))
                .sort(Sorts.ascending(TIMEDATE))
                .into(new ArrayList<>());
        
        for (CandleDTO dto : candleList) {
            
            List<TradePairDTO> tradeList = configEjb.getTradePairColl()
                    .find(
                            and(
                                    gte(TIMEDATE, dto.getStartDate()),
                                    lt(TIMEDATE, dto.getStopDate())
                            )
                    )
                    .into(new ArrayList<>());
            
            if (tradeList.isEmpty()) {
                
                LOGGER.log(Level.INFO, "Missing trade: {0}-{1} {2}-{3}",
                        new Object[]{dto.getStartDate(), dto.getStopDate(),
                            dto.getStartDate().getTime(), dto.getStopDate().getTime()});
                
                list.add("Missing trade: " + dto.getStartDate() + "-" + dto.getStopDate()
                        + " " + dto.getStartDate().getTime() + "-" + dto.getStopDate().getTime());
            }
        }
        
        return list;
    }
    
}
