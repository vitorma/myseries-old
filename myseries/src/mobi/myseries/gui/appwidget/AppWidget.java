package mobi.myseries.gui.appwidget;

import mobi.myseries.application.broadcast.BroadcastAction;
import mobi.myseries.gui.preferences.Preferences;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class AppWidget extends AppWidgetProvider {
    private static final String TAG = "AppWidget";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Log.d(TAG, "first appwidget was added");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        Log.d(TAG, "last appwidget was removed");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Preferences.removeEntriesRelatedToAppWidget(appWidgetId);

            Log.d(TAG, "appwidget " + appWidgetId + " was deleted");
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            this.onUpdate(context, appWidgetManager, appWidgetIds[i]);

            Log.d(TAG, "appwidget " + appWidgetIds[i] + " was updated");
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    protected abstract void onUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.shouldCallOnUpdate(intent.getAction())) {
            this.callOnUpdate(context);

            Log.d(TAG, "onUpdate called because " + intent.getAction());
        } else {
            super.onReceive(context, intent);
        }
    }

    private boolean shouldCallOnUpdate(String action) {
        return BroadcastAction.SEEN_MARKUP.equals(action) ||
               BroadcastAction.UPDATE.equals(action) ||
               BroadcastAction.ADDICTION.equals(action) ||
               BroadcastAction.REMOVAL.equals(action);
    }

    private void callOnUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));

        this.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
