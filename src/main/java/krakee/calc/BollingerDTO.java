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

    public BollingerDTO() {
        this.calcBollinger= false;
        this.sma = BigDecimal.ZERO;
    }
       
    public BollingerDTO(Document doc){
        this.calcBollinger=doc.getBoolean("calcBollinger");
        this.sma=((Decimal128) doc.get("sma")).bigDecimalValue();
    }
    
    public Document getBollinger(){
        return new Document("calcBollinger", this.calcBollinger)
                .append("sma", this.sma);
    }
            
            
    
    


    
    
    
    
}