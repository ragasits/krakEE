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
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
        StringBuilder sb = new StringBuilder();

        //Candle
        sb.append("startDate").append(separator)
                .append("count").append(separator)
                .append("countBuy").append(separator)
                .append("countSell").append(separator)
                .append("open").append(separator)
                .append("low").append(separator)
                .append("high").append(separator)
                .append("close").append(separator)
                .append("total").append(separator)
                .append("totalBuy").append(separator)
                .append("totalSell").append(separator)
                .append("volume").append(separator)
                .append("volumeBuy").append(separator)
                .append("volumeSell").append(separator);

        //MovingAverage
        sb.append("sma20").append(separator)
                .append("ema9").append(separator)
                .append("ema12").append(separator)
                .append("ema26").append(separator);

        //Bollingerband
        sb.append("stDev").append(separator)
                .append("bollingerUpper").append(separator)
                .append("bollingerLower").append(separator)
                .append("bollingerBandWidth").append(separator)
                .append("tradeLower").append(separator)
                .append("tradeUpper").append(separator)
                .append("bollingerBuy").append(separator)
                .append("bollingerSell").append(separator);

        // RSI
        sb.append("change").append(separator)
                .append("gain").append(separator)
                .append("loss").append(separator)
                .append("avgGain").append(separator)
                .append("avgLoss").append(separator)
                .append("rs").append(separator)
                .append("rsi").append(separator)
                .append("rsiBuy").append(separator)
                .append("rsiSell").append(separator);

        //MACD
        sb.append("macdLine").append(separator)
                .append("signalLine").append(separator)
                .append("macdHistogram").append(separator)
                .append("bullMarket").append(separator)
                .append("bearMarket").append(separator)
                .append("crossover").append(separator);

        //CCI
        sb.append("typicalPrice").append(separator)
                .append("sma20Typical").append(separator)
                .append("mad20").append(separator)
                .append("cci20").append(separator)
                .append("overBought").append(separator)
                .append("overSold").append(separator);

        return sb.toString();
    }

    /**
     * Export one candle row
     *
     * @param dto
     * @param separator
     * @return
     */
    private String candleRowToCSV(CandleDTO dto, String separator) {
        StringBuilder sb = new StringBuilder();

        //Candle
        sb.append(dto.getStartDate()).append(separator)
                .append(dto.getCount()).append(separator)
                .append(dto.getCountBuy()).append(separator)
                .append(dto.getCountSell()).append(separator)
                .append(dto.getOpen()).append(separator)
                .append(dto.getLow()).append(separator)
                .append(dto.getHigh()).append(separator)
                .append(dto.getClose()).append(separator)
                .append(dto.getTotal()).append(separator)
                .append(dto.getTotalBuy()).append(separator)
                .append(dto.getTotalSell()).append(separator)
                .append(dto.getVolume()).append(separator)
                .append(dto.getVolumeBuy()).append(separator)
                .append(dto.getVolumeSell()).append(separator);

        //MovingAverage
        sb.append(dto.getMovingAverage().getSma20()).append(separator)
                .append(dto.getMovingAverage().getEma9()).append(separator)
                .append(dto.getMovingAverage().getEma12()).append(separator)
                .append(dto.getMovingAverage().getEma26()).append(separator);

        //BollingerBand
        sb.append(dto.getBollinger().getStDev()).append(separator)
                .append(dto.getBollinger().getBollingerUpper()).append(separator)
                .append(dto.getBollinger().getBollingerLower()).append(separator)
                .append(dto.getBollinger().getBollingerBandWidth()).append(separator)
                .append(dto.getBollinger().getTradeLower()).append(separator)
                .append(dto.getBollinger().getTradeUpper()).append(separator)
                .append(dto.getBollinger().isBollingerBuy()).append(separator)
                .append(dto.getBollinger().isBollingerSell()).append(separator);

        //RSI
        sb.append(dto.getRsi().getChange()).append(separator)
                .append(dto.getRsi().getGain()).append(separator)
                .append(dto.getRsi().getLoss()).append(separator)
                .append(dto.getRsi().getAvgGain()).append(separator)
                .append(dto.getRsi().getAvgLoss()).append(separator)
                .append(dto.getRsi().getRs()).append(separator)
                .append(dto.getRsi().getRsi()).append(separator)
                .append(dto.getRsi().isRsiBuy()).append(separator)
                .append(dto.getRsi().isRsiSell()).append(separator);

        //MACD
        sb.append(dto.getMacd().getMacdLine()).append(separator)
                .append(dto.getMacd().getSignalLine()).append(separator)
                .append(dto.getMacd().getMacdHistogram()).append(separator)
                .append(dto.getMacd().isBullMarket()).append(separator)
                .append(dto.getMacd().isBearMarket()).append(separator)
                .append(dto.getMacd().isCrossover()).append(separator);

        //MACD
        sb.append(dto.getCci().getTypicalPrice()).append(separator)
                .append(dto.getCci().getSma20Typical()).append(separator)
                .append(dto.getCci().getMad20()).append(separator)
                .append(dto.getCci().getCci20()).append(separator)
                .append(dto.getCci().isOverBought()).append(separator)
                .append(dto.getCci().isOverSold()).append(separator);

        return sb.toString();
    }

}
