/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.calc;

import java.math.BigDecimal;
import org.bson.Document;
import org.bson.types.Decimal128;

/**
 *
 * @author rgt
 */
public class BollingerDTO {
    
    private boolean calcBollinger;
    //Single Moving Average
    private BigDecimal sma;
    private BigDecimal deltaSma;

    public BollingerDTO() {
        this.calcBollinger= false;
        this.sma = BigDecimal.ZERO;
        this.deltaSma = BigDecimal.ZERO;
    }
       
    public BollingerDTO(Document doc){
        this.calcBollinger=doc.getBoolean("calcBollinger");
        this.sma=((Decimal128) doc.get("sma")).bigDecimalValue();
        this.deltaSma = ((Decimal128) doc.get("deltaSma")).bigDecimalValue();
    }
    
    public Document getBollinger(){
        return new Document("calcBollinger", this.calcBollinger)
                .append("sma", this.sma)
                .append("deltaSma", this.deltaSma);
    }
    
    public void calcDelta(BollingerDTO prev){
        this.deltaSma = this.sma.subtract(prev.getSma());
    }

    public boolean isCalcBollinger() {
        return calcBollinger;
    }

    public void setCalcBollinger(boolean calcBollinger) {
        this.calcBollinger = calcBollinger;
    }

    public BigDecimal getSma() {
        return sma;
    }

    public void setSma(BigDecimal sma) {
        this.sma = sma;
    }

    public BigDecimal getDeltaSma() {
        return deltaSma;
    }
            
            
    
    


    
    
    
    
}
