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
package krakee.deep;

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
import krakee.deep.input.AllCandleEJB;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;

/**
 * Manage Deep Learning Inputs
 *
 * @author rgt
 */
@Stateless
public class DeepInputEJB {

    @EJB
    LearnEJB learnEjb;
    @EJB
    CandleEJB candleEjb;
    @EJB
    ConfigEJB configEjb;
    @EJB
    AllCandleEJB allCandleEjb;

    /**
     * Convert Values to CSV format
     *
     * @param deep
     * @return
     */
    public ArrayList<String> inOutValuesToCsv(DeepDTO deep) {
        ArrayList<String> rowList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        //Header
        ArrayList<String> columns = deep.getColumnNames();
        for (String column : columns) {
            if (sb.length() != 0) {
                sb.append(";");
            }
            sb.append(column);
        }
        rowList.add(sb.toString());

        //Rows
        ArrayList<DeepInputDTO> inputList = this.get(deep.getDeepName());
        for (DeepInputDTO dto : inputList) {
            sb = new StringBuilder();

            ArrayList<Float> cols = new ArrayList<>();
            cols.addAll(allCandleEjb.inputValueList(dto));
            cols.addAll(allCandleEjb.outputValueList(dto));

            for (Float col : cols) {
                sb.append(";").append(col);
            }

            rowList.add(sb.toString().replaceFirst(";", ""));
        }
        return rowList;
    }

    /**
     * Get inputs filter by deepName
     *
     * @param deepName
     * @return
     */
    public ArrayList<DeepInputDTO> get(String deepName) {
        return configEjb.getDeepInputColl()
                .find(eq("deepName", deepName))
                .sort(Sorts.ascending("candle.startDate"))
                .into(new ArrayList<>());
    }

    /**
     * Create Dataset from the Learn data and store it in the Deep Input
     * collection
     *
     * @param deep
     * @throws MyException
     */
    public void fillDataset(DeepDTO deep) throws MyException {
        if (deep == null || deep.getLearnName() == null || deep.getLearnName().isEmpty()) {
            throw new MyException("Missing: learnname");
        }
        if (deep == null || deep.getDeepName() == null || deep.getDeepName().isEmpty()) {
            throw new MyException("Missing: deepname");
        }

        //Delete old inputs
        this.delete(deep);

        ArrayList<DeepInputDTO> datasetList = new ArrayList<>();

        //Get Learning data
        LearnDTO firstLearn = learnEjb.getFirst(deep.getLearnName());
        LearnDTO lastLearn = learnEjb.getLast(deep.getLearnName());

        //Get Candles
        List<CandleDTO> candleList = candleEjb.get(firstLearn.getStartDate(), lastLearn.getStartDate());
        for (CandleDTO candleDTO : candleList) {
            LearnDTO learnDto = learnEjb.get(deep.getLearnName(), candleDTO.getStartDate());

            String trade = "";
            if (learnDto != null) {
                trade = learnDto.getTrade();
            }

            datasetList.add(new DeepInputDTO(deep.getDeepName(), candleDTO, trade));

        }

        //Save inputs
        configEjb.getDeepInputColl().insertMany(datasetList);
    }

    /**
     * Delete inputs by deepName
     *
     * @param deep
     */
    public void delete(DeepDTO deep) {
        configEjb.getDeepInputColl().deleteMany(eq("deepName", deep.getDeepName()));
    }
}
