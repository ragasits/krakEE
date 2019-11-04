package krakee.get;

import java.math.BigDecimal;
import java.util.Date;
import org.bson.Document;
import org.bson.types.Decimal128;

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
     * Create object from Mongo Document
     * @param doc 
     */
    public TradePairDTO(Document doc) {
        this.price = ((Decimal128)doc.get("price")).bigDecimalValue();
        this.volume = ((Decimal128)doc.get("volume")).bigDecimalValue();
        this.time = ((Decimal128)doc.get("time")).bigDecimalValue();

        this.buySel = doc.getString("buySel");
        this.marketLimit = doc.getString("marketLimit");
        this.miscellaneous = doc.getString("miscellaneous");
        this.error = doc.getString("error");
        this.last = doc.getString("last");
        this.pair = doc.getString("pair");
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
     *
     * @return
     */
    public Document getTradepair() {
        return new Document("time", this.time)
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
    
    /**
     * calculate total value
     * @return 
     */
    public BigDecimal getTotal(){
        return this.price.multiply(this.volume);
    }

    public BigDecimal getTime() {
        return time;
    }

    public String getLast() {
        return last;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getBuySel() {
        return buySel;
    }
    
    
    
    
    
    
    
    
}
