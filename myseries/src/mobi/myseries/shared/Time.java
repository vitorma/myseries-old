package mobi.myseries.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Time implements Comparable<Time> {
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
        }
    }

    public static final Time MAX_VALUE = Time.valueOf("23:59");
    public static final Time MIN_VALUE = Time.valueOf("00:00");

    private Date time;

    private Time(Date time) {
        this.time = time;
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
        return this.time.compareTo(other.time);
    }

    public Date toDate() {
        return new Date(this.toLong());
    }

    public long toLong() {
        return this.time.getTime();
    }

    @Override
    public String toString() {
        return this.toString(DateFormat.getTimeInstance(DateFormat.SHORT));
    }

    public String toString(DateFormat format) {
        Validate.isNonNull(format, "format");

        return format.format(this.time);
    }

    @Override
    public int hashCode() {
        return this.time.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Time && this.time.equals(((Time) obj).time);
    }
}