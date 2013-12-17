package mobi.myseries.application.features.features;

import mobi.myseries.application.features.features.FeaturesPersistence.State;

public class DummyFeaturesPersistenceBackend implements FeaturesPersistenceBackend {

    public State savedState;

    @Override
    public void save(State newState) {
        this.savedState = newState;
    }

    @Override
    public State retrieve() {
        return this.savedState;
    }
}