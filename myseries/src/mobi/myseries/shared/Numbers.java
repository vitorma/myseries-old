/*
 *   Numbers.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.shared;

import java.util.Date;

public class Numbers {

    public static int parseInt(String string, int defaultInt) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            return defaultInt;
        }
    }

    public static Long parseLong(Date date, Long defaultLong) {
        if (date == null) {
            return defaultLong;
        }

        return date.getTime();
    }

    public static Long parseLong(Time time, Long defaultLong) {
        if (time == null) {
            return defaultLong;
        }

        return time.toLong();
    }

    public static Long parseLong(WeekDay weekDay, Long defaultLong) {
        if (weekDay == null) {
            return defaultLong;
        }

        return weekDay.toLong();
    }
}
