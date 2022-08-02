/*
 * Copyright (C) 2021 rgt
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
package krakee.input.type;

import java.util.ArrayList;
import java.util.Arrays;
import javax.ejb.Stateless;
import krakee.input.InputDTO;

/**
 * Iris reference data
 *
 * @author rgt
 */
@Stateless
public class IrisInputEJB extends AbstractInput {

    private final static float[][] irisArr = {
        {5.1f, 3.5f, 1.4f, 0.2f, 1f, 0f, 0f}, {4.9f, 3.0f, 1.4f, 0.2f, 1f, 0f, 0f},
        {4.7f, 3.2f, 1.3f, 0.2f, 1f, 0f, 0f}, {4.6f, 3.1f, 1.5f, 0.2f, 1f, 0f, 0f},
        {5.0f, 3.6f, 1.4f, 0.2f, 1f, 0f, 0f}, {5.4f, 3.9f, 1.7f, 0.4f, 1f, 0f, 0f},
        {4.6f, 3.4f, 1.4f, 0.3f, 1f, 0f, 0f}, {5.0f, 3.4f, 1.5f, 0.2f, 1f, 0f, 0f},
        {4.4f, 2.9f, 1.4f, 0.2f, 1f, 0f, 0f}, {4.9f, 3.1f, 1.5f, 0.1f, 1f, 0f, 0f},
        {5.4f, 3.7f, 1.5f, 0.2f, 1f, 0f, 0f}, {4.8f, 3.4f, 1.6f, 0.2f, 1f, 0f, 0f},
        {4.8f, 3.0f, 1.4f, 0.1f, 1f, 0f, 0f}, {4.3f, 3.0f, 1.1f, 0.1f, 1f, 0f, 0f},
        {5.8f, 4.0f, 1.2f, 0.2f, 1f, 0f, 0f}, {5.7f, 4.4f, 1.5f, 0.4f, 1f, 0f, 0f},
        {5.4f, 3.9f, 1.3f, 0.4f, 1f, 0f, 0f}, {5.1f, 3.5f, 1.4f, 0.3f, 1f, 0f, 0f},
        {5.7f, 3.8f, 1.7f, 0.3f, 1f, 0f, 0f}, {5.1f, 3.8f, 1.5f, 0.3f, 1f, 0f, 0f},
        {5.4f, 3.4f, 1.7f, 0.2f, 1f, 0f, 0f}, {5.1f, 3.7f, 1.5f, 0.4f, 1f, 0f, 0f},
        {4.6f, 3.6f, 1.0f, 0.2f, 1f, 0f, 0f}, {5.1f, 3.3f, 1.7f, 0.5f, 1f, 0f, 0f},
        {4.8f, 3.4f, 1.9f, 0.2f, 1f, 0f, 0f}, {5.0f, 3.0f, 1.6f, 0.2f, 1f, 0f, 0f},
        {5.0f, 3.4f, 1.6f, 0.4f, 1f, 0f, 0f}, {5.2f, 3.5f, 1.5f, 0.2f, 1f, 0f, 0f},
        {5.2f, 3.4f, 1.4f, 0.2f, 1f, 0f, 0f}, {4.7f, 3.2f, 1.6f, 0.2f, 1f, 0f, 0f},
        {4.8f, 3.1f, 1.6f, 0.2f, 1f, 0f, 0f}, {5.4f, 3.4f, 1.5f, 0.4f, 1f, 0f, 0f},
        {5.2f, 4.1f, 1.5f, 0.1f, 1f, 0f, 0f}, {5.5f, 4.2f, 1.4f, 0.2f, 1f, 0f, 0f},
        {4.9f, 3.1f, 1.5f, 0.1f, 1f, 0f, 0f}, {5.0f, 3.2f, 1.2f, 0.2f, 1f, 0f, 0f},
        {5.5f, 3.5f, 1.3f, 0.2f, 1f, 0f, 0f}, {4.9f, 3.1f, 1.5f, 0.1f, 1f, 0f, 0f},
        {4.4f, 3.0f, 1.3f, 0.2f, 1f, 0f, 0f}, {5.1f, 3.4f, 1.5f, 0.2f, 1f, 0f, 0f},
        {5.0f, 3.5f, 1.3f, 0.3f, 1f, 0f, 0f}, {4.5f, 2.3f, 1.3f, 0.3f, 1f, 0f, 0f},
        {4.4f, 3.2f, 1.3f, 0.2f, 1f, 0f, 0f}, {5.0f, 3.5f, 1.6f, 0.6f, 1f, 0f, 0f},
        {5.1f, 3.8f, 1.9f, 0.4f, 1f, 0f, 0f}, {4.8f, 3.0f, 1.4f, 0.3f, 1f, 0f, 0f},
        {5.1f, 3.8f, 1.6f, 0.2f, 1f, 0f, 0f}, {4.6f, 3.2f, 1.4f, 0.2f, 1f, 0f, 0f},
        {5.3f, 3.7f, 1.5f, 0.2f, 1f, 0f, 0f}, {5.0f, 3.3f, 1.4f, 0.2f, 1f, 0f, 0f},
        {7.0f, 3.2f, 4.7f, 1.4f, 0f, 1f, 0f}, {6.4f, 3.2f, 4.5f, 1.5f, 0f, 1f, 0f},
        {6.9f, 3.1f, 4.9f, 1.5f, 0f, 1f, 0f}, {5.5f, 2.3f, 4.0f, 1.3f, 0f, 1f, 0f},
        {6.5f, 2.8f, 4.6f, 1.5f, 0f, 1f, 0f}, {5.7f, 2.8f, 4.5f, 1.3f, 0f, 1f, 0f},
        {6.3f, 3.3f, 4.7f, 1.6f, 0f, 1f, 0f}, {4.9f, 2.4f, 3.3f, 1.0f, 0f, 1f, 0f},
        {6.6f, 2.9f, 4.6f, 1.3f, 0f, 1f, 0f}, {5.2f, 2.7f, 3.9f, 1.4f, 0f, 1f, 0f},
        {5.0f, 2.0f, 3.5f, 1.0f, 0f, 1f, 0f}, {5.9f, 3.0f, 4.2f, 1.5f, 0f, 1f, 0f},
        {6.0f, 2.2f, 4.0f, 1.0f, 0f, 1f, 0f}, {6.1f, 2.9f, 4.7f, 1.4f, 0f, 1f, 0f},
        {5.6f, 2.9f, 3.6f, 1.3f, 0f, 1f, 0f}, {6.7f, 3.1f, 4.4f, 1.4f, 0f, 1f, 0f},
        {5.6f, 3.0f, 4.5f, 1.5f, 0f, 1f, 0f}, {5.8f, 2.7f, 4.1f, 1.0f, 0f, 1f, 0f},
        {6.2f, 2.2f, 4.5f, 1.5f, 0f, 1f, 0f}, {5.6f, 2.5f, 3.9f, 1.1f, 0f, 1f, 0f},
        {5.9f, 3.2f, 4.8f, 1.8f, 0f, 1f, 0f}, {6.1f, 2.8f, 4.0f, 1.3f, 0f, 1f, 0f},
        {6.3f, 2.5f, 4.9f, 1.5f, 0f, 1f, 0f}, {6.1f, 2.8f, 4.7f, 1.2f, 0f, 1f, 0f},
        {6.4f, 2.9f, 4.3f, 1.3f, 0f, 1f, 0f}, {6.6f, 3.0f, 4.4f, 1.4f, 0f, 1f, 0f},
        {6.8f, 2.8f, 4.8f, 1.4f, 0f, 1f, 0f}, {6.7f, 3.0f, 5.0f, 1.7f, 0f, 1f, 0f},
        {6.0f, 2.9f, 4.5f, 1.5f, 0f, 1f, 0f}, {5.7f, 2.6f, 3.5f, 1.0f, 0f, 1f, 0f},
        {5.5f, 2.4f, 3.8f, 1.1f, 0f, 1f, 0f}, {5.5f, 2.4f, 3.7f, 1.0f, 0f, 1f, 0f},
        {5.8f, 2.7f, 3.9f, 1.2f, 0f, 1f, 0f}, {6.0f, 2.7f, 5.1f, 1.6f, 0f, 1f, 0f},
        {5.4f, 3.0f, 4.5f, 1.5f, 0f, 1f, 0f}, {6.0f, 3.4f, 4.5f, 1.6f, 0f, 1f, 0f},
        {6.7f, 3.1f, 4.7f, 1.5f, 0f, 1f, 0f}, {6.3f, 2.3f, 4.4f, 1.3f, 0f, 1f, 0f},
        {5.6f, 3.0f, 4.1f, 1.3f, 0f, 1f, 0f}, {5.5f, 2.5f, 4.0f, 1.3f, 0f, 1f, 0f},
        {5.5f, 2.6f, 4.4f, 1.2f, 0f, 1f, 0f}, {6.1f, 3.0f, 4.6f, 1.4f, 0f, 1f, 0f},
        {5.8f, 2.6f, 4.0f, 1.2f, 0f, 1f, 0f}, {5.0f, 2.3f, 3.3f, 1.0f, 0f, 1f, 0f},
        {5.6f, 2.7f, 4.2f, 1.3f, 0f, 1f, 0f}, {5.7f, 3.0f, 4.2f, 1.2f, 0f, 1f, 0f},
        {5.7f, 2.9f, 4.2f, 1.3f, 0f, 1f, 0f}, {6.2f, 2.9f, 4.3f, 1.3f, 0f, 1f, 0f},
        {5.1f, 2.5f, 3.0f, 1.1f, 0f, 1f, 0f}, {5.7f, 2.8f, 4.1f, 1.3f, 0f, 1f, 0f},
        {6.3f, 3.3f, 6.0f, 2.5f, 0f, 0f, 1f}, {5.8f, 2.7f, 5.1f, 1.9f, 0f, 0f, 1f},
        {7.1f, 3.0f, 5.9f, 2.1f, 0f, 0f, 1f}, {6.3f, 2.9f, 5.6f, 1.8f, 0f, 0f, 1f},
        {6.5f, 3.0f, 5.8f, 2.2f, 0f, 0f, 1f}, {7.6f, 3.0f, 6.6f, 2.1f, 0f, 0f, 1f},
        {4.9f, 2.5f, 4.5f, 1.7f, 0f, 0f, 1f}, {7.3f, 2.9f, 6.3f, 1.8f, 0f, 0f, 1f},
        {6.7f, 2.5f, 5.8f, 1.8f, 0f, 0f, 1f}, {7.2f, 3.6f, 6.1f, 2.5f, 0f, 0f, 1f},
        {6.5f, 3.2f, 5.1f, 2.0f, 0f, 0f, 1f}, {6.4f, 2.7f, 5.3f, 1.9f, 0f, 0f, 1f},
        {6.8f, 3.0f, 5.5f, 2.1f, 0f, 0f, 1f}, {5.7f, 2.5f, 5.0f, 2.0f, 0f, 0f, 1f},
        {5.8f, 2.8f, 5.1f, 2.4f, 0f, 0f, 1f}, {6.4f, 3.2f, 5.3f, 2.3f, 0f, 0f, 1f},
        {6.5f, 3.0f, 5.5f, 1.8f, 0f, 0f, 1f}, {7.7f, 3.8f, 6.7f, 2.2f, 0f, 0f, 1f},
        {7.7f, 2.6f, 6.9f, 2.3f, 0f, 0f, 1f}, {6.0f, 2.2f, 5.0f, 1.5f, 0f, 0f, 1f},
        {6.9f, 3.2f, 5.7f, 2.3f, 0f, 0f, 1f}, {5.6f, 2.8f, 4.9f, 2.0f, 0f, 0f, 1f},
        {7.7f, 2.8f, 6.7f, 2.0f, 0f, 0f, 1f}, {6.3f, 2.7f, 4.9f, 1.8f, 0f, 0f, 1f},
        {6.7f, 3.3f, 5.7f, 2.1f, 0f, 0f, 1f}, {7.2f, 3.2f, 6.0f, 1.8f, 0f, 0f, 1f},
        {6.2f, 2.8f, 4.8f, 1.8f, 0f, 0f, 1f}, {6.1f, 3.0f, 4.9f, 1.8f, 0f, 0f, 1f},
        {6.4f, 2.8f, 5.6f, 2.1f, 0f, 0f, 1f}, {7.2f, 3.0f, 5.8f, 1.6f, 0f, 0f, 1f},
        {7.4f, 2.8f, 6.1f, 1.9f, 0f, 0f, 1f}, {7.9f, 3.8f, 6.4f, 2.0f, 0f, 0f, 1f},
        {6.4f, 2.8f, 5.6f, 2.2f, 0f, 0f, 1f}, {6.3f, 2.8f, 5.1f, 1.5f, 0f, 0f, 1f},
        {6.1f, 2.6f, 5.6f, 1.4f, 0f, 0f, 1f}, {7.7f, 3.0f, 6.1f, 2.3f, 0f, 0f, 1f},
        {6.3f, 3.4f, 5.6f, 2.4f, 0f, 0f, 1f}, {6.4f, 3.1f, 5.5f, 1.8f, 0f, 0f, 1f},
        {6.0f, 3.0f, 4.8f, 1.8f, 0f, 0f, 1f}, {6.9f, 3.1f, 5.4f, 2.1f, 0f, 0f, 1f},
        {6.7f, 3.1f, 5.6f, 2.4f, 0f, 0f, 1f}, {6.9f, 3.1f, 5.1f, 2.3f, 0f, 0f, 1f},
        {5.8f, 2.7f, 5.1f, 1.9f, 0f, 0f, 1f}, {6.8f, 3.2f, 5.9f, 2.3f, 0f, 0f, 1f},
        {6.7f, 3.3f, 5.7f, 2.5f, 0f, 0f, 1f}, {6.7f, 3.0f, 5.2f, 2.3f, 0f, 0f, 1f},
        {6.3f, 2.5f, 5.0f, 1.9f, 0f, 0f, 1f}, {6.5f, 3.0f, 5.2f, 2.0f, 0f, 0f, 1f},
        {6.2f, 3.4f, 5.4f, 2.3f, 0f, 0f, 1f}, {5.9f, 3.0f, 5.1f, 1.8f, 0f, 0f, 1f}
    };

    @Override
    public ArrayList<String> inputColumnNameList() {
        return new ArrayList<>(Arrays.asList("sepal_length", "sepal_width", "petal_length", "petal_width"));

    }

    @Override
    public ArrayList<String> outputColumnNameList() {
        return new ArrayList<>(Arrays.asList("setosa", "versicolor", "virginica"));
    }

    @Override
    public ArrayList<Float> inputValueList(InputDTO dto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Iris Data
     *
     * @return
     */
    public static float[][] getIrisArr() {
        return irisArr;
    }
}
