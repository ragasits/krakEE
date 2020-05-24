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

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import krakee.ConfigEJB;
import org.bson.types.ObjectId;

/**
 * Manage learning data
 * @author rgt
 */
@Stateless
public class LearnEJB {

    static final Logger LOGGER = Logger.getLogger(LearnEJB.class.getCanonicalName());

    @EJB
    private ConfigEJB config;

    /**
     * Get Learns by Candle 
     * @param candleId
     * @return 
     */
    public List<LearnDTO> get(ObjectId candleId) {
        return config.getLearnColl()
                .find(eq("candleId", candleId))
                .sort(Sorts.ascending("startDate"))
                .into(new ArrayList<>());
    }

    /**
     * Get only unique names
     * @return 
     */
    public List<String> getNames() {
        return config.getLearnColl()
                .distinct("name", String.class)
                .into(new ArrayList<String>());
    }

    /**
     * Add new learn data
     * @param dto 
     */
    public void add(LearnDTO dto) {
        config.getLearnColl().insertOne(dto);
    }

    /**
     * Modify existing learning data
     * @param dto 
     */
    public void update(LearnDTO dto) {
        config.getLearnColl().replaceOne(
                eq("_id", dto.getId()), dto);
    }

    /**
     * Delete existing learning data
     * @param dto 
     */
    public void delete(LearnDTO dto) {
        config.getLearnColl().deleteOne(eq("_id", dto.getId()));
    }
}
