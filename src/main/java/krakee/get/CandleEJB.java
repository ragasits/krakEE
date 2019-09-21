package krakee.get;

import com.mongodb.client.model.Sorts;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Calculate and store candle elements
 *
 * @author rgt
 */
@Stateless
public class CandleEJB {

    @EJB
    ConfigEJB config;

    /**
     * Call candle generation methods
     */
    @Asynchronous
    public void callCandle() {
        config.setRunCandle(false);
        this.calcDateList();
        config.setRunCandle(true);
    }

    /**
     * Calculate missing candle dates
     */
    private void calcDateList() {
        Date startDate;
        Date stopDate;

        //Start date
        //Get lates Date from the candle coolection
        if (config.getCandleColl().countDocuments() > 0) {
            startDate = config.getCandleColl().find()
                    .sort(Sorts.descending("candleDate"))
                    .first()
                    .get("candleDate", Date.class);
        } else {
            //if it missing, get first from the tadepaircoll
            startDate = config.getTradePairColl().find()
                    .sort(Sorts.ascending("timeDate"))
                    .first()
                    .get("timeDate", Date.class);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int minute = cal.get(Calendar.MINUTE);

        // 30 minutes candle
        if (minute < 30) {
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else {
            cal.set(Calendar.MINUTE, 30);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        startDate = cal.getTime();

        //Stopdate
        stopDate = config.getTradePairColl().find()
                .sort(Sorts.descending("timeDate"))
                .first().get("timeDate", Date.class);

        //Store dates
        while (startDate.before(stopDate)) {
            CandleDTO dto = new CandleDTO(startDate);
            config.getCandleColl().insertOne(dto.getCandle());
            cal.add(Calendar.MINUTE, 30);
            startDate = cal.getTime();

            System.out.println("calcDateList " + startDate + "---" + stopDate);

        }

    }

}
