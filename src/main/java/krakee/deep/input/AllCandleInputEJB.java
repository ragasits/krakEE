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

import java.util.ArrayList;
import java.util.Arrays;
import javax.ejb.Stateless;
import krakee.calc.BollingerDTO;
import krakee.calc.CandleDTO;
import krakee.deep.DeepInputDTO;

/**
 * Transform candle all values into DataSet
 *
 * @author rgt
 */
@Stateless
public class AllCandleInputEJB extends AbstractInput {

    /**
     * Convert Candle input values to ArrayList
     *
     * @param dto
     * @return
     */
    @Override
    public ArrayList<Float> inputValueList(DeepInputDTO dto) {
        CandleDTO candle = dto.getCandle();
        BollingerDTO boll = candle.getBollinger();

        return new ArrayList<>(Arrays.asList(
                candle.getCount().floatValue(),
                candle.getCountBuy().floatValue(),
                candle.getCountSell().floatValue(),
                candle.getOpen().floatValue(),
                candle.getLow().floatValue(),
                candle.getHigh().floatValue(),
                candle.getClose().floatValue(),
                candle.getTotal().floatValue(),
                candle.getTotalBuy().floatValue(),
                candle.getTotalSell().floatValue(),
                candle.getVolume().floatValue(),
                candle.getVolumeBuy().floatValue(),
                candle.getVolumeSell().floatValue(),
                boll.getSma().floatValue(),
                boll.getStDev().floatValue(),
                boll.getBollingerUpper().floatValue(),
                boll.getBollingerLower().floatValue(),
                boll.getBollingerBandWidth().floatValue(),
                boll.getTradeUpper().floatValue(),
                boll.getTradeLower().floatValue(),
                boll.getTradeUpper().floatValue()
        ));
    }

    /**
     * Get input column names
     *
     * @return
     */
    @Override
    public ArrayList<String> inputColumnNameList() {
        return new ArrayList<>(Arrays.asList(
                "count", "countBuy", "countSell", "open", "low", "high", "close",
                "total", "totalBuy", "totalSell", "volume", "volumeBuy", "volumeSell",
                "sma", "stDev", "bollingerUpper", "bollingerLower", "bollingerBandWith", 
                "tradeUpper", "tradeLower", "tradeUpper"
        ));
    }
}
