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
package krakee.input.type;

import java.util.ArrayList;
import java.util.Arrays;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.input.InputDTO;

/**
 * Transform candle all values into DataSet
 *
 * @author rgt
 */
@Stateless
public class OilSpillEJB extends AbstractInput {

    @EJB
    ConfigEJB configEjb;

    /**
     * get all documents
     * @return 
     */
    public ArrayList<OilSpillDTO> get() {
        return configEjb.getOilSpillColl()
                .find()
                .into(new ArrayList<>());
    }

    /**
     * Convert Candle input values to ArrayList
     *
     * @param dto
     * @return
     */
    @Override
    public ArrayList<Float> inputValueList(InputDTO dto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Get input column names
     *
     * @return
     */
    @Override
    public ArrayList<String> inputColumnNameList() {
        return new ArrayList<>(Arrays.asList(
                "f00", "f01", "f02", "f03", "f04", "f05", "f06", "f07", "f08", "f09", "f10",
                "f11", "f12", "f13", "f14", "f15", "f16", "f17", "f18", "f19", "f20", "f21",
                "f22", "f23", "f24", "f25", "f26", "f27", "f28", "f29", "f30", "f31", "f32",
                "f33", "f34", "f35", "f36", "f37", "f38", "f39", "f40", "f41", "f42", "f43",
                "f44", "f45", "f46", "f47", "f48", "f49"
        ));
    }
}
