package mobi.myseries.shared;

import java.util.Date;

public class RelativeDay {
    private int daysSinceToday;

    private RelativeDay(Date date) {
        this.daysSinceToday = DatesAndTimes.daysBetween(DatesAndTimes.today(), date);
    }

    public static RelativeDay valueOf(Date date) {
        Validate.isNonNull(date, "date");

        return new RelativeDay(date);
    }

    public int daysSinceToday() {
        return this.daysSinceToday;
    }

    public int daysUntilToday() {
        return -this.daysSinceToday;
    }

    public boolean isToday() {
        return this.daysSinceToday == 0;
    }

    public boolean isYesterday() {
        return this.daysSinceToday == -1;
    }

    public boolean isTomorrow() {
        return this.daysSinceToday == 1;
    }

    public boolean isFuture() {
        return this.daysSinceToday >= 1;
    }

    public boolean isPast() {
        return this.daysSinceToday <= -1;
    }

    public boolean wasLessThanAWeekAgo() {
        return this.isPast() && this.daysUntilToday() < DatesAndTimes.DAYS_IN_A_WEEK;
    }

    public boolean isInLessThanAWeek() {
        return this.isFuture() && this.daysSinceToday() < DatesAndTimes.DAYS_IN_A_WEEK;
    }
}
