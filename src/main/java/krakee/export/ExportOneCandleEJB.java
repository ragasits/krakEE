/*
 * Copyright (C) 2023 rgt
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
package krakee.export;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import krakee.calc.CandleDTO;
import krakee.learn.LearnDTO;
import krakee.learn.LearnEJB;

/**
 * Export candle to CSV / ARFF format
 *
 * @author rgt
 */
@Stateless
public class ExportOneCandleEJB {

    @EJB
    private LearnEJB learnEjb;

    /**
     * Export to ARFF format
     *
     * @param learnName
     * @param candleList
     * @return
     */
    public List<String> toArff(String learnName, List<CandleDTO> candleList) {
        ArrayList<String> arffList = new ArrayList<>();
        arffList.addAll(this.headerToArff());
        for (CandleDTO candle : candleList) {
            LearnDTO learnDto = learnEjb.get(learnName, candle.getStartDate());
            arffList.add(this.oneRowToCSV(candle, ",") + this.tradeToCSV(learnDto));
        }
        return arffList;
    }

    /**
     * Export to CSV format
     *
     * @param learnName
     * @param candleList
     * @return
     */
    public List<String> toCSV(String learnName, List<CandleDTO> candleList) {
        ArrayList<String> csvList = new ArrayList<>();
        String separator = ";";

        csvList.add(this.headerToCSV(separator) + "trade");
        for (CandleDTO candle : candleList) {
            LearnDTO learnDto = learnEjb.get(learnName, candle.getStartDate());
            csvList.add(this.oneRowToCSV(candle, separator) + this.tradeToCSV(learnDto));
        }
        return csvList;
    }

    /**
     * Add learning data
     *
     * @param learnDto
     * @return
     */
    private String tradeToCSV(LearnDTO learnDto) {
        String trade = "none";

        if (learnDto != null) {
            trade = learnDto.getTrade();
        }
        return trade;
    }

    /**
     * Create CSV header
     *
     * @param separator
     * @return
     */
    private String headerToCSV(String separator) {

        //Candle
        return "startDate" + separator
                + "count" + separator
                + "countBuy" + separator
                + "countSell" + separator
                + "open" + separator
                + "low" + separator
                + "high" + separator
                + "close" + separator
                + "total" + separator
                + "totalBuy" + separator
                + "totalSell" + separator
                + "volume" + separator
                + "volumeBuy" + separator
                + "volumeSell" + separator
                //MovingAverage
                + "sma20" + separator
                + "ema9" + separator
                + "ema12" + separator
                + "ema26" + separator
                //Bollingerband
                + "stDev" + separator
                + "bollingerUpper" + separator
                + "bollingerLower" + separator
                + "bollingerBandWidth" + separator
                + "tradeLower" + separator
                + "tradeUpper" + separator
                + "bollingerBuy" + separator
                + "bollingerSell" + separator
                // RSI
                + "change" + separator
                + "gain" + separator
                + "loss" + separator
                + "avgGain" + separator
                + "avgLoss" + separator
                + "rs" + separator
                + "rsi" + separator
                + "rsiBuy" + separator
                + "rsiSell" + separator
                //MACD
                + "macdLine" + separator
                + "signalLine" + separator
                + "macdHistogram" + separator
                + "bullMarket" + separator
                + "bearMarket" + separator
                + "crossover" + separator
                //CCI
                + "typicalPrice" + separator
                + "sma20Typical" + separator
                + "mad20" + separator
                + "cci20" + separator
                + "overBought" + separator
                + "overSold" + separator;
    }

    /**
     * Create one row to CSV format
     *
     * @param dto
     * @param separator
     * @return
     */
    private String oneRowToCSV(CandleDTO dto, String separator) {
        //Candle
        return "'" + dto.getFormatedStartDate() + "'" + separator
                + dto.getCount() + separator
                + dto.getCountBuy() + separator
                + dto.getCountSell() + separator
                + dto.getOpen() + separator
                + dto.getLow() + separator
                + dto.getHigh() + separator
                + dto.getClose() + separator
                + dto.getTotal() + separator
                + dto.getTotalBuy() + separator
                + dto.getTotalSell() + separator
                + dto.getVolume() + separator
                + dto.getVolumeBuy() + separator
                + dto.getVolumeSell() + separator
                //MovingAverage
                + dto.getMovingAverage().getSma20() + separator
                + dto.getMovingAverage().getEma9() + separator
                + dto.getMovingAverage().getEma12() + separator
                + dto.getMovingAverage().getEma26() + separator
                //BollingerBand
                + dto.getBollinger().getStDev() + separator
                + dto.getBollinger().getBollingerUpper() + separator
                + dto.getBollinger().getBollingerLower() + separator
                + dto.getBollinger().getBollingerBandWidth() + separator
                + dto.getBollinger().getTradeLower() + separator
                + dto.getBollinger().getTradeUpper() + separator
                + dto.getBollinger().isBollingerBuy() + separator
                + dto.getBollinger().isBollingerSell() + separator
                //RSI
                + dto.getRsi().getChange() + separator
                + dto.getRsi().getGain() + separator
                + dto.getRsi().getLoss() + separator
                + dto.getRsi().getAvgGain() + separator
                + dto.getRsi().getAvgLoss() + separator
                + dto.getRsi().getRs() + separator
                + dto.getRsi().getRsi() + separator
                + dto.getRsi().isRsiBuy() + separator
                + dto.getRsi().isRsiSell() + separator
                //MACD
                + dto.getMacd().getMacdLine() + separator
                + dto.getMacd().getSignalLine() + separator
                + dto.getMacd().getMacdHistogram() + separator
                + dto.getMacd().isBullMarket() + separator
                + dto.getMacd().isBearMarket() + separator
                + dto.getMacd().isCrossover() + separator
                //MACD
                + dto.getCci().getTypicalPrice() + separator
                + dto.getCci().getSma20Typical() + separator
                + dto.getCci().getMad20() + separator
                + dto.getCci().getCci20() + separator
                + dto.getCci().isOverBought() + separator
                + dto.getCci().isOverSold() + separator;
    }

    /**
     * Create ARFF header
     * @return 
     */
    private List<String> headerToArff() {
        ArrayList<String> sb = new ArrayList<>();

        sb.add("@relation OneCandle_202302");
        sb.add("");
        sb.add("@attribute startDate date 'yyyy-MM-dd HH:mm:ss'");
        sb.add("@attribute count numeric");
        sb.add("@attribute countBuy numeric");
        sb.add("@attribute countSell numeric");
        sb.add("@attribute open numeric");
        sb.add("@attribute low numeric");
        sb.add("@attribute high numeric");
        sb.add("@attribute close numeric");
        sb.add("@attribute total numeric");
        sb.add("@attribute totalBuy numeric");
        sb.add("@attribute totalSell numeric");
        sb.add("@attribute volume numeric");
        sb.add("@attribute volumeBuy numeric");
        sb.add("@attribute volumeSell numeric");
        sb.add("@attribute sma20 numeric");
        sb.add("@attribute ema9 numeric");
        sb.add("@attribute ema12 numeric");
        sb.add("@attribute ema26 numeric");
        sb.add("@attribute stDev numeric");
        sb.add("@attribute bollingerUpper numeric");
        sb.add("@attribute bollingerLower numeric");
        sb.add("@attribute bollingerBandWidth numeric");
        sb.add("@attribute tradeLower numeric");
        sb.add("@attribute tradeUpper numeric");
        sb.add("@attribute bollingerBuy {true,false}");
        sb.add("@attribute bollingerSell {false,true}");
        sb.add("@attribute change numeric");
        sb.add("@attribute gain numeric");
        sb.add("@attribute loss numeric");
        sb.add("@attribute avgGain numeric");
        sb.add("@attribute avgLoss numeric");
        sb.add("@attribute rs numeric");
        sb.add("@attribute rsi numeric");
        sb.add("@attribute rsiBuy {false,true}");
        sb.add("@attribute rsiSell {false,true}");
        sb.add("@attribute macdLine numeric");
        sb.add("@attribute signalLine numeric");
        sb.add("@attribute macdHistogram numeric");
        sb.add("@attribute bullMarket {false,true}");
        sb.add("@attribute bearMarket {true,false}");
        sb.add("@attribute crossover {true,false}");
        sb.add("@attribute typicalPrice numeric");
        sb.add("@attribute sma20Typical numeric");
        sb.add("@attribute mad20 numeric");
        sb.add("@attribute cci20 numeric");
        sb.add("@attribute overBought {false,true}");
        sb.add("@attribute overSold {true,false}");
        sb.add("@attribute trade {buy,none,sell}");
        sb.add("");
        sb.add("@data");
        return sb;
    }
}
