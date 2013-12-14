package mobi.myseries.application.features;

import mobi.myseries.application.features.FeaturesPersistence.State;

public class AndroidFeaturesPersistenceBackend implements FeaturesPersistence.PersistenceBackend {

    private State savedState;

    @Override
    public void saveState(State newState) {
        // TODO Auto-generated method stub
        this.savedState = newState;
    }

    @Override
    public State retrieveState() {
        // TODO Auto-generated method stub
        return this.savedState;
    }

}
