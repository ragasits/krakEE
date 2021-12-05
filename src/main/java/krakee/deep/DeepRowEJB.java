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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import deepnetts.data.TabularDataSet;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.Common;
import krakee.ConfigEJB;
import krakee.MyException;
import krakee.deep.input.AbstractInput;
import krakee.deep.input.AllCandleInputEJB;
import krakee.deep.input.AllFlagInputEJB;
import krakee.deep.input.BollingerInputEJB;
import krakee.deep.input.IrisInputEJB;
import krakee.deep.input.TimeSeriesInputEJB;

/**
 *
 * @author rgt
 */
@Stateless
public class DeepRowEJB {

    @EJB
    ConfigEJB configEjb;
    @EJB
    DeepInputEJB deepInputEjb;
    @EJB
    private AllCandleInputEJB allCandleInputEjb;
    @EJB
    private TimeSeriesInputEJB timeSeriesInputEjb;
    @EJB
    private BollingerInputEJB bollingerInputEJB;
    @EJB
    private AllFlagInputEJB allFlagInputEJB;
    @EJB
    private IrisInputEJB irisInputEJB;

    /**
     * Get items from the deep row filter by learnName, inputType
     *
     * @param learnName
     * @param inputType
     * @return
     */
    public ArrayList<DeepRowDTO> get(String learnName, String inputType) {
        return configEjb.getDeepRowColl()
                .find(
                        and(eq("learnName", learnName), eq("inputType", inputType))
                )
                .into(new ArrayList<>());

    }
    
    /**
     * Get used input types to the DropBoxes
     *
     * @param learnName
     * @return
     */
    public ArrayList<String> getInputTypes(String learnName) {
        return configEjb.getDeepRowColl()
                .distinct("inputType", String.class)
                .into(new ArrayList<>());
    }

    /**
     * Get Column names from the current dataset
     * @param inputType
     * @return
     * @throws MyException 
     */
    public ArrayList<String> getColumnNames(String inputType) throws MyException {
        
        if (inputType == null || inputType.isEmpty()){
            return null;
        }
        
        InputType type = InputType.valueOf(inputType);
        ArrayList<String> cols = this.selectDatasetEjb(type).inputColumnNameList();
        cols.addAll(this.selectDatasetEjb(type).outputColumnNameList());
        
        return cols;
    }

    /**
     * Delete rows by learnName, inputType
     *
     * @param learnName
     * @param inputType
     */
    private void delete(String learnName, String inputType) {
        configEjb.getDeepRowColl().deleteMany(
                and(eq("learnName", learnName), eq("inputType", inputType))
        );
    }

    /**
     * Select used input EJB
     *
     * @param type
     * @return
     * @throws MyException
     */
    public AbstractInput selectDatasetEjb(InputType type) throws MyException {

        switch (type) {
            case AllCandle:
                return allCandleInputEjb;
            case TimeSeries:
                return timeSeriesInputEjb;
            case Bollinger:
                return bollingerInputEJB;
            case AllFlag:
                return allFlagInputEJB;
            case Iris:
                return irisInputEJB;
            default:
                throw new MyException("Intenal error: Wrong InputType");
        }
    }

    /**
     * Fill rows from the learns
     *
     * @param learnName
     * @param inputType
     * @throws MyException
     */
    private void fillRowFromLearn(String learnName, String inputType) throws MyException {
        this.delete(learnName, inputType);
        ArrayList<DeepInputDTO> inputList = deepInputEjb.getByLearnName(learnName);
        AbstractInput dataset = this.selectDatasetEjb(InputType.valueOf(inputType));

        for (DeepInputDTO dto : inputList) {
            ArrayList<Float> inputRow = dataset.inputValueList(dto);
            ArrayList<Float> outputRow = dataset.outputValueList(dto);

            DeepRowDTO row = new DeepRowDTO(learnName, inputType, inputRow, outputRow);
            configEjb.getDeepRowColl().insertOne(row);
        }
    }

    /**
     * Fill rows from the Iris data
     *
     * @param learnName
     * @param inputType
     * @throws MyException
     */
    private void fillRowFromIris(String learnName, String inputType) throws MyException {
        this.delete(learnName, inputType);

        float[][] irisArr = IrisInputEJB.getIrisArr();
        for (float[] irisRow : irisArr) {

            ArrayList<Float> inputRow = new ArrayList<>();
            inputRow.add(irisRow[0]);
            inputRow.add(irisRow[1]);
            inputRow.add(irisRow[2]);
            inputRow.add(irisRow[3]);

            ArrayList<Float> outputRow = new ArrayList<>();
            outputRow.add(irisRow[4]);
            outputRow.add(irisRow[5]);
            outputRow.add(irisRow[6]);

            DeepRowDTO row = new DeepRowDTO(learnName, inputType, inputRow, outputRow);
            configEjb.getDeepRowColl().insertOne(row);

        }
    }

    /**
     * Fill row elements
     *
     * @param learnName
     * @param inputType
     * @throws MyException
     */
    public void fillRow(String learnName, String inputType) throws MyException {
        this.delete(learnName, inputType);

        if ((InputType.Iris.equals(InputType.valueOf(inputType)))) {
            this.fillRowFromIris(learnName, inputType);
        } else {
            this.fillRowFromLearn(learnName, inputType);
        }
    }

    /**
     * Add element to TabularDataSet
     *
     * @param dataSet
     * @param inputList
     * @param outputList
     */
    private void addDataset(TabularDataSet dataSet, ArrayList<Float> inputList, ArrayList<Float> outputList) {
        dataSet.add(new TabularDataSet.Item(
                Common.convert(inputList),
                Common.convert(outputList)));
    }

    /**
     * Create TabularDataSet from deepRows
     *
     * @param learnName
     * @param inputType
     * @return
     * @throws MyException
     */
    public TabularDataSet fillDataset(String learnName, String inputType) throws MyException {
        ArrayList<DeepRowDTO> rowList = this.get(learnName, inputType);

        int numInputs = rowList.get(0).getInputRow().size();
        int numOutputs = rowList.get(0).getOutputRow().size();
        TabularDataSet dataSet = new TabularDataSet(numInputs, numOutputs);

        ArrayList<String> columns = new ArrayList<>();
        var input = this.selectDatasetEjb(InputType.valueOf(inputType));
        columns.addAll(input.inputColumnNameList());
        columns.addAll(input.outputColumnNameList());

        dataSet.setColumnNames(columns.toArray(new String[0]));

        for (DeepRowDTO row : rowList) {
            addDataset(dataSet, row.getInputRow(), row.getOutputRow());
        }

        return dataSet;
    }
}
