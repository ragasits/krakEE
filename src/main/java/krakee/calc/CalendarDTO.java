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
package krakee.calc;

import java.util.Calendar;
import java.util.Date;
import org.bson.Document;

/**
 *
 * @author rgt
 */
public class CalendarDTO {

    private final String season;
    private final int month;
    private final int week;
    private final int day;
    private final int dayOfWeek;
    private final int julianDate;
    private final double moonAge;
    private final int hour;

    private final boolean holiday;

    public CalendarDTO(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        this.month = cal.get(Calendar.MONTH) + 1;
        this.dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        this.week = cal.get(Calendar.WEEK_OF_YEAR);
        this.day = cal.get(Calendar.DAY_OF_MONTH);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);

        if (month == 3 || month == 4 || month == 5) {
            this.season = "Spring";
        } else if (month == 6 || month == 7 || month == 8) {
            this.season = "Summer";
        } else if (month == 9 || month == 10 || month == 11) {
            this.season = "Autumn";
        } else {
            this.season = "Winter";
        }

        this.julianDate = this.julianDate(day, month, cal.get(Calendar.YEAR));
        this.moonAge = this.MoonAge(day, month, cal.get(Calendar.YEAR), this.julianDate);

        //West holidays
        this.holiday
                = //XMAS
                (this.month == 12 && (this.day == 25 || this.day == 26))
                //New year
                || (this.month == 1 && this.day == 1);
        //Easter
        //Pentecost
        //Halloween

        //China holidays
        //Arabs holidays
    }

    /**
     * Create DTO from Document
     *
     * @param doc
     */
    public CalendarDTO(Document doc) {
        this.season = doc.getString("season");
        this.month = doc.getInteger("month");
        this.week = doc.getInteger("week");
        this.day = doc.getInteger("day");
        this.hour = doc.getInteger("hour");
        this.dayOfWeek = doc.getInteger("dayOfWeek");
        this.julianDate = doc.getInteger("julianDate");
        this.moonAge = doc.getDouble("moonAge");
        this.holiday = doc.getBoolean("holiday");
    }

    /**
     * Create Document from DTO
     *
     * @return
     */
    public Document getCalendar() {
        return new Document("season", this.season)
                .append("month", this.month)
                .append("week", this.week)
                .append("day", this.day)
                .append("hour", this.hour)
                .append("dayOfWeek", this.dayOfWeek)
                .append("julianDate", this.julianDate)
                .append("moonAge", this.moonAge)
                .append("holiday", this.holiday);
    }

    /**
     * Calculate Julian date
     *
     * @param d
     * @param m
     * @param y
     * @return
     */
    private int julianDate(int d, int m, int y) {
        int mm, yy;
        int k1, k2, k3;
        int j;

        yy = y - (int) ((12 - m) / 10);
        mm = m + 9;
        if (mm >= 12) {
            mm = mm - 12;
        }
        k1 = (int) (365.25 * (yy + 4712));
        k2 = (int) (30.6001 * mm + 0.5);
        k3 = (int) ((int) ((yy / 100) + 49) * 0.75) - 38;
        // 'j' for dates in Julian calendar:
        j = k1 + k2 + d + 59;
        if (j > 2299160) {
            // For Gregorian calendar:
            j = j - k3; // 'j' is the Julian date at 12h UT (Universal Time)
        }
        return j;
    }

    /**
     * Calculate moon age
     *
     * @param d
     * @param m
     * @param y
     * @return
     */
    private double MoonAge(int d, int m, int y, int j) {
        //Calculate the approximate phase of the moon
        Double ip = (j + 4.867) / 29.53059;
        ip = ip - Math.floor(ip);
        //After several trials I've seen to add the following lines, 
        //which gave the result was not bad 
        double ag;
        if (ip < 0.5) {
            ag = ip * 29.53059 + 29.53059 / 2;
        } else {
            ag = ip * 29.53059 - 29.53059 / 2;
        }
        // Moon's age in days
        ag = Math.floor(ag) + 1;
        return ag;
    }

    public String getSeason() {
        return season;
    }

    public int getMonth() {
        return month;
    }

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getJulianDate() {
        return julianDate;
    }

    public double getMoonAge() {
        return moonAge;
    }

    public int getHour() {
        return hour;
    }

    public boolean isHoliday() {
        return holiday;
    }
    
    

}
