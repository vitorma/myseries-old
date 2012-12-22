package mobi.myseries.test.unit.shared;

import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.RelativeDay;

import org.junit.Assert;
import org.junit.Test;

public class RelativeDayTest {

    @Test(expected=IllegalArgumentException.class)
    public void valueOfNullDateCausesIllegalArgumentException() {
        RelativeDay.valueOf(null);
    }

    public void valueOfTodayDateIsToday() {
        RelativeDay today = RelativeDay.valueOf(DatesAndTimes.today());

        Assert.assertTrue(today.isToday());
        Assert.assertFalse(today.isYesterday());
        Assert.assertFalse(today.isTomorrow());
        Assert.assertFalse(today.isPast());
        Assert.assertFalse(today.isFuture());
        Assert.assertFalse(today.wasLessThanAWeekAgo());
        Assert.assertFalse(today.isInLessThanAWeek());

        Assert.assertEquals(0, today.daysSinceToday());
        Assert.assertEquals(0, today.daysUntilToday());
    }

    //TODO (Cleber) Test valueOf with:
    //              - the date of yesterday
    //              - the date of tomorrow
    //              - the date of 6 days ago
    //              - the date of 6 days in future
    //              - the date of 7 days ago
    //              - the date of 7 days in future
    //              Tip: create other useful methods in DatesAndTimes to retrieve these dates
}
