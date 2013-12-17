package mobi.myseries.application.features.features;

import mobi.myseries.application.features.features.FeaturesPersistence.State;
import mobi.myseries.application.preferences.FeaturesPreferences;
import mobi.myseries.shared.Validate;

/**
 * Adapter for FeaturesPreferences implementing FeaturesPersistenceBackend interface.
 */
public class SharedPreferencesFeaturesPersistenceBackend implements FeaturesPersistenceBackend {

    private final FeaturesPreferences featuresPreferences;

    public SharedPreferencesFeaturesPersistenceBackend(FeaturesPreferences featuresPreferences) {
        Validate.isNonNull(featuresPreferences, "featuresPreferences");
        this.featuresPreferences = featuresPreferences;
    }

    @Override
    public void save(State newState) {
        this.featuresPreferences.putState(newState);
    }

    @Override
    public State retrieve() {
        return this.featuresPreferences.state();
    }
}
