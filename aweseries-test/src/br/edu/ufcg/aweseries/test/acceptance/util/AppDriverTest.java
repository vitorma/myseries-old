package br.edu.ufcg.aweseries.test.acceptance.util;

import junit.framework.TestCase;

public class AppDriverTest extends TestCase {

    public void testNullSoloThrowsAnException() {
        try {
            new AppDriver(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }
}
