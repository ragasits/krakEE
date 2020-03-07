/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * We're storing the best profits
 *
 * @author rgt
 */
public class ProfitBestDTO {
        
    private ObjectId id;
    private final Long testNum;
    private final double eur;

    public ProfitBestDTO(Long testNum, double eur) {
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
        this.eur = doc.getDouble("eur");
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

    public double getEur() {
        return eur;
    }
    
    

}
