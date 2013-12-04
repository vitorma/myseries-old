package mobi.myseries.test.unit.application.features;

import mobi.myseries.application.features.Price;
import android.test.AndroidTestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PriceTest extends AndroidTestCase {

    private static final String priceA = "$1.00";
    private static final String priceB = "$2.00";

    private static final Price notAvailablePrice = Price.NotAvailable;

    private static final Price pA1 = new Price(priceA);
    private static final Price pA2 = new Price(priceA);
    private static final Price pA3 = new Price(priceA);
    private static final Price pB = new Price(priceB);

    /* Test assumptions */

    public void testAIsNotEqualToB() {
        assertThat(pA1, not(equalTo(pB)));
    }

    /* Availability */

    public void testNotAvailablePriceIsNotAvailable() {
        assertThat(notAvailablePrice.isAvailable(), is(false));
    }

    public void testAvailablePriceIsAvailable() {
        assertThat(pA1.isAvailable(), is(true));
    }

    public void testNotAvailablePriceHasNotNullValue() {
        assertThat(notAvailablePrice.value(), not(nullValue()));
    }

    /* Equal and hashCode tests*/

    public void testNotAvailablePriceIsDifferentFromBlankPrice() {
        assertThat(notAvailablePrice, not(equalTo(new Price(""))));
    }

    public void testReflexivity() {
        assertThat(pA1, equalTo(pA1));
    }

    public void testSimmetry() {
        assertThat(pA1, equalTo(pA2));
        assertThat(pA2, equalTo(pA1));

        assertThat(pA1, not(equalTo(pB)));
        assertThat(pB, not(equalTo(pA1)));
    }

    public void testTransitivity() {
        assertThat(pA1, equalTo(pA2));
        assertThat(pA2, equalTo(pA3));

        assertThat(pA1, equalTo(pA3));
    }

    public void testOtherClass() {
        String testPrice = "testPrice";
        assertFalse(pA1.equals(testPrice));
    }

    public void testNull() {
        assertThat(pA1, not(equalTo(null)));
    }

    public void testHashCode() {
        assertThat(pA1.hashCode(), equalTo(pA2.hashCode()));
        assertThat(pA1.hashCode(), equalTo(pA3.hashCode()));
    }
}
