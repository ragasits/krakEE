package krakee.calc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import org.bson.Document;
import org.bson.types.Decimal128;

/**
 * DTO for the Candle data
 *
 * @author rgt
 */
public class CandleDTO {
    private final Date startDate;
    private final Integer count;
    private final BigDecimal open;
    private final BigDecimal low;
    private final BigDecimal high;
    private final BigDecimal close;
    private final BigDecimal total;

    public CandleDTO(Date candleDate) {
        this.startDate = candleDate;
        this.count = 0;
        this.open = BigDecimal.ZERO;
        this.low = BigDecimal.ZERO;
        this.high = BigDecimal.ZERO;
        this.close = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    public CandleDTO(Document doc) {
        this.startDate = doc.getDate("date");
        this.count = doc.getInteger("count");
        this.open = ((Decimal128) doc.get("open")).bigDecimalValue();;
        this.low = ((Decimal128) doc.get("low")).bigDecimalValue();;
        this.high = ((Decimal128) doc.get("high")).bigDecimalValue();;
        this.close = ((Decimal128) doc.get("close")).bigDecimalValue();;
        this.total = ((Decimal128) doc.get("total")).bigDecimalValue();;
    }

    public Document getCandle() {
        return new Document("date", this.startDate)
                .append("count", this.count)
                .append("open", this.open)
                .append("low", this.low)
                .append("high", this.high)
                .append("close", this.close)
                .append("total", this.total);
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

    public Date getStartDate() {
        return startDate;
    }

    public Integer getCount() {
        return count;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getTotal() {
        return total;
    }

}
