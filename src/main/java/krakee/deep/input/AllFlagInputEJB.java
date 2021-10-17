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

import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.deep.DeepInputDTO;

/**
 * Bollinger + RSI input
 *
 * @author rgt
 */
@Stateless
public class AllFlagInputEJB extends AbstractInput {

    private final static short COUNT = 20;

    @EJB
    private ConfigEJB config;

    @Override
    public ArrayList<Float> inputValueList(DeepInputDTO dto) {
        ArrayList<Float> outList = new ArrayList<>();

        ArrayList<CandleDTO> timeList = config.getCandleColl()
                .find(lte("startDate", dto.getCandle().getStartDate()))
                .sort(Sorts.descending("startDate"))
                .limit(COUNT)
                .into(new ArrayList<>());

        for (CandleDTO candle : timeList) {
            outList.add(candle.getBollinger().isBollingerBuy() ? 1f : 0f);
            outList.add(candle.getBollinger().isBollingerSell() ? 1f : 0f);
            outList.add(candle.getRsi().isRsiBuy() ? 1f : 0f);
            outList.add(candle.getRsi().isRsiSell() ? 1f : 0f);
            outList.add(candle.getMacd().isBearMarket() ? 1f : 0f);
            outList.add(candle.getMacd().isBullMarket() ? 1f : 0f);
            outList.add(candle.getMacd().isCrossover() ? 1f : 0f);
        }

        return outList;
    }

    @Override
    public ArrayList<String> inputColumnNameList() {
        ArrayList<String> cols = new ArrayList<>();

        for (int i = 0; i < COUNT; i++) {
            cols.add("bollingerBuy_" + i);
            cols.add("bollingerSell_" + i);
            cols.add("rsiBuy_" + i);
            cols.add("rsiSell_" + i);
            cols.add("macdBullMarket_" + i);
            cols.add("macdBearMarket_" + i);
            cols.add("macdCrossover_" + i);
        }

        return cols;
    }

}
