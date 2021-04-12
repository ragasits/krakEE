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
package krakee.deep;

import deepnetts.net.layers.activation.ActivationType;
import java.util.Comparator;
import org.bson.codecs.pojo.annotations.BsonIgnore;

/**
 * Layer parameters
 *
 * @author rgt
 */
public class DeepLayerDTO {

    short order;
    int widths;
    String activationType = ActivationType.TANH.toString();

    /**
     * Order by field "Order"
     * @return 
     */
    @BsonIgnore
    public static Comparator<DeepLayerDTO> getCompByOrder() {
        Comparator comp = new Comparator<DeepLayerDTO>() {

            @Override
            public int compare(DeepLayerDTO d1, DeepLayerDTO d2) {
                Short s1 = d1.order;
                Short s2 = d2.order;

                return s1.compareTo(s2);
            }
        };
        return comp;
    }

    @BsonIgnore
    public ActivationType[] getActivationTypes() {
        return ActivationType.values();
    }

    public short getOrder() {
        return order;
    }

    public void setOrder(short order) {
        this.order = order;
    }

    public int getWidths() {
        return widths;
    }

    public void setWidths(int widths) {
        this.widths = widths;
    }

    public String getActivationType() {
        return activationType;
    }

    public void setActivationType(String activationType) {
        this.activationType = activationType;
    }

}
