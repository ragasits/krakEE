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
package krakee.deep.input;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import deepnetts.data.TabularDataSet;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.Common;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.deep.DeepDTO;
import krakee.deep.DeepInputDTO;

/**
 * Transform Bollinger values from all candles
 *
 * @author rgt
 */
@Stateless
public class BollingerAllInputEJB extends AbstractInput {

    private final static short COUNT = 20;

    @EJB
    private CandleEJB candleEjb;
    @EJB
    private ConfigEJB configEjb;

    public ArrayList<Float> inputValueList(CandleDTO dto) {
        ArrayList<Float> outList = new ArrayList<>();

        ArrayList<CandleDTO> timeList = configEjb.getCandleColl()
                .find(
                        and(
                                lte("startDate", dto.getStartDate()),
                                gt("close", 0)
                        )
                )
                .sort(Sorts.descending("startDate"))
                .limit(COUNT)
                .into(new ArrayList<>());

        timeList.forEach(input -> {
            outList.add(input.getClose().floatValue());
        });

        return outList;
    }

    public ArrayList<Float> outputValueList(CandleDTO dto) {
        ArrayList<Float> outputList = new ArrayList<>();

        if (dto == null) {
            outputList.add(0f);
            outputList.add(0f);
        } else if (dto.getBollingerBuy()) {
            outputList.add(1f);
            outputList.add(0f);
        } else if (dto.getBollingerSell()) {
            outputList.add(0f);
            outputList.add(1f);
        } else {
            outputList.add(0f);
            outputList.add(0f);
        }

        return outputList;

    }

    @Override
    public TabularDataSet fillDataset(DeepDTO deep) {

        deep.setNumInputs(this.inputColumnNameList().size());
        deep.setNumOutputs(this.outputColumnNameList().size());
        TabularDataSet dataSet = new TabularDataSet(deep.getNumInputs(), deep.getNumOutputs());

        ArrayList<String> columns = new ArrayList<>();
        columns.addAll(this.inputColumnNameList());
        columns.addAll(this.outputColumnNameList());

        dataSet.setColumnNames(columns.toArray(new String[0]));
        deep.setColumnNames(columns);

        MongoCursor<CandleDTO> cursor = configEjb.getCandleColl()
                .find(gt("close",0))
                .sort(Sorts.ascending("startDate"))
                .iterator();
        
        while (cursor.hasNext()) {
            CandleDTO dto = cursor.next();

            ArrayList<Float> inputList = this.inputValueList(dto);
            ArrayList<Float> outputList = this.outputValueList(dto);

            if (inputList.size() == COUNT) {
                dataSet.add(new TabularDataSet.Item(
                        Common.convert(inputList),
                        Common.convert(outputList)));
            }
        }

        return dataSet;

    }

    @Override
    public ArrayList<String> inputColumnNameList() {
        ArrayList<String> cols = new ArrayList<>();

        for (int i = 0; i < COUNT; i++) {
            cols.add("close_" + i);
        }

        return cols;
    }

    @Override
    public ArrayList<Float> inputValueList(DeepInputDTO dto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
