package mobi.myseries.test.unit.application.features;

import static org.mockito.Mockito.*;

import mobi.myseries.application.features.Features;
import mobi.myseries.application.features.Store;
import android.test.AndroidTestCase;

public class FeaturesTest extends AndroidTestCase {

    // Constructor

    public void testItDoesntWorkWithNullStore() {
        try {
            new Features(null);
            fail("It should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    // isEnabled

    public void testNullFeaturesAreNeverEnabled() {
        Store store = mock(Store.class);
        Features features = new Features(store);

        assertFalse(features.isEnabled(null));
    }
}
