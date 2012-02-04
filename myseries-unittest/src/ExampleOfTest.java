import mobi.myseries.ATestClass;

import org.junit.Assert;
import org.junit.Test;

public class ExampleOfTest {

    @Test
    public void exampleOfTest() {
        Assert.assertNull((new ATestClass()).testString);
        //Assert.fail("Unit test fail");
    }
}
