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
     * Analyze head
     *
     * @param learnname
     * @param inputType
     */
    public void analyzeHead(String learnname, String inputType) {
        this.delete(learnname, inputType);
        this.calcVarianceThershold(learnname, inputType);
    }
}
