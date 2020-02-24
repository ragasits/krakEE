/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import java.math.BigDecimal;
import java.util.Date;
import krakee.calc.CandleDTO;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

/**
 *
 * @author rgt
 */
public class ProfitDTO {

    private ObjectId id;
    private Date startDate;
    private String trade;
    private BigDecimal close;
    private BigDecimal eur;
    private BigDecimal btc;
    
    public ProfitDTO(CandleDTO candle, String trade) {
        this.startDate = candle.getStartDate();
        this.close = candle.getClose();
        this.trade = trade;
        this.eur = BigDecimal.ZERO;
        this.btc = BigDecimal.ZERO;
        
    }

    public ProfitDTO(Document doc) {
        this.id = doc.getObjectId("_id");
        this.startDate = doc.getDate("startDate");
        this.trade = doc.getString("trade");
        this.close = ((Decimal128) doc.get("close")).bigDecimalValue();
        this.eur = ((Decimal128) doc.get("eur")).bigDecimalValue();
        this.btc = ((Decimal128) doc.get("btc")).bigDecimalValue();
    }

    public Document getProfit() {
        return new Document("startDate", this.startDate)
                .append("trade", this.trade)
                .append("close", this.close)
                .append("eur", this.eur)
                .append("btc", this.btc);
    }
    
    private void buyBtc(ProfitDTO prev){
        if (prev.getEur().compareTo(BigDecimal.ZERO)==1){
            this.btc = prev.getEur().divide(prev.getClose());
            this.eur = BigDecimal.ZERO;
        }
    }

    private void sellBtc(ProfitDTO prev){
        if (prev.getBtc().compareTo(BigDecimal.ZERO)==1){
            this.eur = prev.getBtc().multiply(prev.getClose());
            this.btc = BigDecimal.ZERO;
        }
    }    

    public ObjectId getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getEur() {
        return eur;
    }

    public BigDecimal getBtc() {
        return btc;
    }
    
    
}
