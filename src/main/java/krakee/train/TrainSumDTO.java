/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.train;

import java.math.BigDecimal;

/**
 *
 * @author rgt
 */
public class TrainSumDTO {
        
    private final Integer id;
    private BigDecimal euro;
    private BigDecimal btc;

    public TrainSumDTO(Integer id, BigDecimal euro) {
        this.id = id;
        this.euro = euro;
        this.btc = BigDecimal.ZERO;
    }

    public BigDecimal getEuro() {
        return euro;
    }

    public void setEuro(BigDecimal euro) {
        this.euro = euro;
    }

    public BigDecimal getBtc() {
        return btc;
    }

    public void setBtc(BigDecimal btc) {
        this.btc = btc;
    }

    
    
    
}
