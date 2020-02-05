package krakee.calc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

/**
 * DTO for the Candle data
 *
 * @author rgt
 */
public class CandleDTO {

    private final ObjectId id;
    private final Date startDate;
    private Integer count;
    private Integer countBuy;
    private Integer countSell;
    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal high;
    private BigDecimal close;
    private BigDecimal total;
    private BigDecimal totalBuy;
    private BigDecimal totalSell;
    private BigDecimal volume;
    private BigDecimal volumeBuy;
    private BigDecimal volumeSell;
    private boolean calcCandle;

    private DeltaDTO delta;
    private BollingerDTO bollinger;

    public CandleDTO(Date candleDate) {
        this.id = null;
        this.startDate = (Date) candleDate.clone();
        this.count = 0;
        this.countBuy = 0;
        this.countSell = 0;
        this.open = BigDecimal.ZERO;
        this.low = BigDecimal.ZERO;
        this.high = BigDecimal.ZERO;
        this.close = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.totalBuy = BigDecimal.ZERO;
        this.totalSell = BigDecimal.ZERO;
        this.volume = BigDecimal.ZERO;
        this.volumeBuy = BigDecimal.ZERO;
        this.volumeSell = BigDecimal.ZERO;
        this.calcCandle = false;

        this.delta = new DeltaDTO();
        this.bollinger = new BollingerDTO();
    }

    public CandleDTO(Document doc) {
        this.id = doc.getObjectId("_id");
        this.startDate = doc.getDate("startDate");
        this.count = doc.getInteger("count");
        this.countBuy = doc.getInteger("countBuy");
        this.countSell = doc.getInteger("countSell");
        this.open = ((Decimal128) doc.get("open")).bigDecimalValue();
        this.low = ((Decimal128) doc.get("low")).bigDecimalValue();
        this.high = ((Decimal128) doc.get("high")).bigDecimalValue();
        this.close = ((Decimal128) doc.get("close")).bigDecimalValue();
        this.total = ((Decimal128) doc.get("total")).bigDecimalValue();
        this.totalBuy = ((Decimal128) doc.get("totalBuy")).bigDecimalValue();
        this.totalSell = ((Decimal128) doc.get("totalSell")).bigDecimalValue();
        this.volume = ((Decimal128) doc.get("volume")).bigDecimalValue();
        this.volumeBuy = ((Decimal128) doc.get("volumeBuy")).bigDecimalValue();
        this.volumeSell = ((Decimal128) doc.get("volumeSell")).bigDecimalValue();
        this.calcCandle = doc.getBoolean("calcCandle");

        this.delta = new DeltaDTO((Document) doc.get("delta"));
        this.bollinger = new BollingerDTO((Document) doc.get("bollinger"));
    }

    public Document getCandle() {
        return new Document("startDate", this.startDate)
                .append("count", this.count)
                .append("countBuy", this.countBuy)
                .append("countSell", this.countSell)
                .append("open", this.open)
                .append("low", this.low)
                .append("high", this.high)
                .append("close", this.close)
                .append("total", this.total)
                .append("totalBuy", this.totalBuy)
                .append("totalSell", this.totalSell)
                .append("volume", this.volume)
                .append("volumeBuy", this.volumeBuy)
                .append("volumeSell", this.volumeSell)
                .append("calcCandle", this.calcCandle)
                .append("delta", this.delta.getDelta())
                .append("bollinger", this.bollinger.getBollinger());
    }

    /**
     * Calculate stop Date candleDate + 30 minute
     *
     * @return
     */
    public Date getStopDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.startDate);
        cal.add(Calendar.MINUTE, 30);
        return cal.getTime();
    }

    public String getOHLCtMsg() {
        return "O:" + this.open + " H:" + this.high + " L:" + this.low + " C:" + this.close;
    }

    public String getIdHexa() {
        return this.id.toHexString();
    }

    public Date getStartDate() {
        //return startDate;
        return (Date) startDate.clone();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCountBuy() {
        return countBuy;
    }

    public void setCountBuy(Integer countBuy) {
        this.countBuy = countBuy;
    }

    public Integer getCountSell() {
        return countSell;
    }

    public void setCountSell(Integer countSell) {
        this.countSell = countSell;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalBuy() {
        return totalBuy;
    }

    public void setTotalBuy(BigDecimal totalBuy) {
        this.totalBuy = totalBuy;
    }

    public BigDecimal getTotalSell() {
        return totalSell;
    }

    public void setTotalSell(BigDecimal totalSell) {
        this.totalSell = totalSell;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public ObjectId getId() {
        return id;
    }

    public boolean isCalcCandle() {
        return calcCandle;
    }

    public void setCalcCandle(boolean calcCandle) {
        this.calcCandle = calcCandle;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getVolumeBuy() {
        return volumeBuy;
    }

    public void setVolumeBuy(BigDecimal volumeBuy) {
        this.volumeBuy = volumeBuy;
    }

    public BigDecimal getVolumeSell() {
        return volumeSell;
    }

    public void setVolumeSell(BigDecimal volumeSell) {
        this.volumeSell = volumeSell;
    }

    @Override
    public String toString() {
        return "CandleDTO{" + "id=" + id + ", startDate=" + startDate + ", count=" + count + ", countBuy=" + countBuy + ", countSell=" + countSell + ", open=" + open + ", low=" + low + ", high=" + high + ", close=" + close + ", total=" + total + ", totalBuy=" + totalBuy + ", totalSell=" + totalSell + ", volume=" + volume + ", volumeBuy=" + volumeBuy + ", volumeSell=" + volumeSell + ", calcCandle=" + calcCandle + '}';
    }

    public DeltaDTO getDelta() {
        return delta;
    }

    public void setDelta(DeltaDTO delta) {
        this.delta = delta;
    }

}
