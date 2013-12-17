package mobi.myseries.test.unit.application.features;

import mobi.myseries.application.features.features.DummyFeaturesPersistenceBackend;
import mobi.myseries.application.features.features.FeaturesPersistenceBackend;

public class DummyFeaturesPersistenceBackendTest extends FeaturesPersistenceBackendTest {

    @Override
    protected FeaturesPersistenceBackend newPersistenceBackend() {
        return new DummyFeaturesPersistenceBackend();
    }
}
