package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.myseries.MySeriesActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class MyScheduleWidgetProvider extends AppWidgetProvider {
    public static final String REFRESH = "mobi.myseries.gui.appwidget.REFRESH";
    public static final long REPETITION_INTERVAL = 15 * 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (REFRESH.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyScheduleWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_myschedule);

        //Service
        int scheduleMode = MyScheduleWidgetPreferenceActivity.getScheduleModePreference(context, appWidgetId);
        int sortMode = MyScheduleWidgetPreferenceActivity.getSortModePreference(context, appWidgetId);
        Intent serviceIntent = MyScheduleWidgetService.newIntent(context, appWidgetId, scheduleMode, sortMode);
        rv.setRemoteAdapter(R.id.list_view, serviceIntent);

        //Empty view
        rv.setEmptyView(R.id.list_view, R.id.empty_view);

        //Title
        rv.setTextViewText(R.id.title, getWidgetTitle(context, scheduleMode));

        //HomeButton
        Intent homeIntent = new Intent(context, MySeriesActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent homePendingIntent = PendingIntent.getActivity(context, 0, homeIntent, 0);
        rv.setOnClickPendingIntent(R.id.homeButton, homePendingIntent);

        //ScheduleButton
        Intent scheduleIntent = MyScheduleActivity.newIntent(context, scheduleMode);
        scheduleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent schedulePendingIntent = PendingIntent.getActivity(context, 1, scheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.scheduleButton, schedulePendingIntent);

        //ConfigureButton
        Intent configureIntent = new Intent(context, MyScheduleWidgetPreferenceActivity.class);
        configureIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        configureIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent configurePendingIntent = PendingIntent.getActivity(context, 2, configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.configureButton, configurePendingIntent);

        //RefreshButton
        Intent refreshIntent = new Intent(REFRESH);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 3, refreshIntent, 0);
        rv.setOnClickPendingIntent(R.id.refreshButton, refreshPendingIntent);

        //ItemIntentTemplate
        Intent itemIntent = new Intent(context, EpisodesActivity.class);
        PendingIntent pendingIntentTemplate = PendingIntent.getActivity(context, 4, itemIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.list_view, pendingIntentTemplate);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private static CharSequence getWidgetTitle(Context context, int scheduleMode) {
        switch (scheduleMode) {
            case ScheduleMode.RECENT:
                return context.getText(R.string.recent);
            case ScheduleMode.TODAY:
                return context.getText(R.string.today);
            case ScheduleMode.UPCOMING:
                return context.getText(R.string.upcoming);
            default:
                return null;
        }
    }
}
