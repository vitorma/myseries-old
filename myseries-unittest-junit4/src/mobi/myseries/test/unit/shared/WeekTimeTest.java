package mobi.myseries.test.unit.shared;

import mobi.myseries.shared.Time;
import mobi.myseries.shared.WeekDay;
import mobi.myseries.shared.WeekTime;

import org.junit.Assert;
import org.junit.Test;

public class WeekTimeTest {
    private static final boolean AM = false;
    private static final boolean PM = true;

    @Test
    public void testIsImmutable() {
        Time t1 = time(1, 0, false);
        WeekDay w1 = weekday(1);

        WeekTime weektime = new WeekTime(w1, t1);
        WeekTime weektime2 = weektime.plusHours(1);
        weektime2.time();

        Assert.assertNotSame(weektime, weektime2);
        Assert.assertEquals(t1, weektime.time());
    }

    @Test
    public void testPlusDays() {
        WeekTime w1 = new WeekTime(weekday(1), time(1, 0, AM));
        WeekTime w2 = new WeekTime(weekday(2), time(1, 0, AM));

        Assert.assertEquals(w1, w1.plusDays(0));
        Assert.assertEquals(w2, w1.plusDays(1));

        Assert.assertEquals(w2, w2.plusDays(0));
        Assert.assertEquals(w1, w2.plusDays(-1));


        Assert.assertEquals(new WeekTime(weekday(7), time(1, 0, AM)), w1.plusDays(6));
        Assert.assertEquals(new WeekTime(weekday(6), time(1, 0, AM)), w1.plusDays(5));

        Assert.assertEquals(new WeekTime(weekday(5), time(1, 0, AM)), new WeekTime(weekday(1), time(1, 0, AM)).plusDays(-3));

        Assert.assertEquals(w1, w1.plusDays(7));
        Assert.assertEquals(w1, w1.plusDays(7).plusDays(-7));

        Assert.assertEquals(w1, w1.plusDays(14));
        Assert.assertEquals(w2, w1.plusDays(-13).plusDays(14));

        Assert.assertEquals(w1, w1.plusDays(21));
        Assert.assertEquals(w2, w1.plusDays(-20).plusDays(21));

        Assert.assertEquals(w1, w1.plusDays(28));
        Assert.assertEquals(w2, w1.plusDays(-27).plusDays(28));

        Assert.assertEquals(new WeekTime(weekday(7), time(1, 0, AM)), new WeekTime(weekday(1), time(1, 0, AM)).plusDays(-1));
    }

    @Test
    public void testPlusMinutes() {
        WeekTime w1 = new WeekTime(weekday(1), time(1, 0, AM));
        WeekTime w2 = new WeekTime(weekday(1), time(1, 1, AM));

        Assert.assertEquals(w2, w1.plusMinutes(1));
        Assert.assertEquals(w1, w2.plusMinutes(-1));

        Assert.assertEquals(time(0, 0, AM), w1.plusMinutes(-60).time());
        Assert.assertEquals(weekday(1), w1.plusMinutes(-60).weekday());

        Assert.assertEquals(time(1, 0, AM), w1.plusMinutes(24 * 60).time());
        Assert.assertEquals(weekday(2), w1.plusMinutes(24 * 60).weekday());
    }

    @Test
    public void testPlusHours() {
        WeekTime w1 = new WeekTime(weekday(1), time(1, 0, AM));
        WeekTime w2 = new WeekTime(weekday(1), time(2, 0, AM));

        Assert.assertEquals(w2, w1.plusHours(1));
        Assert.assertEquals(w1, w2.plusHours(-1));

        Assert.assertEquals(time(0, 0, AM), w1.plusHours(-1).time());
        Assert.assertEquals(weekday(1), w1.plusHours(-1).weekday());

        Assert.assertEquals(time(23, 0, PM), w1.plusHours(-2).time());
        Assert.assertEquals(weekday(7), w1.plusHours(-2).weekday());

        Assert.assertEquals(time(1, 0, AM), w1.plusHours(24).time());
        Assert.assertEquals(weekday(2), w1.plusHours(24).weekday());

        Assert.assertEquals(time(11, 0, PM), w1.plusHours(22).time());
        Assert.assertEquals(weekday(1), w1.plusHours(22).weekday());
    }

    private Time time(int h, int m, boolean pm) {
        return Time.valueOf(String.format("%2d:%2d ".concat(pm ? "PM" : "AM"), h, m));
    }

    private WeekDay weekday(int d) {
        switch (d) {
        case 1:
            return WeekDay.valueOf("Sun");
        case 2:
            return WeekDay.valueOf("Mon");
        case 3:
            return WeekDay.valueOf("Tue");
        case 4:
            return WeekDay.valueOf("Wed");
        case 5:
            return WeekDay.valueOf("Thu");
        case 6:
            return WeekDay.valueOf("Fri");
        case 7:
            return WeekDay.valueOf("Sat");
        default:
            throw new RuntimeException("Invalid week day");
        }
    }
}
