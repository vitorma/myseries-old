package mobi.myseries.test.unit.shared;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import mobi.myseries.shared.WeekDay;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WeekDayTest {
    private SimpleDateFormat shortFormatUs;
    private SimpleDateFormat longFormatUs;
    private SimpleDateFormat shortFormatBr;
    private SimpleDateFormat longFormatBr;

    @Before
    public void setUp() {
        shortFormatUs = new SimpleDateFormat("EEE", Locale.US);
        shortFormatUs.setTimeZone(TimeZone.getTimeZone("GMT"));
        longFormatUs = new SimpleDateFormat("EEEE", Locale.US);
        longFormatUs.setTimeZone(TimeZone.getTimeZone("GMT"));
        shortFormatBr = new SimpleDateFormat("EEE", new Locale("pt", "BR"));
        shortFormatBr.setTimeZone(TimeZone.getTimeZone("GMT"));
        longFormatBr = new SimpleDateFormat("EEEE", new Locale("pt", "BR"));
        longFormatBr.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfNullStringCausesIllegalArgumentException() {
        String nullString = null;
        WeekDay.valueOf(nullString);
    }

    @Test
    public void valueOfStringNotStartingWithAtLeastThreeFirstLettersOfRightWeekDayIsNull() {
        Assert.assertNull(WeekDay.valueOf(""));
        Assert.assertNull(WeekDay.valueOf("           "));
        Assert.assertNull(WeekDay.valueOf("Sanday"));
        Assert.assertNull(WeekDay.valueOf("Su"));
        Assert.assertNull(WeekDay.valueOf("S"));
        Assert.assertNull(WeekDay.valueOf("SSunday"));
        Assert.assertNull(WeekDay.valueOf(" Sunday"));
        Assert.assertNull(WeekDay.valueOf("0"));
        Assert.assertNull(WeekDay.valueOf("1"));
    }

    @Test
    public void valueOfStringRepresentingRightWeekDayButNotInEnglishIsNull() {
        Assert.assertNull(WeekDay.valueOf("Domingo"));
        Assert.assertNull(WeekDay.valueOf("Dom"));
    }

    // TODO (Reul): Fix them.
    @Test
    public void valueOfStringStartingWithValidWeekDayIsTheRightWeekDay() {
        Assert.assertEquals(WeekDay.valueOf("Sun").toString(longFormatUs), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sun").toString(shortFormatUs), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sun").toString(longFormatBr), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sun").toString(shortFormatBr), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sund").toString(longFormatUs), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sund").toString(shortFormatUs), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sund").toString(longFormatBr), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sund").toString(shortFormatBr), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sunda").toString(longFormatUs), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sunda").toString(shortFormatUs), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sunda").toString(longFormatBr), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sunda").toString(shortFormatBr), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sunday").toString(longFormatUs), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sunday").toString(shortFormatUs), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sunday").toString(longFormatBr), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sunday").toString(shortFormatBr), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sunday         ").toString(longFormatUs), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sunday         ").toString(shortFormatUs), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sunday         ").toString(longFormatBr), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sunday         ").toString(shortFormatBr), "Dom");

        Assert.assertEquals(WeekDay.valueOf("SundayXyz0123").toString(longFormatUs), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("SundayXyz0123").toString(shortFormatUs), "Sun");
        Assert.assertEquals(WeekDay.valueOf("SundayXyz0123").toString(longFormatBr), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("SundayXyz0123").toString(shortFormatBr), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sunday         Xyz0123").toString(longFormatUs), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sunday         Xyz0123").toString(shortFormatUs), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sunday         Xyz0123").toString(longFormatBr), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sunday         Xyz0123").toString(shortFormatBr), "Dom");
    }

    @Test(expected = IllegalArgumentException.class)
    public void toStringWithNullLocaleCausesIllegalArgumentException() {
        WeekDay day = WeekDay.valueOf("Sunday");

        Assert.assertNotNull(day);

        day.toString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toShortStringWithNullLocaleCausesIllegalArgumentException() {
        WeekDay day = WeekDay.valueOf("Sunday");

        Assert.assertNotNull(day);

        day.toString(null);
    }
}
