package krakee.get;

import java.math.BigDecimal;
import java.util.Date;
import org.bson.Document;

/**
 * DTO for Kraken trade pairs
 *
 * @author rgt
 */
public class TradePairDTO {

    private final BigDecimal price;
    private final BigDecimal volume;
    private final BigDecimal time;
    private final String buySel;
    private final String marketLimit;
    private final String miscellaneous;

    private final String error;
    private final String last;
    private final String pair;

    public TradePairDTO(BigDecimal price, BigDecimal volume, BigDecimal time, String buySel, String marketLimit, String miscellaneous, String error, String last, String pair) {
        this.price = price;
        this.volume = volume;
        this.time = time;
        this.buySel = buySel;
        this.marketLimit = marketLimit;
        this.miscellaneous = miscellaneous;
        this.error = error;
        this.last = last;
        this.pair = pair;
    }

    /**
     * Convert Time value to Date
     *
     * @return
     */
    public Date getTimeDate() {
        Date date = new Date();
        long milis = time.multiply(BigDecimal.valueOf(1000)).longValue();
        date.setTime(milis);
        return date;
    }

    /**
     * Convert Last value to Date
     *
     * @return
     */
    public Date getLastDate() {
        Date date = new Date();
        date.setTime(Long.valueOf(this.last.substring(0, 13)));
        return date;
    }

    /**
     * Create Mongo document
     * @return 
     */
    public Document  getTradepair(){
        return new Document ("time", this.time)
        .append("pair", this.pair)
        .append("timeDate", this.getTimeDate())
        .append("price", this.price)
        .append("volume", this.volume)
        .append("buySel", this.buySel)
        .append("marketLimit", this.marketLimit)
        .append("miscellaneous", this.miscellaneous)
        .append("error", this.error)
        .append("last", this.last)
        .append("lastDate", this.getLastDate());
    }

    public BigDecimal getTime() {
        return time;
    }
}
