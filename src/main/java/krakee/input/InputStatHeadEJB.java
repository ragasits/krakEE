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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;

/**
 * Stats header
 *
 * @author rgt
 */
@Stateless
public class InputStatHeadEJB {

    @EJB
    ConfigEJB configEjb;
    @EJB
    InputStatEJB inputStatEjb;
    @EJB
    InputRowEJB inputRowEjb;

    /**
     * get header
     *
     * @param learnName
     * @param inputType
     * @return
     */
    public InputStatHeadDTO get(String learnName, String inputType) {
        return configEjb.getInputStatHeadColl()
                .find(
                        and(eq("learnName", learnName), eq("inputType", inputType))
                )
                .first();
    }

    /**
     * Delete old header
     *
     * @param learnName
     * @param inputType
     */
    private void delete(String learnName, String inputType) {
        configEjb.getInputStatHeadColl().deleteMany(
                and(eq("learnName", learnName), eq("inputType", inputType))
        );
    }

    /**
     * Delete one duplicate row
     *
     * @param learnName
     * @param inputType
     * @param row
     */
    public void deleteDuplicateRow(String learnName, String inputType, InputRowDTO row) {
        InputStatHeadDTO head = this.get(learnName, inputType);
        head.getInputRows().remove(row);
        configEjb.getInputStatHeadColl().replaceOne(eq("_id", head.getId()), head);
        inputRowEjb.deleteRow(row);
    }

    /**
     * Calculate Variance threshold
     *
     * @param learnname
     * @param inputType
     */
    private void calcVarianceThershold(String learnname, String inputType) {
        ArrayList<InputStatDTO> statList = inputStatEjb.get(learnname, inputType);
        ArrayList<ThresholdDTO> thresholdList = new ArrayList<>();

        for (int i = 0; i <= 50; i += 5) {
            int count = 0;

            for (InputStatDTO stat : statList) {
                if (stat.getVariance() > ((float) i / 100)) {
                    count++;
                }
            }

            thresholdList.add(new ThresholdDTO((float) i / 100, count));
        }

        InputStatHeadDTO head = new InputStatHeadDTO(learnname, inputType);
        head.setThresholds(thresholdList);
        configEjb.getInputStatHeadColl().insertOne(head);
    }

    /**
     * Identify Rows that Contain Duplicate Data
     *
     * @param learnname
     * @param inputType
     */
    private void findDuplicates(String learnname, String inputType) {
        ArrayList<InputRowDTO> rowList = inputRowEjb.getByInputRow(learnname, inputType);
        ArrayList<InputRowDTO> resultList = new ArrayList<>();
        InputRowDTO prevDto = null;

        for (InputRowDTO dto : rowList) {
            if (prevDto != null) {
                if (dto.getInputRow().toString().equals(prevDto.getInputRow().toString())) {
                    resultList.add(dto);
                }
            }

            prevDto = dto;
        }

        InputStatHeadDTO headDto = this.get(learnname, inputType);
        headDto.setInputRows(resultList);
        configEjb.getInputStatHeadColl().replaceOne(eq("_id", headDto.getId()), headDto);
    }

    /**
     * Analyze head
     *
     * @param learnname
     * @param inputType
     */
    public void analyzeHead(String learnname, String inputType) {
        this.delete(learnname, inputType);
        this.calcVarianceThershold(learnname, inputType);
        this.findDuplicates(learnname, inputType);
    }
}