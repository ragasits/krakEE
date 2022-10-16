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
package krakee.learn;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import krakee.calc.CandleDTO;
import krakee.calc.CandleEJB;

/**
 * Manage learning data
 *
 * @author rgt
 */
@Stateless
public class LearnEJB {

    private static final String STARTDATE = "startDate";
    private static final String LEARNNAME = "name";

    @EJB
    private ConfigEJB configEjb;
    @EJB
    private CandleEJB candleEjb;

    /**
     * Get all learns
     *
     * @return
     */
    public List<LearnDTO> get() {
        return configEjb.getLearnColl()
                .find()
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());
    }

    /**
     * Get Learns by Candle
     *
     * @param startDate
     * @return
     */
    public List<LearnDTO> get(Date startDate) {
        return configEjb.getLearnColl()
                .find(eq(STARTDATE, startDate))
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());
    }

    /**
     * Get learns filter by Name
     *
     * @param learnName
     * @return
     */
    public List<LearnDTO> get(String learnName) {
        return configEjb.getLearnColl()
                .find(eq(LEARNNAME, learnName))
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());
    }

    /**
     * Get learns filter by:
     * @param learnName
     * @param buyDate
     * @param sellDate
     * @return 
     */
    public List<LearnDTO> get(String learnName, Date buyDate, Date sellDate) {
        return configEjb.getLearnColl()
                .find(
                        and(
                                eq(LEARNNAME, learnName),
                                gte(STARTDATE, buyDate),
                                lte(STARTDATE, sellDate)
                        )
                )
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());
    }

    /**
     * Get one learn
     *
     * @param learnName
     * @param startDate
     * @return
     */
    public LearnDTO get(String learnName, Date startDate) {
        return configEjb.getLearnColl()
                .find(and(eq(LEARNNAME, learnName), eq(STARTDATE, startDate)))
                .first();
    }

    /**
     * Get first learn
     *
     * @param learnName
     * @return
     */
    public LearnDTO getFirst(String learnName) {
        return configEjb.getLearnColl()
                .find(eq(LEARNNAME, learnName))
                .sort(Sorts.ascending(STARTDATE))
                .first();
    }

    /**
     * Get last learn
     *
     * @param learnName
     * @return
     */
    public LearnDTO getLast(String learnName) {
        return configEjb.getLearnColl()
                .find(eq(LEARNNAME, learnName))
                .sort(Sorts.descending(STARTDATE))
                .first();
    }

    /**
     * Get BUYs
     *
     * @return
     */
    public List<LearnDTO> getBuy() {
        return configEjb.getLearnColl()
                .find(eq("trade", "buy"))
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());
    }

    /**
     * Get SELLs
     *
     * @return
     */
    public List<LearnDTO> getSell() {
        return configEjb.getLearnColl()
                .find(eq("trade", "sell"))
                .sort(Sorts.ascending(STARTDATE))
                .into(new ArrayList<>());
    }

    /**
     * Get only unique names
     *
     * @return
     */
    public List<String> getNames() {
        return configEjb.getLearnColl()
                .distinct(LEARNNAME, String.class)
                .into(new ArrayList<>());
    }

    /**
     * Add new learn data
     *
     * @param dto
     */
    public void add(LearnDTO dto) {
        configEjb.getLearnColl().insertOne(dto);
    }

    /**
     * Modify existing learning data
     *
     * @param dto
     */
    public void update(LearnDTO dto) {
        configEjb.getLearnColl().replaceOne(
                eq("_id", dto.getId()), dto);
    }

    /**
     * Delete existing learning data
     *
     * @param dto
     */
    public void delete(LearnDTO dto) {
        configEjb.getLearnColl().deleteOne(eq("_id", dto.getId()));
    }

    /**
     * Looking for the best position for the learns
     */
    public void chkLearnPeaks() {

        ArrayList<LearnDTO> learnList = (ArrayList<LearnDTO>) this.get();
        for (LearnDTO learn : learnList) {
            StringBuilder chkMessage = new StringBuilder();

            //Get Current
            CandleDTO current = candleEjb.get(learn.getStartDate());

            //Before 5
            ArrayList<CandleDTO> beforeList = configEjb.getCandleColl()
                    .find(lt(STARTDATE, learn.getStartDate()))
                    .sort(Sorts.descending(STARTDATE))
                    .limit(5)
                    .into(new ArrayList<>());

            //After 5
            ArrayList<CandleDTO> afterList = configEjb.getCandleColl()
                    .find(gt(STARTDATE, learn.getStartDate()))
                    .sort(Sorts.ascending(STARTDATE))
                    .limit(5)
                    .into(new ArrayList<>());

            //chk it
            beforeList.addAll(afterList);
            for (CandleDTO candle : afterList) {
                if (learn.getTrade().equals("buy")) {
                    if (candle.getClose().compareTo(current.getClose()) == -1) {
                        chkMessage.append(candle.getClose()).append(",");
                    }
                } else {
                    if (candle.getClose().compareTo(current.getClose()) == 1) {
                        chkMessage.append(candle.getClose()).append(",");
                    }
                }
            }

            //Store Result
            if (chkMessage.length() != 0) {
                learn.setChkMessage(chkMessage.toString());
            } else {
                learn.setChkMessage(null);
            }
            this.update(learn);
        }

    }

    /**
     * Looking for wrong learn pairs
     */
    public void chkLearnPairs() {

        ArrayList<LearnDTO> learnList = (ArrayList<LearnDTO>) this.get();
        for (int i = 0; i < learnList.size(); i++) {
            StringBuilder chkMessage = new StringBuilder();
            LearnDTO learn = learnList.get(i);

            if (i == 0) {
                //Fist element
                if (!learn.getTrade().equals("buy")) {
                    chkMessage.append("First element not BUY");
                }
            } else {
                //Looking for wrong pairs
                LearnDTO prev = learnList.get(i - 1);

                if (learn.getTrade().equals("buy") && prev.getTrade().equals("buy")) {
                    chkMessage.append("Wrong pairs");
                } else if (learn.getTrade().equals("sell") && prev.getTrade().equals("sell")) {
                    chkMessage.append("Wrong pairs");
                }
            }

            //Last element
            if ((i == learnList.size() - 1) && (!learn.getTrade().equals("sell"))) {
                chkMessage.append("First element not sell");
            }

            //Store Result
            if (chkMessage.length() != 0) {
                learn.setChkMessage(chkMessage.toString());
            } else {
                learn.setChkMessage(null);
            }
            this.update(learn);

        }

    }
}
