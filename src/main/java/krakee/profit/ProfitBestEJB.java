/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.profit;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import org.bson.Document;

/**
 * Calculate profit
 *
 * @author rgt
 */
@Stateless
public class ProfitBestEJB {

    static final Logger LOGGER = Logger.getLogger(ProfitBestEJB.class.getCanonicalName());

    @EJB
    ConfigEJB config;

    public List<ProfitBestDTO> get() {
        MongoCursor<Document> cursor = config.getProfitBestColl()
                .find()
                .sort(Sorts.descending("testNum"))
                .iterator();

        List<ProfitBestDTO> list = new ArrayList<>();
        while (cursor.hasNext()) {
            ProfitBestDTO dto = new ProfitBestDTO(cursor.next());
            list.add(dto);
        }
        return list;
    }

    public ProfitBestDTO getBest() {
        Document doc = config.getProfitBestColl()
                .find()
                .sort(Sorts.descending("eur"))
                .first();

        if (doc == null) {
            return null;
        } else {
            return new ProfitBestDTO(doc);
        }
    }

    public ProfitBestDTO getMaxTest() {
        Document doc = config.getProfitBestColl()
                .find()
                .sort(Sorts.descending("testNum"))
                .first();

        if (doc == null) {
            return null;
        } else {
            return new ProfitBestDTO(doc);
        }
    }
}
