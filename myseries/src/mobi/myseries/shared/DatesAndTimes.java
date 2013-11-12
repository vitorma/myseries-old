/*
 * DatesAndTimes.java
 *
 * Copyright 2012 MySeries Team.
 *
 * This file is part of MySeries.
 *
 * MySeries is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * MySeries is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MySeries. If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatesAndTimes {
    public static final long DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;
    public static final long WEEK_IN_MILLIS = 7L * DAY_IN_MILLIS;
    public static final long MONTH_IN_MILLIS = 30L * DAY_IN_MILLIS;
    public static final int DAYS_IN_A_WEEK = 7;

    private static final Date MAX_DATE = new Date(Long.MAX_VALUE);

    private static final TimeZone PST_TIME_ZONE = TimeZone.getTimeZone("GMT-8");

    /* Parsing */

    public static Date parse(String date, DateFormat format, Date defaultDate) {
        Validate.isNonNull(format, "format");

        if ((date == null) || Strings.isBlank(date)) {
            return defaultDate;
        }

        try {
            return format.parse(date);
        } catch (ParseException e) {
            return defaultDate;
        }
    }

    public static Date parseDate(Long date, Date defaultDate) {
        if (date == null) {
            return defaultDate;
        }

        return new Date(date);
    }

    public static WeekDay parse(Long weekDay, WeekDay defaultWeekDay) {
        if (weekDay == null) {
            return defaultWeekDay;
        }

        return WeekDay.valueOf(weekDay);
    }

    public static RelativeDay parse(Date day, RelativeDay defaultRelativeDay) {
        if (day == null) {
            return defaultRelativeDay;
        }

        return RelativeDay.valueOf(day);
    }

    /* Formatting */

    public static String toString(Date date, DateFormat format, String defaultDate) {
        Validate.isNonNull(format, "format");

        if (date == null) {
            return defaultDate;
        }

        return format.format(date);
    }

    public static String toString(Time time, DateFormat format, String defaultTime) {
        Validate.isNonNull(format, "format");

        if (time == null) {
            return defaultTime;
        }

        return time.toString(format);
    }

    public static String toString(WeekDay weekDay, DateFormat format, String defaultWeekDay) {
        Validate.isNonNull(format, "format");

        if (weekDay == null) {
            return defaultWeekDay;
        }

        return weekDay.toString(format);
    }

    /* Comparison */

    public static int compareByNullLast(Date date1, Date date2) {
        return Objects.nullSafe(date1, MAX_DATE).compareTo(Objects.nullSafe(date2, MAX_DATE));
    }

    public static int compareByNullLast(Time time1, Time time2) {
        return Objects.nullSafe(time1, Time.MAX_VALUE).compareTo(Objects.nullSafe(time2, Time.MAX_VALUE));
    }

    /* Calculus */

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

    public static int daysBetween(Date date1, Date date2) {
        Validate.isNonNull(date1, "date1");
        Validate.isNonNull(date2, "date2");

        return (int) ((date2.getTime() - date1.getTime()) / DAY_IN_MILLIS);
    }

    public static long toPstTime(long utcTimeStamp) {
        return utcTimeStamp + PST_TIME_ZONE.getOffset(utcTimeStamp);
    }

    public static Date toUtcTime(Date date, TimeZone originTimeZone) {
        long offset = - originTimeZone.getOffset(date.getTime());

        return new Date(date.getTime() + offset);
    }

    public static Date midnightDateFrom(Date date) {
        if(date == null)
            return null;
        long milis = (date.getTime() / DAY_IN_MILLIS) * DAY_IN_MILLIS ;

        return new Date(milis);
    }
}
