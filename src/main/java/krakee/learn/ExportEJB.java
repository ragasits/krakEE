/*
 * Copyright (C) 2022 rgt
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
package krakee.learn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;

/**
 * Export Candle + Learn to CSV
 *
 * @author rgt
 */
@Stateless
public class ExportEJB {

    private static final String SEPARATOR = ",";

    @EJB
    private CandleEJB candleEjb;
    @EJB
    private LearnEJB learnEjb;

    /**
     * Export candles to CSV
     *
     * @param learnName
     * @param buyDate
     * @param sellDate
     * @return
     */
    public List<String> candleToCSV(String learnName, Date buyDate, Date sellDate) {
        ArrayList<String> csvList = new ArrayList<>();

        //Get Candles
        List<CandleDTO> candleList = candleEjb.get(buyDate, sellDate);

        boolean firstRow = true;
        String row;

        for (CandleDTO candleDTO : candleList) {
            if (firstRow) {
                row = this.candleHeaderToCSV(SEPARATOR) + "trade";
                csvList.add(row);
                firstRow = false;
            }

            LearnDTO learnDto = learnEjb.get(learnName, candleDTO.getStartDate());

            String trade = "none";
            if (learnDto != null) {
                trade = learnDto.getTrade();
            }
            row = this.candleRowToCSV(candleDTO, SEPARATOR) + trade;
            csvList.add(row);

        }
        return csvList;
    }

    /**
     * Export candle headers
     *
     * @param separator
     * @return
     */
    private String candleHeaderToCSV(String separator) {

        //Candle

        String sb = "startDate" + separator +
                "count" + separator +
                "countBuy" + separator +
                "countSell" + separator +
                "open" + separator +
                "low" + separator +
                "high" + separator +
                "close" + separator +
                "total" + separator +
                "totalBuy" + separator +
                "totalSell" + separator +
                "volume" + separator +
                "volumeBuy" + separator +
                "volumeSell" + separator +

                //MovingAverage
                "sma20" + separator +
                "ema9" + separator +
                "ema12" + separator +
                "ema26" + separator +

                //Bollingerband
                "stDev" + separator +
                "bollingerUpper" + separator +
                "bollingerLower" + separator +
                "bollingerBandWidth" + separator +
                "tradeLower" + separator +
                "tradeUpper" + separator +
                "bollingerBuy" + separator +
                "bollingerSell" + separator +

                // RSI
                "change" + separator +
                "gain" + separator +
                "loss" + separator +
                "avgGain" + separator +
                "avgLoss" + separator +
                "rs" + separator +
                "rsi" + separator +
                "rsiBuy" + separator +
                "rsiSell" + separator +

                //MACD
                "macdLine" + separator +
                "signalLine" + separator +
                "macdHistogram" + separator +
                "bullMarket" + separator +
                "bearMarket" + separator +
                "crossover" + separator +

                //CCI
                "typicalPrice" + separator +
                "sma20Typical" + separator +
                "mad20" + separator +
                "cci20" + separator +
                "overBought" + separator +
                "overSold" + separator;

        return sb;
    }

    /**
     * Export one candle row
     *
     * @param dto
     * @param separator
     * @return
     */
    private String candleRowToCSV(CandleDTO dto, String separator) {

        //Candle

        return dto.getStartDate() + separator +
                dto.getCount() + separator +
                dto.getCountBuy() + separator +
                dto.getCountSell() + separator +
                dto.getOpen() + separator +
                dto.getLow() + separator +
                dto.getHigh() + separator +
                dto.getClose() + separator +
                dto.getTotal() + separator +
                dto.getTotalBuy() + separator +
                dto.getTotalSell() + separator +
                dto.getVolume() + separator +
                dto.getVolumeBuy() + separator +
                dto.getVolumeSell() + separator +

                //MovingAverage
                dto.getMovingAverage().getSma20() + separator +
                dto.getMovingAverage().getEma9() + separator +
                dto.getMovingAverage().getEma12() + separator +
                dto.getMovingAverage().getEma26() + separator +

                //BollingerBand
                dto.getBollinger().getStDev() + separator +
                dto.getBollinger().getBollingerUpper() + separator +
                dto.getBollinger().getBollingerLower() + separator +
                dto.getBollinger().getBollingerBandWidth() + separator +
                dto.getBollinger().getTradeLower() + separator +
                dto.getBollinger().getTradeUpper() + separator +
                dto.getBollinger().isBollingerBuy() + separator +
                dto.getBollinger().isBollingerSell() + separator +

                //RSI
                dto.getRsi().getChange() + separator +
                dto.getRsi().getGain() + separator +
                dto.getRsi().getLoss() + separator +
                dto.getRsi().getAvgGain() + separator +
                dto.getRsi().getAvgLoss() + separator +
                dto.getRsi().getRs() + separator +
                dto.getRsi().getRsi() + separator +
                dto.getRsi().isRsiBuy() + separator +
                dto.getRsi().isRsiSell() + separator +

                //MACD
                dto.getMacd().getMacdLine() + separator +
                dto.getMacd().getSignalLine() + separator +
                dto.getMacd().getMacdHistogram() + separator +
                dto.getMacd().isBullMarket() + separator +
                dto.getMacd().isBearMarket() + separator +
                dto.getMacd().isCrossover() + separator +

                //MACD
                dto.getCci().getTypicalPrice() + separator +
                dto.getCci().getSma20Typical() + separator +
                dto.getCci().getMad20() + separator +
                dto.getCci().getCci20() + separator +
                dto.getCci().isOverBought() + separator +
                dto.getCci().isOverSold() + separator;
    }

}
