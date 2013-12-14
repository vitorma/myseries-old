package mobi.myseries.test.unit.application.features;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import mobi.myseries.application.features.Feature;
import mobi.myseries.application.features.FeaturesPersistence;
import mobi.myseries.application.features.FeaturesPersistence.PersistenceBackend;
import mobi.myseries.application.features.FeaturesPersistence.State;
import android.test.AndroidTestCase;

public class FeaturesPersistenceTest extends AndroidTestCase {

    private DummyBackend dummyBackend;

    private class DummyBackend implements PersistenceBackend {
        public State savedState;

        @Override
        public void saveState(State newState) {
            this.savedState = newState;
        }

        @Override
        public State retrieveState() {
            return this.savedState;
        }
    }

    @Override
    public void setUp() {
        this.dummyBackend = new DummyBackend();
    }

    public void testItCannotBeInstantiatedWithNullBackend() {
        try {
            new FeaturesPersistence(null);
            fail("It should have thrown an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    public void testSavedFeaturesAreRetrieved() {
        FeaturesPersistence p = new FeaturesPersistence(dummyBackend);

        Set<Feature> savedFeatures = new HashSet<Feature>(Arrays.asList(Feature.CLOUD_BACKUP, Feature.SCHEDULE_WIDGET));
        p.save(savedFeatures);

        Set<Feature> retrievedFeatures = p.load();

        assertTrue(retrievedFeatures.containsAll(savedFeatures));
        assertTrue(savedFeatures.containsAll(retrievedFeatures));
    }

    public void testLoadingNullSavedStateReturnsEmptyFeatures() {
        FeaturesPersistence p = new FeaturesPersistence(dummyBackend);
        dummyBackend.savedState = null;

        assertTrue(p.load().isEmpty());
    }

    public void testLoadingNullSignatureOnSavedStateReturnsEmptyFeatures() {
        FeaturesPersistence p = new FeaturesPersistence(dummyBackend);

        Set<Feature> savedFeatures = new HashSet<Feature>(Arrays.asList(Feature.CLOUD_BACKUP, Feature.SCHEDULE_WIDGET));
        p.save(savedFeatures);

        dummyBackend.savedState.base64Signature = null;

        assertTrue(p.load().isEmpty());
    }

    public void testLoadingNullFeatureListOnSavedStateReturnsEmptyFeatures() {
        FeaturesPersistence p = new FeaturesPersistence(dummyBackend);

        Set<Feature> savedFeatures = new HashSet<Feature>(Arrays.asList(Feature.CLOUD_BACKUP, Feature.SCHEDULE_WIDGET));
        p.save(savedFeatures);

        dummyBackend.savedState.features = null;

        assertTrue(p.load().isEmpty());
    }

    public void testLoadingInvalidNonceOnSavedStateReturnsEmptyFeatures() {
        FeaturesPersistence p = new FeaturesPersistence(dummyBackend);

        Set<Feature> savedFeatures = new HashSet<Feature>(Arrays.asList(Feature.CLOUD_BACKUP, Feature.SCHEDULE_WIDGET));
        p.save(savedFeatures);

        dummyBackend.savedState.nonce++;

        assertTrue(p.load().isEmpty());
    }

    public void testLoadingInvalidSignatureOnSavedStateReturnsEmptyFeatures() {
        FeaturesPersistence p = new FeaturesPersistence(dummyBackend);

        Set<Feature> savedFeatures = new HashSet<Feature>(Arrays.asList(Feature.CLOUD_BACKUP, Feature.SCHEDULE_WIDGET));
        p.save(savedFeatures);

        dummyBackend.savedState.base64Signature += "invalid";

        assertTrue(p.load().isEmpty());
    }

    public void testLoadingInvalidFeatureListOnSavedStateReturnsEmptyFeatures() {
        FeaturesPersistence p = new FeaturesPersistence(dummyBackend);

        Set<Feature> savedFeatures = new HashSet<Feature>(Arrays.asList(Feature.CLOUD_BACKUP, Feature.SCHEDULE_WIDGET));
        p.save(savedFeatures);

        dummyBackend.savedState.features = dummyBackend.savedState.features.subList(0, 1);

        assertTrue(p.load().isEmpty());
    }
}
