package mobi.myseries.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeekDay implements Comparable<WeekDay> {
    private static final String PATTERN_SHORT = "EEE";
    private static final String PATTERN_FULL = "EEEE";

    private static final DateFormat ACCEPTED_FORMAT_FOR_PARSING = new SimpleDateFormat("E", Locale.US);

    static {
        ACCEPTED_FORMAT_FOR_PARSING.setLenient(false);
    }

    private Date day;

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
        return this.day.compareTo(other.day);
    }

    public Date toDate() {
        return new Date(this.toLong());
    }

    public long toLong() {
        return this.day.getTime();
    }

    @Override
    public String toString() {
        return ACCEPTED_FORMAT_FOR_PARSING.format(this.day);
    }

    public String toString(Locale locale) {
        Validate.isNonNull(locale, "locale");

        return this.toString(new SimpleDateFormat(PATTERN_FULL, locale));
    }

    public String toShortString(Locale locale) {
        Validate.isNonNull(locale, "locale");

        return this.toString(new SimpleDateFormat(PATTERN_SHORT, locale));
    }

    private String toString(DateFormat format) {
        return format.format(this.day);
    }

    @Override
    public int hashCode() {
        return this.day.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WeekDay && this.day.equals(((WeekDay) obj).day);
    }
}
