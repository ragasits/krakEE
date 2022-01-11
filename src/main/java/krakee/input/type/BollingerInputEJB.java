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

import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.input.InputDTO;

/**
 * Transform Bollinger values to Dataset
 *
 * @author rgt
 */
@Stateless
public class BollingerInputEJB extends AbstractInput {

    private final static short COUNT = 20;

    @EJB
    private ConfigEJB config;

    @Override
    public ArrayList<Float> inputValueList(InputDTO dto) {
        ArrayList<Float> outList = new ArrayList<>();

        ArrayList<CandleDTO> timeList = config.getCandleColl()
                .find(lte("startDate", dto.getCandle().getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(COUNT)
                .into(new ArrayList<>());

        timeList.forEach(input -> {
            outList.add(input.getClose().floatValue());
        });

        return outList;
    }

    @Override
    public ArrayList<String> inputColumnNameList() {
        ArrayList<String> cols = new ArrayList<>();

        for (int i = 0; i < BollingerInputEJB.COUNT; i++) {
            cols.add("close_" + i);
        }

        return cols;
    }

    @Override
    public ArrayList<Float> outputValueList(InputDTO dto) {
        ArrayList<Float> outputList = new ArrayList<>();

        if (dto.getCandle() == null) {
            outputList.add(0f);
            outputList.add(0f);
        } else if (dto.getCandle().getBollinger().isBollingerBuy()) {
            outputList.add(1f);
            outputList.add(0f);
        } else if (dto.getCandle().getBollinger().isBollingerSell()) {
            outputList.add(0f);
            outputList.add(1f);
        } else {
            outputList.add(0f);
            outputList.add(0f);
        }

        return outputList;

    }

}
