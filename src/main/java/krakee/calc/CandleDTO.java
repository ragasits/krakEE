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
    private final Date candleDate;
    private final Integer candleCount;
    private final BigDecimal candleOpen;
    private final BigDecimal candleLow;
    private final BigDecimal candleHigh;
    private final BigDecimal candleClose;
    private final BigDecimal candleTotal;

    public CandleDTO(Date candleDate) {
        this.candleDate = candleDate;
        this.candleCount = 0;
        this.candleOpen = BigDecimal.ZERO;
        this.candleLow = BigDecimal.ZERO;
        this.candleHigh = BigDecimal.ZERO;
        this.candleClose = BigDecimal.ZERO;
        this.candleTotal = BigDecimal.ZERO;
    }

    public CandleDTO(Document doc) {
        this.candleDate = doc.getDate("candleDate");
        this.candleCount = doc.getInteger("candleCount");
        this.candleOpen = ((Decimal128) doc.get("candleOpen")).bigDecimalValue();;
        this.candleLow = ((Decimal128) doc.get("candleLow")).bigDecimalValue();;
        this.candleHigh = ((Decimal128) doc.get("candleHigh")).bigDecimalValue();;
        this.candleClose = ((Decimal128) doc.get("candleClose")).bigDecimalValue();;
        this.candleTotal = ((Decimal128) doc.get("candleTotal")).bigDecimalValue();;
    }

    public Document getCandle() {
        return new Document("candleDate", this.candleDate)
                .append("candleCount", this.candleCount)
                .append("candleOpen", this.candleOpen)
                .append("candleLow", this.candleLow)
                .append("candleHigh", this.candleHigh)
                .append("candleClose", this.candleClose)
                .append("candleTotal", this.candleTotal);
    }

    /**
     * Calculate stop Date candleDate + 30 minute
     *
     * @return
     */
    public Date getCandleStopDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.candleDate);
        cal.add(Calendar.MINUTE, 30);
        return cal.getTime();
    }

    public Date getCandleDate() {
        return candleDate;
    }

    public Integer getCandleCount() {
        return candleCount;
    }

    public BigDecimal getCandleOpen() {
        return candleOpen;
    }

    public BigDecimal getCandleLow() {
        return candleLow;
    }

    public BigDecimal getCandleHigh() {
        return candleHigh;
    }

    public BigDecimal getCandleClose() {
        return candleClose;
    }

    public BigDecimal getCandleTotal() {
        return candleTotal;
    }

}
