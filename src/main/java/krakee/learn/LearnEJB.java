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
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;

/**
 * Manage learning data
 *
 * @author rgt
 */
@Stateless
public class LearnEJB {

    static final Logger LOGGER = Logger.getLogger(LearnEJB.class.getCanonicalName());

    @EJB
    private ConfigEJB configEjb;

    /**
     * Get all learns
     *
     * @return
     */
    public List<LearnDTO> get() {
        return configEjb.getLearnColl()
                .find()
                .sort(Sorts.ascending("startDate"))
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
                .find(eq("startDate", startDate))
                .sort(Sorts.ascending("startDate"))
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
                .find(eq("name", learnName))
                .sort(Sorts.ascending("startDate"))
                .into(new ArrayList<>());
    }


    /**
     * Get one learn
     * @param learnName
     * @param startDate
     * @return 
     */
    public LearnDTO get(String learnName, Date startDate){
        return configEjb.getLearnColl()
                .find(and(eq("name", learnName), eq("startDate", startDate)))
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
                .find(eq("name", learnName))
                .sort(Sorts.ascending("startDate"))
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
                .find(eq("name", learnName))
                .sort(Sorts.descending("startDate"))
                .first();
    }

    /**
     * Get only unique names
     *
     * @return
     */
    public List<String> getNames() {
        return configEjb.getLearnColl()
                .distinct("name", String.class)
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
}
