package mobi.myseries.test.unit.application.features;

import java.security.SecureRandom;
import java.util.Arrays;

import android.test.AndroidTestCase;
import mobi.myseries.application.features.Feature;
import mobi.myseries.application.features.FeaturesPersistence.State;
import mobi.myseries.application.features.FeaturesPersistenceBackend;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public abstract class FeaturesPersistenceBackendTest extends AndroidTestCase {

    private static SecureRandom randomNumerGenerator = new SecureRandom();

    private FeaturesPersistenceBackend backend;

    protected abstract FeaturesPersistenceBackend newPersistenceBackend();

    private State newRandomState() {
        State newState = new State();

        newState.nonce = randomNumerGenerator.nextInt();
        newState.features = Arrays.asList(Feature.SCHEDULE_WIDGET, Feature.CLOUD_BACKUP);
        newState.base64Signature = Long.toString(randomNumerGenerator.nextLong());

        return newState;
    }

    @Override
    public void setUp() {
        this.backend = newPersistenceBackend();
    }

    public void testItCanSaveNullState() {
        this.backend.save(null);
        assertThat(this.backend.retrieve(), is(nullValue()));
    }

    public void testTheSavedContentMustBeRetrieved() {
        State savedState = newRandomState();
        this.backend.save(savedState);

        State retrievedState = this.backend.retrieve();

        assertThat(retrievedState.nonce, equalTo(savedState.nonce));
        assertThat(retrievedState.features, equalTo(savedState.features));
        assertThat(retrievedState.base64Signature, equalTo(savedState.base64Signature));
    }
}
