package mobi.myseries.application.preferences;

import android.util.Log;

public class UpdatePreferences {
    private static final String ENABLE_AUTOMATIC_UPDATES_KEY = "automaticUpdateEnabled";
    private static final String ENABLE_UPDATES_ON_DATAPLAN_KEY = "updateOnDataPlanEnabled";

    private PrimitivePreferences primitive;

    public UpdatePreferences(PrimitivePreferences primitive) {
        this.primitive = primitive;
    }

    public boolean updateAutomatically() {
        return this.primitive.getBoolean(ENABLE_AUTOMATIC_UPDATES_KEY, true);
    }

    public boolean updateOnDataPlan() {
        return this.primitive.getBoolean(ENABLE_UPDATES_ON_DATAPLAN_KEY, true);
    }

    public UpdatePreferences putUpdateAutomatically(boolean updateAutomatically) {
        Log.d(getClass().getName(), "Setting update automatically preference to " + updateAutomatically);
        this.primitive.putBoolean(ENABLE_AUTOMATIC_UPDATES_KEY, updateAutomatically);
        return this;
    }

    public UpdatePreferences putUpdateOnDataPlan(boolean updateOnDataPlan) {
        Log.d(getClass().getName(), "Setting update on data plan preference to " + updateOnDataPlan);
        this.primitive.putBoolean(ENABLE_UPDATES_ON_DATAPLAN_KEY, updateOnDataPlan);
        return this;
    }
}
