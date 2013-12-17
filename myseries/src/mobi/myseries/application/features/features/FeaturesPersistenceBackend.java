package mobi.myseries.application.features.features;

import mobi.myseries.application.features.features.FeaturesPersistence.State;

public interface FeaturesPersistenceBackend {
    public void save(State newState);
    public State retrieve();
}