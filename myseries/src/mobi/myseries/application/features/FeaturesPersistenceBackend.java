package mobi.myseries.application.features;

import mobi.myseries.application.features.FeaturesPersistence.State;

public interface FeaturesPersistenceBackend {
    public void save(State newState);
    public State retrieve();
}