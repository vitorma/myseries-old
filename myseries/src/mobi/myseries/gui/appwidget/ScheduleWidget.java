package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.broadcast.BroadcastAction;
import mobi.myseries.gui.episodes.EpisodesActivity;
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

    //XXX (Cleber) This method does not provide the right navigation. It will work as expected when MySchedule
    //             is redesigned for showing both lists and details of episodes.
    private static PendingIntent episodesIntentTemplateFrom(Context context, int appWidgetId) {
        //XXX (Cleber) Uncomment the following lines and delete the duplicated declaration of intent.
//        int scheduleMode = App.preferences().forScheduleWidget(appWidgetId).scheduleMode();
//        Intent intent = MyScheduleActivity
//                .newIntent(context, scheduleMode, seriesId, seasonNumber, episodeNumber)
//                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = new Intent(context, EpisodesActivity.class);
        return PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

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
            App.preferences().removeEntriesRelatedToAppWidget(appWidgetId);

            Log.d(TAG, "appwidget " + appWidgetId + " was deleted");
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            setUp(context, appWidgetManager, appWidgetIds[i]);

            Log.d(TAG, "appwidget " + appWidgetIds[i] + " was updated");
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
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
               BroadcastAction.ADDITION.equals(action) ||
               BroadcastAction.REMOVAL.equals(action);
    }

    private void callOnUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));

        this.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
