/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import krakee.calc.CandleDTO;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

/**
 * DTO for profits
 * @author rgt
 */
public class ProfitDTO {

    // Random values
    static final String BUY = "buy";
    static final String SELL = "sell";
    static final String NONE = "none";
    static final String[] OP = {BUY, SELL, NONE};

    private ObjectId id;
    private Date startDate;
    private String trade;
    private BigDecimal close;
    private BigDecimal eur;
    private BigDecimal btc;

    public ProfitDTO(BigDecimal eur) {
        this.eur = eur;
        this.close = BigDecimal.ZERO;
        this.btc = BigDecimal.ZERO;
    }

    public ProfitDTO(CandleDTO candle, String trade) {
        this.startDate = candle.getStartDate();
        this.close = candle.getClose();
        this.trade = trade;
        this.eur = BigDecimal.ZERO;
        this.btc = BigDecimal.ZERO;

    }

    /**
     * Create DTO from Document
     * @param doc 
     */
    public ProfitDTO(Document doc) {
        this.id = doc.getObjectId("_id");
        this.startDate = doc.getDate("startDate");
        this.trade = doc.getString("trade");
        this.close = ((Decimal128) doc.get("close")).bigDecimalValue();
        this.eur = ((Decimal128) doc.get("eur")).bigDecimalValue();
        this.btc = ((Decimal128) doc.get("btc")).bigDecimalValue();
    }

    /**
     * Create Document from DTO
     * @return 
     */
    public Document getProfit() {
        return new Document("startDate", this.startDate)
                .append("trade", this.trade)
                .append("close", this.close)
                .append("eur", this.eur)
                .append("btc", this.btc);
    }

    /**
     * Buy BTC
     * @param eur 
     */
    public void buyBtc(BigDecimal eur) {
        this.btc = eur.divide(this.close, RoundingMode.HALF_UP);
        this.eur = BigDecimal.ZERO;
    }

    /**
     * Sell BTC
     * @param btc 
     */
    public void sellBtc(BigDecimal btc) {
        this.eur = btc.multiply(this.close);
        this.btc = BigDecimal.ZERO;
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

    public String getTrade() {
        return trade;
    }

}
