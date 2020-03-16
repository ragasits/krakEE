/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import java.util.Date;
import krakee.calc.CandleDTO;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * DTO for profits
 *
 * @author rgt
 */
public class ProfitDTO {

    //Kranken Fees
    private static final double MAKER = 0.16;
    private static final double TAKER = 0.26;

    // Random values
    static final String BUY = "buy";
    static final String SELL = "sell";
    static final String NONE = "none";
    static final String[] OP = {BUY,NONE, SELL, NONE};

    private ObjectId id;
    private final Long testNum;
    private final Date startDate;
    private final String trade;
    private final double close;
    private double eur;
    private double btc;
    private double fee;

    public ProfitDTO(CandleDTO candle, String trade, Long testNum) {
        this.startDate = candle.getStartDate();
        this.close = candle.getClose().doubleValue();
        this.trade = trade;
        this.eur = 0;
        this.btc = 0;
        this.fee = 0;
        this.testNum = testNum;

    }

    /**
     * Create DTO from Document
     *
     * @param doc
     */
    public ProfitDTO(Document doc) {
        this.id = doc.getObjectId("_id");
        this.testNum = doc.getLong("testNum");
        this.startDate = doc.getDate("startDate");
        this.trade = doc.getString("trade");
        this.close = doc.getDouble("close");
        this.eur = doc.getDouble("eur");
        this.btc = doc.getDouble("btc");
        this.fee = doc.getDouble("fee");
    }

    /**
     * Create Document from DTO
     *
     * @return
     */
    public Document getProfit() {
        return new Document("startDate", this.startDate)
                .append("testNum", this.testNum)
                .append("trade", this.trade)
                .append("close", this.close)
                .append("eur", this.eur)
                .append("btc", this.btc)
                .append("fee", this.fee);
    }

    /**
     * Buy BTC
     *
     * @param eur
     */
    public void buyBtc(double eur) {
        //Calculate Kraken Marker Fee
        this.fee = (eur / 100) * MAKER;
        this.btc = (eur - this.fee) / this.close;
        this.eur = 0;
    }

    /**
     * Sell BTC
     *
     *
     * @param btc
     */
    public void sellBtc(double btc) {
        this.eur = (btc * this.close);

        //Calculate Kraken Taker Fee
        this.fee = (eur / 100) * TAKER;
        this.eur = this.eur - this.fee;

        this.btc = 0;
    }

    public ObjectId getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public double getClose() {
        return close;
    }

    public double getEur() {
        return eur;
    }

    public double getBtc() {
        return btc;
    }

    public String getTrade() {
        return trade;
    }

    public Long getTestNum() {
        return testNum;
    }

    public double getFee() {
        return fee;
    }
}
