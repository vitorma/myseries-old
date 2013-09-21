package mobi.myseries.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Time implements Comparable<Time> {
    private static final long MILISECONDS_IN_A_DAY = 24L * 60 * 60 * 1000;

    private static final DateFormat[] ACCEPTED_TIME_FORMATS = {
        new SimpleDateFormat("hh:mm aa", Locale.US),
        new SimpleDateFormat("hh:mmaa", Locale.US),
        new SimpleDateFormat("hh aa", Locale.US),
        new SimpleDateFormat("hhaa", Locale.US),
        new SimpleDateFormat("HH:mm", Locale.US)
    };

    static {
        for (DateFormat format : ACCEPTED_TIME_FORMATS) {
            format.setLenient(false);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    }

    public static final Time MAX_VALUE = Time.valueOf("23:59");
    public static final Time MIN_VALUE = Time.valueOf("00:00");

    private final Date time;

    private Time(Date time) {
        this.time = new Date(time.getTime() % MILISECONDS_IN_A_DAY);
    }

    public static Time valueOf(long time) {
        return new Time(new Date(time));
    }

    public static Time valueOf(String time) {
        Validate.isNonNull(time, "time");

        for (DateFormat df : ACCEPTED_TIME_FORMATS) {
            try {
                return new Time(df.parse(time));
            } catch (ParseException e) {
                continue;
            }
        }

        return null;
    }

    @Override
    public int compareTo(Time other) {
        return time.compareTo(other.time);
    }

    public Date toDate() {
        return new Date(toLong());
    }

    public long toLong() {
        return time.getTime();
    }

    @Override
    public String toString() {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        return this.toString(df);
    }

    public String toString(DateFormat format) {
        Validate.isNonNull(format, "format");

        return format.format(time);
    }

    @Override
    public int hashCode() {
        return time.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Time && time.equals(((Time) obj).time);
    }
}