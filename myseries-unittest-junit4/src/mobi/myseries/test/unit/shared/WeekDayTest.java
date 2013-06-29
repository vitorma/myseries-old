package mobi.myseries.test.unit.shared;

import java.util.Locale;

import mobi.myseries.shared.WeekDay;

import org.junit.Assert;
import org.junit.Test;

public class WeekDayTest {

    @Test(expected=IllegalArgumentException.class)
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

    @Test
    public void valueOfStringStartingWithValidWeekDayIsTheRightWeekDay() {
        Assert.assertEquals(WeekDay.valueOf("Sun").toString(Locale.US), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sun").toShortString(Locale.US), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sun").toString(new Locale("pt", "BR")), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sun").toShortString(new Locale("pt", "BR")), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sund").toString(Locale.US), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sund").toShortString(Locale.US), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sund").toString(new Locale("pt", "BR")), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sund").toShortString(new Locale("pt", "BR")), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sunda").toString(Locale.US), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sunda").toShortString(Locale.US), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sunda").toString(new Locale("pt", "BR")), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sunda").toShortString(new Locale("pt", "BR")), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sunday").toString(Locale.US), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sunday").toShortString(Locale.US), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sunday").toString(new Locale("pt", "BR")), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sunday").toShortString(new Locale("pt", "BR")), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sunday         ").toString(Locale.US), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sunday         ").toShortString(Locale.US), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sunday         ").toString(new Locale("pt", "BR")), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sunday         ").toShortString(new Locale("pt", "BR")), "Dom");

        Assert.assertEquals(WeekDay.valueOf("SundayXyz0123").toString(Locale.US), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("SundayXyz0123").toShortString(Locale.US), "Sun");
        Assert.assertEquals(WeekDay.valueOf("SundayXyz0123").toString(new Locale("pt", "BR")), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("SundayXyz0123").toShortString(new Locale("pt", "BR")), "Dom");

        Assert.assertEquals(WeekDay.valueOf("Sunday         Xyz0123").toString(Locale.US), "Sunday");
        Assert.assertEquals(WeekDay.valueOf("Sunday         Xyz0123").toShortString(Locale.US), "Sun");
        Assert.assertEquals(WeekDay.valueOf("Sunday         Xyz0123").toString(new Locale("pt", "BR")), "Domingo");
        Assert.assertEquals(WeekDay.valueOf("Sunday         Xyz0123").toShortString(new Locale("pt", "BR")), "Dom");
    }

    @Test(expected=IllegalArgumentException.class)
    public void toStringWithNullLocaleCausesIllegalArgumentException() {
        WeekDay day = WeekDay.valueOf("Sunday");

        Assert.assertNotNull(day);

        day.toString(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void toShortStringWithNullLocaleCausesIllegalArgumentException() {
        WeekDay day = WeekDay.valueOf("Sunday");

        Assert.assertNotNull(day);

        day.toShortString(null);
    }
}
