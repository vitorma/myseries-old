/*
 *   Dates.java
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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Dates {
    private static final Date MAX_DATE = new Date(Long.MAX_VALUE);

    public static Date parseDate(String date, DateFormat format, Date alternative) {
        Validate.isNonNull(format, "format");

        if (date == null || Strings.isBlank(date)) {
            return alternative;
        }

        try {
            return format.parse(date);
        } catch (ParseException e) {
            return alternative;
        }
    }

    public static Date parseDate(Long date, Date alternative) {
        if (date == null) {
            return alternative;
        }

        return new Date(date);
    }

    public static String toString(Date date, DateFormat format) {
        Validate.isNonNull(date, "date");
        Validate.isNonNull(format, "format");

        return format.format(date);
    }

    public static String toString(Date date, DateFormat format, String alternative) {
        Validate.isNonNull(format, "format");

        if (date == null) {
            return alternative;
        }

        return format.format(date);
    }

    public static int compareByNullLast(Date date1, Date date2) {
        return Objects.nullSafe(date1, MAX_DATE).compareTo(
               Objects.nullSafe(date2, MAX_DATE));
    }

    public static Date today() {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    public static Date now() {
        return new Date();
    }
}
