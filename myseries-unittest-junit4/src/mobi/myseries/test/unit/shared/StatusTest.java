package mobi.myseries.test.unit.shared;

import mobi.myseries.shared.Status;

import org.junit.Assert;
import org.junit.Test;

public class StatusTest {

    @Test(expected=IllegalArgumentException.class)
    public void statusFromNullStringCausesIllegalArgumentException() {
        Status.from(null);
    }

    @Test
    public void statusFromStringWithoutMatchedStatusNameIsUnknown() {
        Assert.assertEquals(Status.UNKNOWN, Status.from(""));
        Assert.assertEquals(Status.UNKNOWN, Status.from("        \t      \n     "));
        Assert.assertEquals(Status.UNKNOWN, Status.from("continuin"));
        Assert.assertEquals(Status.UNKNOWN, Status.from("continuando"));
        Assert.assertEquals(Status.UNKNOWN, Status.from("ende"));
        Assert.assertEquals(Status.UNKNOWN, Status.from("encerrado"));
    }

    @Test
    public void statusFromStringWithMatchedStatusNameIsTheMatchedStatus() {
        Assert.assertEquals(Status.CONTINUING, Status.from("Continuing"));
        Assert.assertEquals(Status.CONTINUING, Status.from("continuing"));
        Assert.assertEquals(Status.CONTINUING, Status.from("CONTINUING"));
        Assert.assertEquals(Status.CONTINUING, Status.from("cONtinuinG"));

        Assert.assertEquals(Status.ENDED, Status.from("Ended"));
        Assert.assertEquals(Status.ENDED, Status.from("ended"));
        Assert.assertEquals(Status.ENDED, Status.from("ENDED"));
        Assert.assertEquals(Status.ENDED, Status.from("EnDEd"));

        Assert.assertEquals(Status.UNKNOWN, Status.from("Unknown"));
        Assert.assertEquals(Status.UNKNOWN, Status.from("unknown"));
        Assert.assertEquals(Status.UNKNOWN, Status.from("UNKNOWN"));
        Assert.assertEquals(Status.UNKNOWN, Status.from("uNKNOwn"));
    }
}
