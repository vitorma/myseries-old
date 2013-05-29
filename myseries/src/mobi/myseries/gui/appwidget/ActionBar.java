package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.myseries.MySeriesActivity;
import mobi.myseries.gui.preferences.Preferences;
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
        Intent intent = new Intent(this.context, MySeriesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent scheduleIntentFrom(int appWidgetId) {
        int scheduleMode = Preferences.forAppWidget(appWidgetId).scheduleMode();

        Intent intent = MyScheduleActivity.newIntent(this.context, scheduleMode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent preferencesIntentFrom(int appWidgetId) {
        Intent intent = ScheduleWidgetPreferenceActivity.newIntent(this.context, appWidgetId);

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
