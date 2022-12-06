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

import com.mongodb.client.model.Sorts;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;

import static com.mongodb.client.model.Filters.lte;

/**
 * Export Candle + Learn to CSV
 *
 * @author rgt
 */
@Stateless
public class ExportEJB {
    private static final String SEPARATOR = ",";
    private static final String START_DATE = "startDate";

    @EJB
    private CandleEJB candleEjb;
    @EJB
    private LearnEJB learnEjb;
    @EJB
    private ConfigEJB configEjb;

    public List<String> candleToCSV(String learnName, Date buyDate, Date sellDate, ExportType type) {
        List<CandleDTO> candleList = candleEjb.get(buyDate, sellDate);

        if (type == ExportType.OneCandle){
            return oneCandleToCSV(learnName, candleList);
        } else {
            return histCandleToCSV(learnName, candleList, type);
        }
    }

    /**
     * Export 10 days history to CSV
     *
     * @param learnName
     * @param candleList
     * @return
     */
    public List<String> histCandleToCSV(String learnName, List<CandleDTO> candleList, ExportType type) {
        ArrayList<String> csvList = new ArrayList<>();

        boolean firstRow = true;
        String row;

        for (CandleDTO candleDTO : candleList) {
            if (firstRow) {
                    row = this.candleHeadersToCSV(type) + "trade";

                csvList.add(row);
                firstRow = false;
            }
            row = this.candleHistRowToCSV(candleDTO, learnName, type);
            csvList.add(row);
        }
        return csvList;
    }

    /**
     * Export Candle Headers to CSV
     * @param type
     * @return
     */
    private String candleHeadersToCSV(ExportType type) {
        StringBuilder sb = new StringBuilder();
        boolean isFirstDate = true;

        for (int i = 0; i < 10; i++) {
            String separator = "_" + i + SEPARATOR;

            if (type.equals(ExportType.HistCandle)){
                sb.append(this.candleHeaderToCSV(separator));
            } else {
                sb.append(this.candleCCiHeaderToCSV(separator, isFirstDate));
                isFirstDate = false;
            }
        }
        return sb.toString();
    }


    /**
     * Export candles to CSV
     *
     * @param learnName
     * @param candleList
     * @return
     */
    private List<String> oneCandleToCSV(String learnName, List<CandleDTO> candleList) {
        ArrayList<String> csvList = new ArrayList<>();

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

        return START_DATE + separator +
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
    }

    /**
     * Export CCi header to CSV
     * @param separator
     * @return
     */
    private String candleCCiHeaderToCSV(String separator, boolean isFirstDate) {

        StringBuilder sb = new StringBuilder();

        if (isFirstDate){
            sb.append(START_DATE).append(separator);
        }

        sb.append("cci20").append(separator);
        return sb.toString();
    }

    /**
     * Export  Candle Hist(10) to CSV
     * @param candle
     * @param learnName
     * @param type
     * @return
     */
    private String candleHistRowToCSV(CandleDTO candle, String learnName, ExportType type) {
        StringBuilder sb = new StringBuilder();

        List<CandleDTO> candleList = configEjb.getCandleColl()
                .find(
                        lte(START_DATE, candle.getStartDate())
                )
                .sort(Sorts.descending(START_DATE))
                .limit(10)
                .into(new ArrayList<>());

        boolean isFirstDate = true;
        for (int i = 0; i < 10; i++) {
            CandleDTO dto = candleList.get(i);

            if (type.equals(ExportType.HistCandle)){
                sb.append(this.candleRowToCSV(dto, SEPARATOR));
            } else {
                sb.append(this.candleRowCCiToCSV(dto, SEPARATOR, isFirstDate));
                isFirstDate = false;
            }


        }

        LearnDTO learnDto = learnEjb.get(learnName, candle.getStartDate());

        String trade = "none";
        if (learnDto != null) {
            trade = learnDto.getTrade();
        }

        sb.append(trade);
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

    /**
     * Export CCI hist
     * @param dto
     * @param separator
     * @return
     */
    private String candleRowCCiToCSV(CandleDTO dto, String separator, boolean isFirstDate) {

       StringBuilder sb = new StringBuilder();

       if (isFirstDate){
           sb.append(dto.getStartDate()).append(separator);
       }

       sb.append(dto.getCci().getCci20()).append(separator);

       return  sb.toString();
    }
}
