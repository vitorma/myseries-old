package mobi.myseries.test.unit.application;

import mobi.myseries.application.Communications;
import mobi.myseries.application.CommunicationsImpl;
import mobi.myseries.application.ConnectionFailedException;
import android.test.InstrumentationTestCase;

public class CommunicationsImplTest extends InstrumentationTestCase {

    private Communications communications;

    @Override
    public void setUp() {  
        this.communications = new CommunicationsImpl(this.getInstrumentation().getContext());
    }

    public void testItDoesNotAllowNullUrls() throws ConnectionFailedException {
        try {
            communications.streamFor(null);
            fail("Should have thrown an exception");
        } catch (IllegalArgumentException e) {}
    }

    public void testItDoesNotAllowBlankUrls() throws ConnectionFailedException {
        try {
            communications.streamFor("    \t\t \n  ");
            fail("Should have thrown an exception");
        } catch (IllegalArgumentException e) {}
    }

    public void testItDoesNotAllowInvalidUrls() {
        try {
            communications.streamFor("http://.com/");
            fail("Should have thrown an exception");
        } catch (ConnectionFailedException e) {}
    }

    public void testItDoesNotAllowInvalidUrls2() {
        try {
            communications.streamFor("http://lasdxkjdsh.com/");
            fail("Should have thrown an exception");
        } catch (ConnectionFailedException e) {}
    }

    public void testItAllowsValidUrls() throws ConnectionFailedException {
        communications.streamFor("http://google.com/");
    }
}
