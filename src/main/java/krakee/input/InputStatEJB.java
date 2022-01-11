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
     * @param learnname
     * @param inputType
     * @param columnId
     */
    private void uniqueColumn(String learnname, String inputType, Integer columnId) {
        //get row number
        int rowNum = inputRowEjb.get(learnname, inputType).size();
        
        // Identify Columns That Contain a Single Value
        MongoCursor<Document> cursor = configEjb.getInputRowColl()
                .aggregate(
                        Arrays.asList(
                                //Basic filter
                                Aggregates.match(and(eq("learnName", learnname), eq("inputType", inputType))),
                                //Create new column form array
                                Aggregates.project(
                                        Projections.fields(
                                                Projections.excludeId(),
                                                Projections.computed(
                                                        "elem",
                                                        new Document("$arrayElemAt", Arrays.asList("$inputRow", columnId))
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
            Float percent = ((float) count / rowNum)*100;
            
            countList.add(new InputStatCountDTO(value, count, percent));
            uniqueCount++;
        }

        //Store result      
        InputStatDTO dto = this.get(learnname, inputType, columnId);
        //Consider Columns That Have Very Few Values
        float variance = ((float)uniqueCount/rowNum)*100;

        dto.setVariance(variance);
        dto.setValueCounts(countList);
        dto.setUniqueCount(uniqueCount);        

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
            this.uniqueColumn(learnname, inputType, dto.getColumnId());
        }

    }

}
