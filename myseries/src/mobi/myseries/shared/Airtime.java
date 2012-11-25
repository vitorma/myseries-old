package mobi.myseries.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Airtime implements Comparable<Airtime> {
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

    private Date time;

    private Airtime(Date time) {
        this.time = time;
    }

    public static Airtime valueOf(long time) {
        return new Airtime(new Date(time));
    }

    public static Airtime valueOf(String time) {
        Validate.isNonNull(time, "time");

        for (DateFormat df : ACCEPTED_TIME_FORMATS) {
            try {
                return new Airtime(df.parse(time));
            } catch (ParseException e) {
                continue;
            }
        }

        return null;
    }

    @Override
    public int compareTo(Airtime other) {
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
        return obj instanceof Airtime && this.time.equals(((Airtime) obj).time);
    }
}