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
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

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
     * Add learning data
     *
     * @param learnDto
     * @return
     */
    private String getTrade(LearnDTO learnDto) {
        String trade = "none";

        if (learnDto != null) {
            trade = learnDto.getTrade();
        }
        return trade;
    }


    /**
     * Create weka attributes
     * @return 
     */
    private ArrayList<Attribute> getAttributes() {
        ArrayList<String> booleanValues = new ArrayList<>();
        booleanValues.add("true");
        booleanValues.add("false");

        ArrayList<Attribute> attributes = new ArrayList<>();

        attributes.add(new Attribute("startDate", "yyyy-MM-dd HH:mm:ss"));
        attributes.add(new Attribute("count"));
        attributes.add(new Attribute("countBuy"));
        attributes.add(new Attribute("countSell"));
        attributes.add(new Attribute("open"));
        attributes.add(new Attribute("low"));
        attributes.add(new Attribute("high"));
        attributes.add(new Attribute("close"));
        attributes.add(new Attribute("total"));
        attributes.add(new Attribute("totalBuy"));
        attributes.add(new Attribute("totalSell"));
        attributes.add(new Attribute("volume"));
        attributes.add(new Attribute("volumeBuy"));
        attributes.add(new Attribute("volumeSell"));
        attributes.add(new Attribute("sma20"));
        attributes.add(new Attribute("ema9"));
        attributes.add(new Attribute("ema12"));
        attributes.add(new Attribute("ema26"));
        attributes.add(new Attribute("stDev"));
        attributes.add(new Attribute("bollingerUpper"));
        attributes.add(new Attribute("bollingerLower"));
        attributes.add(new Attribute("bollingerBandWidth"));
        attributes.add(new Attribute("tradeLower"));
        attributes.add(new Attribute("tradeUpper"));

        attributes.add(new Attribute("bollingerBuy", booleanValues));
        attributes.add(new Attribute("bollingerSell", booleanValues));

        attributes.add(new Attribute("change"));
        attributes.add(new Attribute("gain"));
        attributes.add(new Attribute("loss"));
        attributes.add(new Attribute("avgGain"));
        attributes.add(new Attribute("avgLoss"));
        attributes.add(new Attribute("rs"));
        attributes.add(new Attribute("rsi"));

        attributes.add(new Attribute("rsiBuy", booleanValues));
        attributes.add(new Attribute("rsiSell", booleanValues));

        attributes.add(new Attribute("macdLine"));
        attributes.add(new Attribute("signalLine"));
        attributes.add(new Attribute("macdHistogram"));

        attributes.add(new Attribute("bullMarket", booleanValues));
        attributes.add(new Attribute("bearMarket", booleanValues));
        attributes.add(new Attribute("crossover", booleanValues));

        attributes.add(new Attribute("typicalPrice"));
        attributes.add(new Attribute("sma20Typical"));
        attributes.add(new Attribute("mad20"));
        attributes.add(new Attribute("cci20"));

        attributes.add(new Attribute("overBought", booleanValues));
        attributes.add(new Attribute("overSold", booleanValues));

        ArrayList<String> tradeValues = new ArrayList<>();
        tradeValues.add("buy");
        tradeValues.add("none");
        tradeValues.add("sell");
        attributes.add(new Attribute("trade", tradeValues));

        return attributes;
    }

    /**
     * Create weka values
     * @param dto
     * @param instances
     * @return 
     */
    private DenseInstance getValues(CandleDTO dto,  Instances instances) {
        DenseInstance instance = new DenseInstance(instances.numAttributes());
        instance.setDataset(instances);

        instance.setValue(instances.attribute("startDate"), dto.getStartDate().getTime());
        instance.setValue(instances.attribute("count"), dto.getCount());
        instance.setValue(instances.attribute("countBuy"), dto.getCountBuy());
        instance.setValue(instances.attribute("countSell"), dto.getCountSell());
        instance.setValue(instances.attribute("open"), dto.getOpen().doubleValue());
        instance.setValue(instances.attribute("low"), dto.getLow().doubleValue());
        instance.setValue(instances.attribute("high"), dto.getHigh().doubleValue());
        instance.setValue(instances.attribute("close"), dto.getClose().doubleValue());
        instance.setValue(instances.attribute("total"), dto.getTotal().doubleValue());
        instance.setValue(instances.attribute("totalBuy"), dto.getTotalBuy().doubleValue());
        instance.setValue(instances.attribute("totalSell"), dto.getTotalSell().doubleValue());
        instance.setValue(instances.attribute("volume"), dto.getVolume().doubleValue());
        instance.setValue(instances.attribute("volumeBuy"), dto.getVolumeBuy().doubleValue());
        instance.setValue(instances.attribute("volumeSell"), dto.getVolumeSell().doubleValue());
        //MovingAverage
        instance.setValue(instances.attribute("sma20"), dto.getMovingAverage().getSma20().doubleValue());
        instance.setValue(instances.attribute("ema9"), dto.getMovingAverage().getEma9().doubleValue());
        instance.setValue(instances.attribute("ema12"), dto.getMovingAverage().getEma12().doubleValue());
        instance.setValue(instances.attribute("ema26"), dto.getMovingAverage().getEma26().doubleValue());
        //BollingerBand
        instance.setValue(instances.attribute("stDev"), dto.getBollinger().getStDev().doubleValue());
        instance.setValue(instances.attribute("bollingerUpper"), dto.getBollinger().getBollingerUpper().doubleValue());
        instance.setValue(instances.attribute("bollingerLower"), dto.getBollinger().getBollingerLower().doubleValue());
        instance.setValue(instances.attribute("bollingerBandWidth"), dto.getBollinger().getBollingerBandWidth().doubleValue());
        instance.setValue(instances.attribute("tradeLower"), dto.getBollinger().getTradeLower().doubleValue());
        instance.setValue(instances.attribute("tradeUpper"), dto.getBollinger().getTradeUpper().doubleValue());
        instance.setValue(instances.attribute("bollingerBuy"), Boolean.toString(dto.getBollinger().isBollingerBuy()));
        instance.setValue(instances.attribute("bollingerSell"), Boolean.toString(dto.getBollinger().isBollingerSell()));
        //RSI
        instance.setValue(instances.attribute("change"), dto.getRsi().getChange().doubleValue());
        instance.setValue(instances.attribute("gain"), dto.getRsi().getGain().doubleValue());
        instance.setValue(instances.attribute("loss"), dto.getRsi().getLoss().doubleValue());
        instance.setValue(instances.attribute("avgGain"), dto.getRsi().getAvgGain().doubleValue());
        instance.setValue(instances.attribute("avgLoss"), dto.getRsi().getAvgLoss().doubleValue());
        instance.setValue(instances.attribute("rs"), dto.getRsi().getRs().doubleValue());
        instance.setValue(instances.attribute("rsi"), dto.getRsi().getRsi().doubleValue());
        instance.setValue(instances.attribute("rsiBuy"), Boolean.toString(dto.getRsi().isRsiBuy()));
        instance.setValue(instances.attribute("rsiSell"), Boolean.toString(dto.getRsi().isRsiSell()));
        //MACD
        instance.setValue(instances.attribute("macdLine"), dto.getMacd().getMacdLine().doubleValue());
        instance.setValue(instances.attribute("signalLine"), dto.getMacd().getSignalLine().doubleValue());
        instance.setValue(instances.attribute("macdHistogram"), dto.getMacd().getMacdHistogram().doubleValue());
        instance.setValue(instances.attribute("bullMarket"), Boolean.toString(dto.getMacd().isBullMarket()));
        instance.setValue(instances.attribute("bearMarket"), Boolean.toString(dto.getMacd().isBearMarket()));
        instance.setValue(instances.attribute("crossover"), Boolean.toString(dto.getMacd().isCrossover()));
        //MACD
        instance.setValue(instances.attribute("typicalPrice"), dto.getCci().getTypicalPrice().doubleValue());
        instance.setValue(instances.attribute("sma20Typical"), dto.getCci().getSma20Typical().doubleValue());
        instance.setValue(instances.attribute("mad20"), dto.getCci().getMad20().doubleValue());
        instance.setValue(instances.attribute("cci20"), dto.getCci().getCci20().doubleValue());
        instance.setValue(instances.attribute("overBought"), Boolean.toString(dto.getCci().isOverBought()));
        instance.setValue(instances.attribute("overSold"), Boolean.toString(dto.getCci().isOverSold()));

        return instance;

    }

    /**
     * Create weka instance
     * @param learnName
     * @param candleList
     * @return 
     */
    public Instances toInstances(String learnName, List<CandleDTO> candleList) {

        ArrayList<Attribute> attributes = this.getAttributes();
        Instances instances = new Instances("name", attributes, 0);

        for (CandleDTO candle : candleList) {
            DenseInstance instance = this.getValues(candle, instances);

            LearnDTO learnDto = learnEjb.get(learnName, candle.getStartDate());
            instance.setValue(instances.attribute("trade"), this.getTrade(learnDto));

            instances.add(instance);
        }

        instances.setClassIndex(instances.attribute("trade").index());
        return instances;
    }
}
