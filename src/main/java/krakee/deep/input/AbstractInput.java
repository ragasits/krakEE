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
package krakee.deep.input;

import deepnetts.data.TabularDataSet;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ejb.EJB;
import krakee.Common;
import krakee.deep.DeepDTO;
import krakee.deep.DeepInputDTO;
import krakee.deep.DeepInputEJB;

/**
 * Transform candle all values into DataSet
 *
 * @author rgt
 */
public abstract class AbstractInput {

    @EJB
    DeepInputEJB inputEjb;

    /**
     * Get one input value
     *
     * @param inputList
     * @param rowIdx
     * @param colIdx
     * @return
     */
    public Float getInputValue(ArrayList<DeepInputDTO> inputList, Integer rowIdx, Integer colIdx) {

        DeepInputDTO dto = inputList.get(rowIdx);
        ArrayList<Float> row = new ArrayList<>();
        row.addAll(this.inputValueList(dto));
        row.addAll(this.outputValueList(dto));

        return row.get(colIdx);
    }

    /**
     * Create Output value list
     *
     * @param dto
     * @return
     */
    public ArrayList<Float> outputValueList(DeepInputDTO dto) {
        ArrayList<Float> outputList = new ArrayList<>();

        if (dto.getCandle() == null) {
            outputList.add(0f);
            outputList.add(0f);
        } else if (dto.getTrade().equals("buy")) {
            outputList.add(1f);
            outputList.add(0f);
        } else if (dto.getTrade().equals("sell")) {
            outputList.add(0f);
            outputList.add(1f);
        } else {
            outputList.add(0f);
            outputList.add(0f);
        }

        return outputList;

    }

    /**
     * Convert Candle input values to ArrayList
     *
     * @param dto
     * @return
     */
    public abstract ArrayList<Float> inputValueList(DeepInputDTO dto);

    /**
     * Create output column names list
     *
     * @return
     */
    public ArrayList<String> outputColumnNameList() {
        return new ArrayList<>(Arrays.asList("buy", "sell"));
    }

    /**
     * Get input column names
     *
     * @return
     */
    public abstract ArrayList<String> inputColumnNameList();

    /**
     * Create TabularDataSet
     *
     * @param deep
     * @return
     */
    public TabularDataSet fillDataset(DeepDTO deep) {

        deep.setNumInputs(this.inputColumnNameList().size());
        deep.setNumOutputs(this.outputColumnNameList().size());
        TabularDataSet dataSet = new TabularDataSet(deep.getNumInputs(), deep.getNumOutputs());

        ArrayList<String> columns = new ArrayList<>();
        columns.addAll(this.inputColumnNameList());
        columns.addAll(this.outputColumnNameList());

        dataSet.setColumnNames(columns.toArray(new String[0]));
        deep.setColumnNames(columns);

        ArrayList<DeepInputDTO> dtoList = inputEjb.get(deep.getDeepName());

        for (DeepInputDTO dto : dtoList) {

            ArrayList<Float> inputList = this.inputValueList(dto);
            ArrayList<Float> outputList = this.outputValueList(dto);

            dataSet.add(new TabularDataSet.Item(
                    Common.convert(inputList),
                    Common.convert(outputList)));          
        }

        return dataSet;

    }
}
