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
import java.util.Iterator;
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
     * Add item to Dataset
     *
     * @param dataSet
     * @param inputList
     * @param outputList
     */
    public void addDataset(TabularDataSet dataSet, ArrayList<Float> inputList, ArrayList<Float> outputList) {
        dataSet.add(new TabularDataSet.Item(
                Common.convert(inputList),
                Common.convert(outputList)));
    }

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
        ArrayList<String> uniqueList = new ArrayList<>();

        int cntBuy = deep.getInputBuyLimit();
        int cntSell = deep.getInputSellLimit();
        int cntNone = deep.getInputNoneLimit();
        boolean isLimit = true;

        Iterator iterator = dtoList.iterator();
        //for (DeepInputDTO dto : dtoList) {
        while (iterator.hasNext() && isLimit) {
            DeepInputDTO dto = (DeepInputDTO) iterator.next();

            ArrayList<Float> inputList = this.inputValueList(dto);
            ArrayList<Float> outputList = this.outputValueList(dto);
            int count = 0;

            if (deep.isDeleteDuplications()) {
                //ignore duplicates

                for (String s : uniqueList) {
                    if (s.equals(inputList.toString())) {
                        count++;
                        break;
                    }
                }

                if (count == 0) {
                    uniqueList.add(inputList.toString());

                    //Use limits
                    if (deep.isInputLimits()) {
                        if (outputList.get(0) == 1f && cntBuy > 0) {
                            addDataset(dataSet, inputList, outputList);
                            cntBuy--;
                        } else if (outputList.get(1) == 1f && cntSell > 0) {
                            addDataset(dataSet, inputList, outputList);
                            cntSell--;
                        } else if (outputList.get(0) == 0f && outputList.get(1) == 0f && cntNone > 0) {
                            addDataset(dataSet, inputList, outputList);
                            cntNone--;
                        }

                        isLimit = !((cntBuy + cntSell + cntNone) == 0);

                    } else {
                        addDataset(dataSet, inputList, outputList);
                    }
                }

            } else {
                //Add Duplicates
                //Use limits
                if (deep.isInputLimits()) {
                    if (outputList.get(0) == 1f && cntBuy > 0) {
                        addDataset(dataSet, inputList, outputList);
                        cntBuy--;
                    } else if (outputList.get(1) == 1f && cntSell > 0) {
                        addDataset(dataSet, inputList, outputList);
                        cntSell--;
                    } else if (outputList.get(0) == 0f && outputList.get(1) == 0f && cntNone > 0) {
                        addDataset(dataSet, inputList, outputList);
                        cntNone--;
                    }

                    isLimit = !((cntBuy + cntSell + cntNone) == 0);

                } else {
                    addDataset(dataSet, inputList, outputList);
                }
            }

        }

        return dataSet;
    }

}
