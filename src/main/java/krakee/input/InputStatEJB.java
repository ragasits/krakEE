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
package krakee.input;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import org.bson.Document;

/**
 * Create stats
 *
 * @author rgt
 */
@Stateless
public class InputStatEJB {

    @EJB
    ConfigEJB configEjb;
    @EJB
    InputRowEJB inputRowEjb;

    /**
     * get stats
     *
     * @param learnName
     * @param inputType
     * @return
     */
    public ArrayList<InputStatDTO> get(String learnName, String inputType) {
        return configEjb.getInputStatColl()
                .find(
                        and(eq("learnName", learnName), eq("inputType", inputType))
                )
                .into(new ArrayList<>());
    }

    /**
     * Get one stat
     *
     * @param learnName
     * @param inputType
     * @param columnId
     * @return
     */
    public InputStatDTO get(String learnName, String inputType, Integer columnId) {
        return configEjb.getInputStatColl()
                .find(
                        and(eq("learnName", learnName), eq("inputType", inputType), eq("columnId", columnId))
                )
                .first();
    }

    /**
     * Delete stats
     *
     * @param learnName
     * @param inputType
     */
    private void delete(String learnName, String inputType) {
        configEjb.getInputStatColl().deleteMany(
                and(eq("learnName", learnName), eq("inputType", inputType))
        );
    }

    /**
     * Delete column from Inputstat, InputRowEJB
     *
     * @param learnName
     * @param inputType
     * @param columnId
     */
    public void deleteColumn(String learnName, String inputType, Integer columnId) {
        //Delete from stat
        InputStatDTO stat = this.get(learnName, inputType, columnId);
        configEjb.getInputStatColl().deleteOne(eq("_id", stat.getId()));

        //Delete from row
        inputRowEjb.deleteColumn(learnName, inputType, columnId);
    }

    /**
     * Fill collection with columns as rows
     *
     * @param learnname
     * @param inputType
     */
    public void fillColumns(String learnname, String inputType) {
        this.delete(learnname, inputType);

        InputRowDTO firstRow = inputRowEjb.getFirst(learnname, inputType);
        ArrayList<String> columnList = firstRow.getInputColumnNames();

        ArrayList<InputStatDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < columnList.size(); i++) {
            InputStatDTO dto = new InputStatDTO(learnname, inputType, i, columnList.get(i));
            dtoList.add(dto);
        }

        configEjb.getInputStatColl().insertMany(dtoList);
    }

    /**
     * Create stat from the unique values
     *
     * @param dto
     */
    private void calcUniqueColumn(InputStatDTO dto) {
        //get row number
        int rowNum = inputRowEjb.get(dto.getLearnName(), dto.getInputType()).size();

        // Identify Columns That Contain a Single Value
        MongoCursor<Document> cursor = configEjb.getInputRowColl()
                .aggregate(
                        Arrays.asList(
                                //Basic filter
                                Aggregates.match(and(eq("learnName", dto.getLearnName()), eq("inputType", dto.getInputType()))),
                                //Create new column form array
                                Aggregates.project(
                                        Projections.fields(
                                                Projections.excludeId(),
                                                Projections.computed(
                                                        "elem",
                                                        new Document("$arrayElemAt", Arrays.asList("$inputRow", dto.getColumnId()))
                                                )
                                        )
                                ),
                                //Group By
                                Aggregates.group("$elem", Accumulators.sum("count", 1)),
                                //Sort
                                Aggregates.sort(Sorts.descending("count"))
                        ), Document.class
                )
                .allowDiskUse(Boolean.TRUE)
                .iterator();

        //Read result
        ArrayList<InputStatCountDTO> countList = new ArrayList<>();
        int uniqueCount = 0;

        while (cursor.hasNext()) {
            Document doc = cursor.next();

            Float value = doc.getDouble("_id").floatValue();
            Integer count = doc.getInteger("count");
            Float percent = ((float) count / rowNum) * 100;

            countList.add(new InputStatCountDTO(value, count, percent));
            uniqueCount++;
        }

        //Store result      
        dto.setValueCounts(countList);
        dto.setUniqueCount(uniqueCount);

        //Identify Columns That Contain a Single Value
        if (dto.getUniqueCount().equals(1)) {
            dto.getResultList().add("Column contains a Single Value");
        }

        //Consider Columns That Have Very Few Values
        float uPercent = ((float) uniqueCount / rowNum) * 100;
        dto.setUniquePercent(uPercent);

        if (uPercent < 1) {
            dto.getResultList().add("Column have very few values");
        }

        configEjb.getInputStatColl().replaceOne(eq("_id", dto.getId()), dto);
    }

    /**
     * Calculate AVG, variance
     *
     * @param dto
     */
    private void calcVariance(InputStatDTO dto) {
        ArrayList<InputRowDTO> rowList = inputRowEjb.get(dto.getLearnName(), dto.getInputType());

        //Calc AVG
        int count = 0;
        float sum = 0;
        for (InputRowDTO row : rowList) {
            float value = row.getInputRow().get(dto.getColumnId());
            sum = sum + value;
            count++;
        }

        float avg = sum / count;
        float var = 0;

        dto.setValueAvg(avg);

        //Calc Variance
        for (InputRowDTO row : rowList) {
            float value = row.getInputRow().get(dto.getColumnId());

            var = var + (float) Math.pow(value - avg, 2);
        }
        var = var / count;
        dto.setVariance(var);

        if (var < 0.16f) {
            dto.getResultList().add("Variance less than 80%(0.16)");
        }

        configEjb.getInputStatColl().replaceOne(eq("_id", dto.getId()), dto);
    }

    /**
     * Looking for outliners - Std
     *
     * @param dto
     */
    private void calcOutliners(InputStatDTO dto) {
        ArrayList<InputRowDTO> inputList = this.inputRowEjb.get(dto.getLearnName(), dto.getInputType());
        double mean = 0d;
        double stdev = 0d;

        //calculate summary statistics
        //Mean - Average
        for (InputRowDTO input : inputList) {
            mean = mean + input.getInputRow().get(dto.getColumnId());

        }
        mean = mean / inputList.size();

        //Stdev
        for (InputRowDTO input : inputList) {
            // stdev = stdev.add(candle.getClose().subtract(dto.getMovingAverage().getSma20()).pow(2));            
            double x = input.getInputRow().get(dto.getColumnId()).doubleValue();
            stdev = stdev + Math.pow(x - mean, 2);
        }
        stdev = stdev / inputList.size();
        stdev = Math.sqrt(stdev);

        //define outliers
        double cutOff = stdev * 3;
        double lower = mean - cutOff;
        double upper = mean + cutOff;

        //identify outliers        
        int outliers = 0;
        StringBuilder sb = new StringBuilder();
        for (InputRowDTO input : inputList) {
            float value = input.getInputRow().get(dto.getColumnId());
            if (value < lower || value > upper) {
                outliers++;
                sb.append(value).append(";");
            }
        }

        dto.setMean((float) mean);
        dto.setStd((float) stdev);
        dto.setCutOff((float) cutOff);
        dto.setLower((float) lower);
        dto.setUpper((float) upper);
        dto.setOutliers(sb.toString());
        
        if (outliers>0){
            dto.getResultList().add("Identified outliers");
        }
        configEjb.getInputStatColl().replaceOne(eq("_id", dto.getId()), dto);
    }

    /**
     * Analyze all columns
     *
     * @param learnname
     * @param inputType
     */
    public void analyzeColumns(String learnname, String inputType) {
        ArrayList<InputStatDTO> colList = this.get(learnname, inputType);
        for (InputStatDTO dto : colList) {
            this.calcUniqueColumn(dto);
            this.calcVariance(dto);
            this.calcOutliners(dto);

        }
    }
}
