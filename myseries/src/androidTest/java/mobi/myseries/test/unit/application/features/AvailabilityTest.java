package mobi.myseries.test.unit.application.features;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import mobi.myseries.application.features.product.Availability;
import mobi.myseries.application.features.product.Price;
import android.test.AndroidTestCase;

public class AvailabilityTest extends AndroidTestCase {

    private static final Availability notAvailable = Availability.NotAvailable;

    private static final String priceA = "$1.00";
    private static final String priceB = "$2.00";

    private static final Price pA1 = new Price(priceA);
    private static final Price pA2 = new Price(priceA);
    private static final Price pB = new Price(priceB);

    /* Test assumptions */

    public void testAsAreEquals() {
        assertThat(pA1, equalTo(pA2));
    }

    public void testAIsNotEqualToB() {
        assertThat(pA1, not(equalTo(pB)));
    }

    /* Constructor */

    public void testItCannotBeInstantiatedWithNullPrice() {
        try {
            new Availability(null, true);
            fail("It should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    /* NotAvailable */

    public void testNotAvailableIsNotOwned() {
        assertThat(notAvailable.isOwned(), is(false));
    }

    public void testNotAvailableDoesntHavePrice() {
        assertThat(notAvailable.price().isAvailable(), is(false));
    }

    /* Equal and hashCode tests*/

    public void testNotAvailableIsEqualToNotAvailable() {
        assertThat(Availability.NotAvailable, equalTo(Availability.NotAvailable));
    }

    public void testAvailabilitiesAreEqualsIfPricesAndOwnershipAreEquals() {
        assertThat(new Availability(pA1, true), equalTo(new Availability(pA2, true)));
    }

    public void testEqualAvailabilitiesHaveEqualHashCodes() {
        assertThat(new Availability(pA1, true).hashCode(),
           equalTo(new Availability(pA2, true).hashCode()));
    }

    public void testAvailabilitiesAreNotEqualsIfOwnershipAreNotEquals() {
        assertThat(new Availability(pA1, true), not(equalTo(new Availability(pA2, false))));
    }

    public void testAvailabilitiesAreNotEqualsIfPricesAreNotEquals() {
        assertThat(new Availability(pA1, true), not(equalTo(new Availability(pB, true))));
    }

    public void testAvailabilityIsNeverEqualToNull() {
        assertThat(new Availability(pA1, true), not(equalTo(null)));
    }
}
