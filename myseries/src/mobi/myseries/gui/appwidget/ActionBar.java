package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.library.LibraryActivity;
import mobi.myseries.gui.schedule.singlepane.ScheduleSinglePaneActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class ActionBar {
    private Context context;
    private RemoteViews appWidgetView;

    private ActionBar(Context context, RemoteViews appWidgetView) {
        this.context = context;
        this.appWidgetView = appWidgetView;
    }

    public static ActionBar from(Context context, RemoteViews appWidgetView) {
        return new ActionBar(context, appWidgetView);
    }

    public ActionBar setUpFor(int appWidgetId) {
        this.appWidgetView.setOnClickPendingIntent(R.id.homeButton, this.homeIntentFrom(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(R.id.scheduleButton, this.scheduleIntentFrom(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(R.id.preferencesButton, this.preferencesIntentFrom(appWidgetId));

        return this;
    }

    private PendingIntent homeIntentFrom(int appWidgetId) {
        Intent intent = LibraryActivity.newIntent(this.context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent scheduleIntentFrom(int appWidgetId) {
        int scheduleMode = App.preferences().forScheduleWidget(appWidgetId).scheduleMode();

        Intent intent = ScheduleSinglePaneActivity.newIntent(this.context, scheduleMode);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent preferencesIntentFrom(int appWidgetId) {
        Intent intent = ScheduleWidgetPreferenceActivity.newIntent(this.context, appWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
