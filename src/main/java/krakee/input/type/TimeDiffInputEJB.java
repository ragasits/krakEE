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

import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.input.InputDTO;

/**
 * Transform candle trade values into DataSet
 *
 * @author rgt
 */
@Stateless
public class TimeDiffInputEJB extends AbstractInput {

    private final static short COUNT = 7;

    @EJB
    private ConfigEJB config;

    /**
     * Convert Candle input values to ArrayList
     *
     * @param dto
     * @return
     */
    @Override
    public ArrayList<Float> inputValueList(InputDTO dto) {

        ArrayList<Float> outList = new ArrayList<>();

        ArrayList<CandleDTO> timeList = config.getCandleColl()
                .find(lte("startDate", dto.getCandle().getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(COUNT)
                .into(new ArrayList<>());

        for (CandleDTO input : timeList) {
            outList.add(input.getClose().subtract(dto.getCandle().getClose()).floatValue());
        }

        return outList;
    }

    /**
     * Get input column names
     *
     * @return
     */
    @Override
    public ArrayList<String> inputColumnNameList() {
        ArrayList<String> cols = new ArrayList<>();

        for (int i = 0; i < TimeDiffInputEJB.COUNT; i++) {
            cols.add("close_" + i);
        }

        return cols;
    }
    
    @Override
    public ArrayList<Float> outputValueList(InputDTO dto) {
        ArrayList<Float> outputList = new ArrayList<>();

        if (dto.getCandle() == null) {
            outputList.add(0f);

        } else if (dto.getTrade().equals("buy")) {
            outputList.add(1f);
        } else if (dto.getTrade().equals("sell")) {
            outputList.add(2f);
        } else {
            outputList.add(0f);
        }

        return outputList;

    }    

    @Override
    public ArrayList<String> outputColumnNameList() {
        return new ArrayList<>(Arrays.asList("trade"));
    }
}
