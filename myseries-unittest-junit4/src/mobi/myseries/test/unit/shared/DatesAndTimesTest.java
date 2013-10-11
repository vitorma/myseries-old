package mobi.myseries.test.unit.shared;

import java.util.Date;
import java.util.TimeZone;

import mobi.myseries.shared.DatesAndTimes;

import org.junit.Assert;
import org.junit.Test;

public class DatesAndTimesTest {

    @Test
    public void testToLocalTimeDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Recife"));
        Date date1 = new Date(System.currentTimeMillis());
        Date date2 = new Date(date1.getTime() - 3 * 60 * 60 * 1000);

        //Assert.assertEquals(date2, DatesAndTimes.toLocalTime(date1));

    }

}
