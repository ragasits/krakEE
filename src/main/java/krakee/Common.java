/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee;

import java.math.BigDecimal;

/**
 *
 * @author rgt
 */
public class Common {
    
 /**
     * Calculate Trend Up value (Integer)
     *
     * @param last
     * @param prev
     * @param prevUp
     * @return
     */
    public static Integer calcTrendUp(Integer last, Integer prev, Integer prevUp) {
        return Common.calcTrendUp(BigDecimal.valueOf(last), BigDecimal.valueOf(prev), prevUp);
    }

    
    /**
     * Calculate Trend Down value (Integer)
     * @param last
     * @param prev
     * @param prevUp
     * @return 
     */
    public static Integer calcTrendDown(Integer last, Integer prev, Integer prevUp) {
        return Common.calcTrendDown(BigDecimal.valueOf(last), BigDecimal.valueOf(prev), prevUp);
    }

    /**
     * Calculate Trend up value (BigDecima)
     *
     * @param last
     * @param prev
     * @param prevUp
     * @return
     */
    public static Integer calcTrendUp(BigDecimal last, BigDecimal prev, Integer prevUp) {
        switch (last.compareTo(prev)) {
            case 1:
                return prevUp + 1;
            case 0:
                return prevUp;
            case -1:
                return 0;
        }
        return null;
    }

    /**
     * Calculate Trend down value (BigDecimal)
     *
     * @param last
     * @param prev
     * @param prevDown
     * @return
     */
    public static Integer calcTrendDown(BigDecimal last, BigDecimal prev, Integer prevDown) {
        switch (last.compareTo(prev)) {
            case 1:
                return 0;
            case 0:
                return prevDown;
            case -1:
                return prevDown + 1;
        }
        return null;
    }
    
    
}
