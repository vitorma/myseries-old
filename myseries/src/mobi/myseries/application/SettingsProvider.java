package mobi.myseries.application;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsProvider {
    private static final String PREFERENCES_NAME =
            "mobi.myseries.gui.settings.MySeriesPreferences";
    private static final String ENABLE_AUTOMATIC_UPDATES_KEY = "automaticUpdateEnabled_";
    private static final String ENABLE_UPDATES_ON_DATAPLAN_KEY = "updateOnDataPlanEnabled_";
    private Context context;

    public SettingsProvider(Context context) {
        this.context = context;
    }

    public boolean updateAutomatically() {
        return sharedPreferences(context).getBoolean(ENABLE_AUTOMATIC_UPDATES_KEY, true);
    }

    public boolean updateOnDataPlan() {
        return sharedPreferences(context).getBoolean(ENABLE_UPDATES_ON_DATAPLAN_KEY, true);
    }

    public SettingsProvider putUpdateAutomatically(boolean updateAutomatically) {
        sharedPreferences(context).edit()
                .putBoolean(ENABLE_AUTOMATIC_UPDATES_KEY, updateAutomatically).commit();
        return this;
    }

    public SettingsProvider putUpdateOnDataPlan(boolean updateOnDataPlan) {
        sharedPreferences(context).edit()
                .putBoolean(ENABLE_UPDATES_ON_DATAPLAN_KEY, updateOnDataPlan).commit();
        return this;
    }

    private SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
