package mobi.myseries.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WeekDay implements Comparable<WeekDay> {
    private static final DateFormat ACCEPTED_FORMAT_FOR_PARSING = new SimpleDateFormat("E", Locale.US);

    static {
        ACCEPTED_FORMAT_FOR_PARSING.setLenient(false);
        ACCEPTED_FORMAT_FOR_PARSING.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final Date day;

    private WeekDay(Date day) {
        this.day = day;
    }

    public static WeekDay valueOf(Date day) {
        Validate.isNonNull(day, "day");

        return new WeekDay(day);
    }

    public static WeekDay valueOf(long day) {
        return new WeekDay(new Date(day));
    }

    public static WeekDay valueOf(String day) {
        Validate.isNonNull(day, "day");

        try {
            return new WeekDay(ACCEPTED_FORMAT_FOR_PARSING.parse(day));
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public int compareTo(WeekDay other) {
        return day.compareTo(other.day);
    }

    public Date toDate() {
        return new Date(toLong());
    }

    public long toLong() {
        return day.getTime();
    }

    @Override
    public String toString() {
        return ACCEPTED_FORMAT_FOR_PARSING.format(day);
    }

    public String toString(DateFormat format) {
        Validate.isNonNull(format, "format");

        return format.format(day);
    }

    @Override
    public int hashCode() {
        return day.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WeekDay && day.equals(((WeekDay) obj).day);
    }
}
