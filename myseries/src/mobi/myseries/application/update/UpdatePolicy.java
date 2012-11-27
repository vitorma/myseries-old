package mobi.myseries.application.update;

import mobi.myseries.application.App;
import mobi.myseries.application.SettingsProvider;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

class UpdatePolicy {
    private static final long ONE_MONTH = 30L * 24L * 60L * 60L * 1000L;
    private static final long AUTOMATIC_UPDATE_INTERVAL = 12L * 60L * 60L * 1000L;
    private static final long THIRTY_SECONDS = 30 * 1000L;

    static boolean networkAvailable() {
        return (activeNetworkInfo() != null) && activeNetworkInfo().isConnected();
    }

    static NetworkInfo activeNetworkInfo() {
        ConnectivityManager connectivityManager =
                ((ConnectivityManager) App.context().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo();
    }

    static boolean shouldUpdateNow() {
        SettingsProvider settings = new SettingsProvider(App.context());

        if (!settings.updateAutomatically()) {
            Log.d(UpdatePolicy.class.getName(), "Do not update automatically.");
            return false;
        }

        NetworkInfo networkInfo = activeNetworkInfo();

        if ((networkInfo == null) || !networkInfo.isConnected()) {
            Log.d(UpdatePolicy.class.getName(), "No connection.");
            return false;

        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            Log.d(UpdatePolicy.class.getName(),
                    "Update on data plan? " + settings.updateOnDataPlan());
            return settings.updateOnDataPlan();
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
        return THIRTY_SECONDS;
    }
}
