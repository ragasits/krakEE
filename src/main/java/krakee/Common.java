/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee;

import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_UP;
import java.util.ArrayList;

/**
 * My common methods
 *
 * @author rgt
 */
public class Common {

    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    /**
     * Convert ArrayList<Float> to float[]
     *
     * @param list
     * @return
     */
    public static float[] convert(ArrayList<Float> list) {
        float[] out = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            out[i] = list.get(i);
        }
        return out;
    }

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
}
