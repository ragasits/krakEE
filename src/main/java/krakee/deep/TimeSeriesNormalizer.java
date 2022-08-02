/*
 * Copyright (C) 2020 rgt
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
package krakee.deep;

import deepnetts.data.MLDataItem;
import java.io.Serializable;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.data.preprocessing.Scaler;

/**
 * Own TimeSeries Normalizer for Deep Nets
 *
 * @author rgt
 */
public final class TimeSeriesNormalizer implements Scaler<DataSet<MLDataItem>>, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Row level normalization
     *
     * @param dataSet
     */
    @Override
    public void apply(DataSet<MLDataItem> dataSet) {
        float epsilon = 1.01f;

        for (MLDataItem row : dataSet) {
            float rowMin = 99999999999.0f;
            float rowMax = -9999999999.0f;

            for (int i = 0; i < row.getInput().size(); i++) {
                if (row.getInput().get(i) > rowMax) {
                    rowMax = row.getInput().get(i);
                }
                if (row.getInput().get(i) < rowMin) {
                    rowMin = row.getInput().get(i);
                }
            }

            rowMax = rowMax * epsilon;
            rowMin = rowMin / epsilon;

            for (int i = 0; i < row.getInput().size(); i++) {
                float value = (row.getInput().get(i) - rowMin) / (rowMax - rowMin);
                row.getInput().set(i, value);
            }
        }
    }
}
