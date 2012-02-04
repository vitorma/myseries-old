package mobi.myseries.test;

import mobi.myseries.ATestClass;
import junit.framework.Assert;
import junit.framework.TestCase;

public class AExampleOfTest extends TestCase {

    public void testAExampleOfTest() {
        Assert.assertNull(new ATestClass().testString);
    }
}
