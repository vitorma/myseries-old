package mobi.myseries.test.unit.application.features;

import mobi.myseries.application.features.product.Sku;
import android.test.AndroidTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SkuTest extends AndroidTestCase {

    private static final String productA = "Product A";
    private static final String productB = "Product B";

    private static final Sku sA1 = new Sku(productA);
    private static final Sku sA2 = new Sku(productA);
    private static final Sku sA3 = new Sku(productA);
    private static final Sku sB = new Sku(productB);

    /* Test assumptions */

    public void testAIsNotEqualToB() {
        assertThat(sA1, not(equalTo(sB)));
    }

    /* Equal and hashCode tests*/

    public void testReflexivity() {
        assertThat(sA1, equalTo(sA1));
    }

    public void testSimmetry() {
        assertThat(sA1, equalTo(sA2));
        assertThat(sA2, equalTo(sA1));

        assertThat(sA1, not(equalTo(sB)));
        assertThat(sB, not(equalTo(sA1)));
    }

    public void testTransitivity() {
        assertThat(sA1, equalTo(sA2));
        assertThat(sA2, equalTo(sA3));

        assertThat(sA1, equalTo(sA3));
    }

    public void testOtherClass() {
        String testSku = "testSku";
        assertFalse(sA1.equals(testSku));
    }

    public void testNull() {
        assertThat(sA1, not(equalTo(null)));
    }

    public void testHashCode() {
        assertThat(sA1.hashCode(), equalTo(sA2.hashCode()));
        assertThat(sA1.hashCode(), equalTo(sA3.hashCode()));
    }
}
