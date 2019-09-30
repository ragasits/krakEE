package krakee.web;

import com.mongodb.client.model.Sorts;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;

/**
 *
 * @author rgt
 */
@Stateless
public class MongoEJB {

    @EJB
    ConfigEJB config;

    /**
     * Get latest date value from Candle collection
     *
     * @return
     */
    public Date getLatesDateFromCandle() {
        try {
            return config.getCandleColl()
                    .find()
                    .sort(Sorts.descending("startDate"))
                    .first()
                    .getDate("startDate");
        } catch (NullPointerException e) {
            return null;
        }
    }
}
