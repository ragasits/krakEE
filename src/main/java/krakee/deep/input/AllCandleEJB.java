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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.Common;
import krakee.calc.BollingerDTO;
import krakee.calc.CalendarDTO;
import krakee.calc.CandleDTO;
import krakee.calc.DeltaDTO;
import krakee.deep.DeepDTO;
import krakee.deep.DeepInputDTO;
import krakee.deep.DeepInputEJB;

/**
 * Transform candle all values into DataSet
 *
 * @author rgt
 */
@Stateless
public class AllCandleEJB {

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
     * @return
     */
    public ArrayList<Float> inputValueList(DeepInputDTO dto) {
        CandleDTO candle = dto.getCandle();
        DeltaDTO delta = candle.getDelta();
        BollingerDTO boll = candle.getBollinger();
        CalendarDTO cal = candle.getCalendar();

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
                delta.getDeltaCount().floatValue(),
                delta.getDeltaCountBuy().floatValue(),
                delta.getDeltaCountSell().floatValue(),
                delta.getDeltaOpen().floatValue(),
                delta.getDeltaLow().floatValue(),
                delta.getDeltaHigh().floatValue(),
                delta.getDeltaClose().floatValue(),
                delta.getDeltaTotal().floatValue(),
                delta.getDeltaTotalBuy().floatValue(),
                delta.getDeltaTotalSell().floatValue(),
                delta.getDeltaVolume().floatValue(),
                delta.getDeltaVolumeBuy().floatValue(),
                delta.getDeltaVolumeSell().floatValue(),
                delta.getTrendOpenUp().floatValue(),
                delta.getTrendOpenDown().floatValue(),
                delta.getTrendHighUp().floatValue(),
                delta.getTrendHighDown().floatValue(),
                delta.getTrendLowUp().floatValue(),
                delta.getTrendLowDown().floatValue(),
                delta.getTrendCloseUp().floatValue(),
                delta.getTrendCloseDown().floatValue(),
                delta.getTrendTotalUp().floatValue(),
                delta.getTrendTotalBuyUp().floatValue(),
                delta.getTrendTotalSellUp().floatValue(),
                delta.getTrendVolumeUp().floatValue(),
                delta.getTrendVolumeBuyUp().floatValue(),
                delta.getTrendVolumeSellUp().floatValue(),
                delta.getTrendTotalDown().floatValue(),
                delta.getTrendTotalBuyDown().floatValue(),
                delta.getTrendTotalSellDown().floatValue(),
                delta.getTrendVolumeDown().floatValue(),
                delta.getTrendVolumeBuyDown().floatValue(),
                delta.getTrendVolumeSellDown().floatValue(),
                delta.getTrendCountUp().floatValue(),
                delta.getTrendCountBuyUp().floatValue(),
                delta.getTrendCountSellUp().floatValue(),
                delta.getTrendCountDown().floatValue(),
                delta.getTrendCountBuyDown().floatValue(),
                delta.getTrendCountSellDown().floatValue(),
                boll.getSma().floatValue(),
                boll.getDeltaSma().floatValue(),
                boll.getTrendSmaUp().floatValue(),
                boll.getTrendSmaDown().floatValue(),
                boll.getStDev().floatValue(),
                boll.getDeltaStDev().floatValue(),
                boll.getTrendStDevUp().floatValue(),
                boll.getTrendStDevDown().floatValue(),
                boll.getBollingerUpper().floatValue(),
                boll.getDeltaBollingerUpper().floatValue(),
                boll.getTrendBollingerUpperUp().floatValue(),
                boll.getTrendBollingerUpperDown().floatValue(),
                boll.getBollingerLower().floatValue(),
                boll.getDeltaBollingerLower().floatValue(),
                boll.getTrendBollingerLowerUp().floatValue(),
                boll.getTrendBollingerLowerDown().floatValue(),
                boll.getBollingerBandWith().floatValue(),
                boll.getDeltaBollingerBandWith().floatValue(),
                boll.getTrendBollingerBandWithUp().floatValue(),
                boll.getTrendBollingerBandWithDown().floatValue(),
                boll.getTradeUpper().floatValue(),
                boll.getDeltaTradeUpper().floatValue(),
                boll.getTrendTradeUpperUp().floatValue(),
                boll.getTrendTradeUpperDown().floatValue(),
                boll.getTradeLower().floatValue(),
                boll.getDeltaTradeLower().floatValue(),
                boll.getTrendTradeLowerUp().floatValue(),
                boll.getTrendTradeLowerDown().floatValue(),
                boll.getTradeUpper().floatValue(),
                boll.getDeltaTradeUpper().floatValue(),
                boll.getTrendTradeUpperUp().floatValue(),
                boll.getTrendTradeLowerDown().floatValue(),
                (float) cal.getSeason(),
                (float) cal.getMonth(),
                (float) cal.getWeek(),
                (float) cal.getDay(),
                (float) cal.getDayOfWeek(),
                (float) cal.getJulianDate(),
                (float) cal.getMoonAge(),
                (float) cal.getHour()
        ));
    }

    /**
     * Create output column names list
     *
     * @return
     */
    private ArrayList<String> outputColumnNameList() {
        return new ArrayList<>(Arrays.asList("buy", "sell"));
    }

    /**
     * Get input column names
     *
     * @return
     */
    private ArrayList<String> inputColumnNameList() {
        return new ArrayList<>(Arrays.asList(
                "count", "countBuy", "countSell", "open", "low", "high", "close",
                "total", "totalBuy", "totalSell", "volume", "volumeBuy", "volumeSell",
                "deltaCount", "deltaCountBuy", "deltaCountSell", "deltaOpen", "deltaLow",
                "deltaHigh", "deltaClose", "deltaTotal", "deltaTotalBuy", "deltaTotalSell",
                "deltaVolume", "deltaVolumeBuy", "deltaVolumeSell",
                "trendOpenUp", "trendOpenDown", "trendHighUp", "trendHighDown",
                "trendLowUp", "trendLowDown", "trendCloseUp", "trendCloseDown",
                "trendTotalUp", "trendTotalBuyUp", "trendTotalSellUp", "trendVolumeUp",
                "trendVolumeBuyUp", "trendVolumeSellUp", "trendTotalDown", "trendTotalBuyDown",
                "trendTotalSellDown", "trendVolumeDown", "trendVolumeBuyDown",
                "trendVolumeSellDown", "trendCountUp", "trendCountBuyUp", "trendCountSellUp",
                "trendCountDown", "trendCountBuyDown", "trendCountSellDown",
                "sma", "deltaSma", "trendSmaUp", "trendSmaDown", "stDev", "deltaStDev",
                "trendStDevUp", "trendStDevDown", "bollingerUpper", "deltaBollingerUpper",
                "trendBollingerUpperUp", "trendBollingerUpperDown", "bollingerLower",
                "deltaBollingerLower", "trendBollingerLowerUp", "trendBollingerLowerDown",
                "bollingerBandWith", "deltaBollingerBandWith", "trendBollingerBandWithUp",
                "trendBollingerBandWithDown", "tradeUpper", "deltaTradeUpper", "trendTradeUpperUp",
                "trendTradeUpperDown", "tradeLower", "deltaTradeLower", "trendTradeLowerUp",
                "trendTradeLowerDown", "tradeUpper", "deltaTradeUpper", "trendTradeUpperUp",
                "trendTradeUpperDown",
                "season", "month", "week", "day", "dayOfWeek", "julianDate", "moonAge",
                "hour"
        ));
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

        for (DeepInputDTO dto : dtoList) {
            dataSet.add(new TabularDataSet.Item(
                    Common.convert(this.inputValueList(dto)),
                    Common.convert(this.outputValueList(dto))));
        }

        return dataSet;

    }

}
