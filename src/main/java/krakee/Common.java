/*
 * Copyright (C) 2021 Ragasits Csaba
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package krakee;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        //BigDecimal x1 = BigDecimal.valueOf(Math.sqrt(A.doubleValue()));
        BigDecimal x1 = new BigDecimal(Math.sqrt(A.doubleValue()));
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, SCALE, RoundingMode.HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(TWO, SCALE, RoundingMode.HALF_UP);

        }
        return x1;
    }
}
