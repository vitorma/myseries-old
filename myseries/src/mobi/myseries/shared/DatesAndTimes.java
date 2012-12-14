/*
 *   DatesAndTimes.java
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
import java.util.Comparator;
import java.util.Date;

import android.text.format.DateUtils;

public class DatesAndTimes {
    public static final long DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;
    public static final long WEEK_IN_MILLIS = 7L * DAY_IN_MILLIS;
    public static final long MONTH_IN_MILLIS = 30L * DAY_IN_MILLIS;
    public static final int DAYS_IN_A_WEEK = 7;

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

    public static Time parseAirtime(Long airtime, Time alternative) {
        if (airtime == null) {
            return alternative;
        }

        return Time.valueOf(airtime);
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

    public static String toString(Time airtime, DateFormat format, String alternative) {
        if (airtime == null) {
            return alternative;
        }

        if (format == null) {
            return airtime.toString();
        }

        return airtime.toString(format);
    }

    public static int compareByNullLast(Date date1, Date date2) {
        return Objects.nullSafe(date1, MAX_DATE).compareTo(
               Objects.nullSafe(date2, MAX_DATE));
    }

    public static int compareByNullLast(Time airtime1, Time airtime2) {
        return Objects.nullSafe(airtime1, Time.MAX_VALUE).compareTo(
               Objects.nullSafe(airtime2, Time.MAX_VALUE));
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

    public static Comparator<Date> naturalComparator() {
        return new Comparator<Date>() {
            @Override
            public int compare(Date d1, Date d2) {
                return d1.compareTo(d2);
            }
        };
    }

    public static Comparator<Date> reversedComparator() {
        return new Comparator<Date>() {
            @Override
            public int compare(Date d1, Date d2) {
                return d2.compareTo(d1);
            }
        };
    }

    public static int daysBetween(Date d1, Date d2) {
        Validate.isNonNull(d1, "date1");
        Validate.isNonNull(d2, "date2");

        return (int) ((d2.getTime() - d1.getTime())/DAY_IN_MILLIS);
    }

    public static String relativeTimeStringForNear(Date date) {
        if (date == null) {
            return "";
        }

        long time = date.getTime();
        long now = System.currentTimeMillis();
        long duration = Math.abs(now - time);

        if (duration >= WEEK_IN_MILLIS) {
            return "";
        }

        long minResolution = DAY_IN_MILLIS;
        int flag = DateUtils.FORMAT_ABBREV_RELATIVE;

        return DateUtils.getRelativeTimeSpanString(time, now, minResolution, flag).toString();
    }

    public static String relativeTimeStringFor(Date date, String defaultReturn) {
        Validate.isNonNull(defaultReturn, "defaultReturn");

        if (date == null) {
            return defaultReturn;
        }

        long time = date.getTime();
        long now = System.currentTimeMillis();
        long minResolution = DAY_IN_MILLIS;
        int flag = DateUtils.FORMAT_ABBREV_ALL;

        return DateUtils.getRelativeTimeSpanString(time, now, minResolution, flag).toString();
    }
}
