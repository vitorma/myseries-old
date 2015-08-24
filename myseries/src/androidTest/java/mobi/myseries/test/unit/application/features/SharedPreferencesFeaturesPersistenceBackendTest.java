package mobi.myseries.test.unit.application.features;

import mobi.myseries.application.features.features.FeaturesPersistenceBackend;
import mobi.myseries.application.features.features.SharedPreferencesFeaturesPersistenceBackend;
import mobi.myseries.application.preferences.Preferences;

public class SharedPreferencesFeaturesPersistenceBackendTest extends FeaturesPersistenceBackendTest {

    @Override
    protected FeaturesPersistenceBackend newPersistenceBackend() {
        return new SharedPreferencesFeaturesPersistenceBackend(new Preferences(this.getContext()).forFeatures());
    }

    public void testItDoesntAllowNullPreferences() {
        try {
            new SharedPreferencesFeaturesPersistenceBackend(null);
            fail("It should have thrown an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }
}
