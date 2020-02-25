/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee;

import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * My common methods
 *
 * @author rgt
 */
public class Common {

    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    /**
     * Local BigDecimal.sqrt (Babylonian_method) Implemented only in the JAVA9
     *
     * @param A
     * @param SCALE
     * @return
     */
    public static BigDecimal sqrt(BigDecimal A, final int SCALE) {
        BigDecimal x0 = new BigDecimal("0");
        BigDecimal x1 = new BigDecimal(Math.sqrt(A.doubleValue()));
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, SCALE, ROUND_HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(TWO, SCALE, ROUND_HALF_UP);

        }
        return x1;
    }

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
     *
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
        if (last.compareTo(BigDecimal.ZERO) == 0 && prev.compareTo(BigDecimal.ZERO) == 0) {
            return 0;

        } else {
            switch (last.compareTo(prev)) {
                case 1:
                    return prevUp + 1;
                case 0:
                    return prevUp;
                case -1:
                    return 0;
            }
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
        if (last.compareTo(BigDecimal.ZERO) == 0 && prev.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        } else {
            switch (last.compareTo(prev)) {
                case 1:
                    return 0;
                case 0:
                    return prevDown;
                case -1:
                    return prevDown + 1;
            }
        }
        return null;
    }

}
