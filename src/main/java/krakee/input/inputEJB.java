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
package krakee.input;

import krakee.input.InputDTO;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.MyException;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;

/**
 * Manage Deep Learning Inputs
 *
 * @author rgt
 */
@Stateless
public class InputEJB {

    @EJB
    LearnEJB learnEjb;
    @EJB
    CandleEJB candleEjb;
    @EJB
    ConfigEJB configEjb;

    /**
     * Get inputs filter by deepName
     *
     * @param learnName
     * @return
     */
    public ArrayList<InputDTO> getByLearnName(String learnName) {
        return configEjb.getInputColl()
                .find(eq("learnName", learnName))
                .sort(Sorts.ascending("candle.startDate"))
                .into(new ArrayList<>());
    }

    /**
     * Delete inputs by deepName
     *
     * @param learnName
     */
    public void delete(String learnName) {
        configEjb.getInputColl().deleteMany(eq("learnName", learnName));
    }

    /**
     * Fill Input row from learn
     *
     * @param learnName
     * @throws MyException
     */
    public void fillDeepInput(String learnName) throws MyException {
        if (learnName == null || learnName.isEmpty()) {
            throw new MyException("Missing: learnname");
        }

        //Delete old inputs
        this.delete(learnName);

        ArrayList<InputDTO> datasetList = new ArrayList<>();

        //Get Learning data
        LearnDTO firstLearn = learnEjb.getFirst(learnName);
        LearnDTO lastLearn = learnEjb.getLast(learnName);

        //Get Candles
        List<CandleDTO> candleList = candleEjb.get(firstLearn.getStartDate(), lastLearn.getStartDate());
        for (CandleDTO candleDTO : candleList) {
            LearnDTO learnDto = learnEjb.get(learnName, candleDTO.getStartDate());

            String trade = "";
            if (learnDto != null) {
                trade = learnDto.getTrade();
            }

            datasetList.add(new InputDTO(learnName, candleDTO, trade));

        }
        //Save inputs
        configEjb.getInputColl().insertMany(datasetList);
    }
}
