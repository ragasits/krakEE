/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import java.math.BigDecimal;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

/**
 * We're storing the best profits
 *
 * @author rgt
 */
public class ProfitBestDTO {
        
    private ObjectId id;
    private final Long testNum;
    private final BigDecimal eur;

    public ProfitBestDTO(Long testNum, BigDecimal eur) {
        this.testNum = testNum;
        this.eur = eur;
    }

    /**
     * Create DTO from document
     * @param doc 
     */
    public ProfitBestDTO(Document doc) {
        this.id = doc.getObjectId("_id");
        this.testNum = doc.getLong("testNum");
        this.eur = ((Decimal128) doc.get("eur")).bigDecimalValue();
    }
    
    /**
     * Create Document from DTO
     * @return 
     */
    public Document getProfitBest() {
        return new Document("testNum", this.testNum)
                .append("eur", this.eur);
    }    

    public Long getTestNum() {
        return testNum;
    }

    public BigDecimal getEur() {
        return eur;
    }
    
    

}
