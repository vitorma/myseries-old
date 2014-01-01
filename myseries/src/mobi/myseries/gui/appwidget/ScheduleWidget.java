package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.broadcast.BroadcastAction;
import mobi.myseries.shared.DatesAndTimes;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class ScheduleWidget extends AppWidgetProvider {
    private static final String TAG = ScheduleWidget.class.getName();

    public static void setUp(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews appWidgetView = new RemoteViews(context.getPackageName(), R.layout.schedulewidget);

        ActionBar.from(context, appWidgetView).setUpFor(appWidgetId);
        TabBar.from(context, appWidgetView).setUpFor(appWidgetId);

        appWidgetView.setRemoteAdapter(R.id.episodeList, adapterIntentFrom(context, appWidgetId));

        appWidgetView.setPendingIntentTemplate(R.id.episodeList, episodesIntentTemplateFrom(context, appWidgetId));
        appWidgetView.setEmptyView(R.id.episodeList, R.id.emptyView);

        appWidgetManager.updateAppWidget(appWidgetId, appWidgetView);

        refresh(context, appWidgetId);
    }

    public static void refresh(Context context, int appWidgetId) {
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.episodeList);
    }

    private static Intent adapterIntentFrom(Context context, int appWidgetId) {
        return ScheduleWidgetViewsService.newIntent(context, appWidgetId);
    }

    private static PendingIntent episodesIntentTemplateFrom(Context context, int appWidgetId) {
        Intent intent = new Intent(context, ScheduleWidgetDialogActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Log.d(TAG, "first appwidget was added");

        scheduleAlarm(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        Log.d(TAG, "last appwidget was removed");

        cancelAlarm(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            App.preferences().removeEntriesRelatedToScheduleWidget(appWidgetId);

            Log.d(TAG, "appwidget " + appWidgetId + " was deleted");
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i = 0; i < appWidgetIds.length; i++) {
            setUp(context, appWidgetManager, appWidgetIds[i]);

            Log.d(TAG, "appwidget " + appWidgetIds[i] + " was updated");
        }
    }

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
        return BroadcastAction.MARKING.equals(action) ||
               BroadcastAction.UPDATE.equals(action) ||
               BroadcastAction.UPDATE_ALARM.equals(action) ||
               BroadcastAction.ADDITION.equals(action) ||
               BroadcastAction.REMOVAL.equals(action);
    }

    private void callOnUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));

        this.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /* TODO (Cleber) Extract an object to do this work. */

    public static void scheduleAlarm(Context context) {
        cancelAlarm(context);

        alarmManager(context).setRepeating(
                AlarmManager.RTC,
                DatesAndTimes.today().getTime(),
                AlarmManager.INTERVAL_DAY,
                pendingIntentForUpdate(context));
    }

    private static void cancelAlarm(Context context) {
        alarmManager(context).cancel(pendingIntentForUpdate(context));
    }

    private static AlarmManager alarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent pendingIntentForUpdate(Context context) {
        return PendingIntent.getBroadcast(
                context,
                0,
                new Intent(BroadcastAction.UPDATE_ALARM),
                0);
    }
}
