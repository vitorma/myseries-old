package mobi.myseries.test.unit.application.features;

import mobi.myseries.application.features.ProductId;
import android.test.AndroidTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class ProductIdTest<I extends ProductId> extends AndroidTestCase {

    protected abstract I newProductA1();
    protected abstract I newProductA2();
    protected abstract I newProductA3();
    protected abstract I newProductB();

    /* Test assumptions */

    public void testAIsNotEqualToB() {
        assertThat(newProductA1(), not(equalTo(newProductB())));
    }

    public void testBuildersAreIdempotent() {
        for (int i = 0; i < 3; ++i) {
            assertThat(newProductA1(), equalTo(newProductA1()));
            assertThat(newProductA2(), equalTo(newProductA2()));
            assertThat(newProductA3(), equalTo(newProductA3()));
            assertThat(newProductB(), equalTo(newProductB()));
        }
    }

    /* Equal and hashCode tests*/

    public void testReflexivity() {
        ProductId p = newProductA1();
        assertThat(p, equalTo(p));
    }

    public void testSimmetry() {
        ProductId p1 = newProductA1();
        ProductId p2 = newProductA2();

        assertThat(p1, equalTo(p2));
        assertThat(p2, equalTo(p1));
    }

    public void testTransitivity() {
        ProductId p1 = newProductA1();
        ProductId p2 = newProductA2();
        ProductId p3 = newProductA3();

        assertThat(p1, equalTo(p2));
        assertThat(p2, equalTo(p3));

        assertThat(p1, equalTo(p3));
    }

    public void testOtherClass() {
        ProductId testProductId = new ProductId() {};
        assertThat(newProductA1(), not(equalTo(testProductId)));
    }

    public void testNull() {
        assertThat(newProductA1(), not(equalTo(null)));
    }

    public void testHashCode() {
        ProductId p1 = newProductA1();
        ProductId p2 = newProductA2();
        ProductId p3 = newProductA3();

        assertThat(p1.hashCode(), equalTo(p2.hashCode()));
        assertThat(p1.hashCode(), equalTo(p3.hashCode()));
    }
}
