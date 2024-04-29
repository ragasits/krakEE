/*
 * Copyright (C) 2024 rgt
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
package krakee.prod;

import java.util.Date;
import org.bson.types.ObjectId;

/**
 *
 * @author rgt
 */
public class ProdLogDTO {
    
    private ObjectId id;
    private final Date runDate;
    private final boolean runProduction;
    private final boolean runTrade;
    private final boolean runCandle;
    private final Date stopdate;
    private final long duration;

    public ProdLogDTO(Date runDate, boolean runProduction, boolean runTrade, boolean runCandle, Date stopdate) {
        this.runDate = runDate;
        this.runProduction = runProduction;
        this.runTrade = runTrade;
        this.runCandle = runCandle;
        this.stopdate = stopdate;
        this.duration = stopdate.getTime()-runDate.getTime();
    }

    public ObjectId getId() {
        return id;
    }

    public Date getRunDate() {
        return runDate;
    }

    public boolean isRunProduction() {
        return runProduction;
    }

    public boolean isRunTrade() {
        return runTrade;
    }

    public boolean isRunCandle() {
        return runCandle;
    }

    public Date getStopdate() {
        return stopdate;
    }

    public long getDuration() {
        return duration;
    }
}
