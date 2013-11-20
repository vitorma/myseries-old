package mobi.myseries.application.update;

import java.util.concurrent.TimeUnit;

import mobi.myseries.application.App;
import mobi.myseries.application.Communications;
import mobi.myseries.application.preferences.UpdatePreferences;
import android.util.Log;

public class UpdatePolicy {
    private static final long ONE_MONTH = 30L * 24L * 60L * 60L * 1000L;
    private static final long AUTOMATIC_UPDATE_INTERVAL = 12L * 60L * 60L * 1000L;
    private static final long ONE_MINUTE = 60 * 1000L;

    static boolean shouldUpdateNow(Communications communications) {
        UpdatePreferences settings = App.preferences().forUpdate();

        if (!settings.updateAutomatically()) {
            Log.d(UpdatePolicy.class.getName(), "Do not update automatically.");
            return false;
        }

        if (!communications.isConnected()) {
            Log.d(UpdatePolicy.class.getName(), "No connection.");
            return false;

        } else if (!communications.isConnectedToWiFi() /* hence is connected on data plan? */) {
            // TODO(Gabriel): Rename setting as onlyUpdateOnWiFi
            Log.d(UpdatePolicy.class.getName(),
                    "Update on data plan? " + settings.updateAlways());
            return settings.updateAlways();
        }

        return true;
    }

    public static long automaticUpdateInterval() {
        return AUTOMATIC_UPDATE_INTERVAL;
    }

    public static long downloadEverythingInterval() {
        return ONE_MONTH;
    }

    public static long updateTimeout() {
        return ONE_MINUTE;
    }

    public static TimeUnit updateTimeoutUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
