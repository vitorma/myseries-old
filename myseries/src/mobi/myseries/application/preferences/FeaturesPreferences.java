package mobi.myseries.application.preferences;

import com.google.gson.Gson;

import mobi.myseries.R;
import mobi.myseries.application.features.features.FeaturesPersistence.State;
import android.content.Context;

public class FeaturesPreferences extends BasePreferences {

    private final Gson gson;

    public FeaturesPreferences(Context context) {
        super(context);

        this.gson = new Gson();
    }

    public synchronized void putState(State state) {
        putString(key(R.string.prefKey_features_state), serialized(state));
    }

    public synchronized State state() {
        return deserialized(getString(key(R.string.prefKey_features_state), null));
    }

    private String serialized(State state) {
        return this.gson.toJson(state);
    }

    private State deserialized(String serializedState) {
        return this.gson.fromJson(serializedState, State.class);
    }
}
