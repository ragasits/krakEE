package krakee.get;

import java.util.Date;
import org.bson.Document;

/**
 * DTO for the Candle data
 * @author rgt
 */
public class CandleDTO {

    private final Date candleDate;

    public CandleDTO(Date candleDate) {
        this.candleDate = candleDate;
    }

    
    public Document getCandle() {
        return new Document("candleDate", this.candleDate);
    }
}
