package mobi.myseries.shared;

public class WeekTime {

    private static final int MILISECONDS_IN_A_MINUTE = 60 * 1000;

    private static final long MILISECONDS_IN_AN_HOUR = 60 * MILISECONDS_IN_A_MINUTE;

    private static final long MILISECONDS_IN_A_DAY = 24 * MILISECONDS_IN_AN_HOUR;

    private final WeekDay day;
    private final Time time;

    public WeekTime(WeekDay day, Time time) {
        Validate.isNonNull(day, "day");
        Validate.isNonNull(time, "time");

        this.day = day;
        this.time = time;
    }

    public WeekTime plusHours(long i) {
        return plusMinutes(60 * i);
    }

    public Time time() {
        return time;
    }

    public WeekDay weekday() {
        return day;
    }

    public WeekTime plusDays(int i) {
        return plusHours(24 * i);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WeekTime)) {
            return false;
        }

        WeekTime other = (WeekTime) o;

        // TODO(Reul): WeekDay equals should return true if both represent the
        // same day in the week
        return day.toString().equals(other.day.toString()) && time.toString().equals(other.time.toString());
    }

    public WeekTime plusMinutes(long i) {
        long time = (day.toLong() / MILISECONDS_IN_A_DAY) * MILISECONDS_IN_A_DAY + (this.time.toLong() % MILISECONDS_IN_A_DAY) + i
                * MILISECONDS_IN_A_MINUTE;

        return new WeekTime(WeekDay.valueOf(time - time % MILISECONDS_IN_A_DAY), Time.valueOf(time % MILISECONDS_IN_A_DAY));
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }

}
