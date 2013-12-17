package mobi.myseries.test.unit.application.features;

import static org.mockito.Mockito.*;

import mobi.myseries.application.features.features.Features;
import mobi.myseries.application.features.store.Store;
import mobi.myseries.application.preferences.Preferences;
import android.test.AndroidTestCase;

public class FeaturesTest extends AndroidTestCase {

    // Constructor

    public void testItDoesntWorkWithNullStore() {
        try {
            Preferences preferences = mock(Preferences.class);
            new Features(null, preferences);
            fail("It should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testItDoesntWorkWithNullPreferences() {
        try {
            Store store = mock(Store.class);
            new Features(store, null);
            fail("It should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    // isEnabled

    public void testNullFeaturesAreNeverEnabled() {
        Store store = mock(Store.class);
        Preferences preferences = mock(Preferences.class);
        Features features = new Features(store, preferences);

        assertFalse(features.isEnabled(null));
    }
}
