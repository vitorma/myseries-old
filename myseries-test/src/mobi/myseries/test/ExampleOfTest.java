package mobi.myseries.test;

import mobi.myseries.ATestClass;
import junit.framework.Assert;
import junit.framework.TestCase;

public class ExampleOfTest extends TestCase {

    public void testExample() {
        Assert.assertNull(new ATestClass().testString);
    }
}
