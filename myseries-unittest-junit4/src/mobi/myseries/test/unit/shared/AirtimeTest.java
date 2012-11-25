package mobi.myseries.test.unit.shared;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import junit.framework.Assert;
import mobi.myseries.shared.Airtime;

import org.junit.Test;

public class AirtimeTest {
    private static final DateFormat FORMAT = new SimpleDateFormat("hh:mm aa");

    @Test(expected=IllegalArgumentException.class)
    public void valueOfNullStringCausesIllegalArgumentException() {
        Airtime.valueOf(null);
    }

    @Test
    public void valueOfBadFormattedStringIsNull() {
        Assert.assertNull(Airtime.valueOf(""));
        Assert.assertNull(Airtime.valueOf("           "));
        Assert.assertNull(Airtime.valueOf("01"));
        Assert.assertNull(Airtime.valueOf("1"));
        Assert.assertNull(Airtime.valueOf("2010/01/01 01:00 am"));
    }

    @Test
    public void valueOfStringRepresentingInvalidTimeIsNull() {
        Assert.assertNull(Airtime.valueOf("24:00"));
        Assert.assertNull(Airtime.valueOf("1:60"));
        Assert.assertNull(Airtime.valueOf("01:60"));
        Assert.assertNull(Airtime.valueOf("01:60 am"));
    }

    @Test
    public void valueOfWellFormattedStringRepresentingValidTimeIsTheRightTime() {
        //hh:mm aa
        Assert.assertEquals(Airtime.valueOf("01:30 AM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("12:30 AM").toString(FORMAT), "12:30 AM");
        Assert.assertEquals(Airtime.valueOf("01:30 PM").toString(FORMAT), "01:30 PM");
        Assert.assertEquals(Airtime.valueOf("12:30 PM").toString(FORMAT), "12:30 PM");

        //h:mm aa
        Assert.assertEquals(Airtime.valueOf("1:30 AM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("1:30 PM").toString(FORMAT), "01:30 PM");

        //hh:mmaa
        Assert.assertEquals(Airtime.valueOf("01:30AM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("12:30AM").toString(FORMAT), "12:30 AM");
        Assert.assertEquals(Airtime.valueOf("01:30PM").toString(FORMAT), "01:30 PM");
        Assert.assertEquals(Airtime.valueOf("12:30PM").toString(FORMAT), "12:30 PM");

        //h:mmaa
        Assert.assertEquals(Airtime.valueOf("1:30AM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("1:30PM").toString(FORMAT), "01:30 PM");

        //hh aa
        Assert.assertEquals(Airtime.valueOf("01 AM").toString(FORMAT), "01:00 AM");
        Assert.assertEquals(Airtime.valueOf("12 AM").toString(FORMAT), "12:00 AM");
        Assert.assertEquals(Airtime.valueOf("01 PM").toString(FORMAT), "01:00 PM");
        Assert.assertEquals(Airtime.valueOf("12 PM").toString(FORMAT), "12:00 PM");

        //h aa
        Assert.assertEquals(Airtime.valueOf("1 AM").toString(FORMAT), "01:00 AM");
        Assert.assertEquals(Airtime.valueOf("1 PM").toString(FORMAT), "01:00 PM");

        //hhaa
        Assert.assertEquals(Airtime.valueOf("01AM").toString(FORMAT), "01:00 AM");
        Assert.assertEquals(Airtime.valueOf("12AM").toString(FORMAT), "12:00 AM");
        Assert.assertEquals(Airtime.valueOf("01PM").toString(FORMAT), "01:00 PM");
        Assert.assertEquals(Airtime.valueOf("12PM").toString(FORMAT), "12:00 PM");

        //haa
        Assert.assertEquals(Airtime.valueOf("1AM").toString(FORMAT), "01:00 AM");
        Assert.assertEquals(Airtime.valueOf("1PM").toString(FORMAT), "01:00 PM");

        //HH:mm
        Assert.assertEquals(Airtime.valueOf("01:30").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("12:30").toString(FORMAT), "12:30 PM");
        Assert.assertEquals(Airtime.valueOf("13:30").toString(FORMAT), "01:30 PM");
        Assert.assertEquals(Airtime.valueOf("00:30").toString(FORMAT), "12:30 AM");

        //H:mm
        Assert.assertEquals(Airtime.valueOf("1:30").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("0:30").toString(FORMAT), "12:30 AM");

        //Strings with seconds
        Assert.assertEquals(Airtime.valueOf("01:30:30 AM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("01:30:30AM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("1:30:30 AM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("01:30:30").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("1:30:30").toString(FORMAT), "01:30 AM");
    }

    @Test
    public void ValueOfStringIgnoresCaseOfAMPM() {
        Assert.assertEquals(Airtime.valueOf("01:30 AM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("01:30 am").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("01:30 aM").toString(FORMAT), "01:30 AM");
        Assert.assertEquals(Airtime.valueOf("01:30 Am").toString(FORMAT), "01:30 AM");
    }

    @Test(expected=IllegalArgumentException.class)
    public void toStringWithNullFormatCausesIllegalArgumentException() {
        Airtime a = Airtime.valueOf("08:30 PM");

        Assert.assertNotNull(a);

        a.toString(null);
    }
}
