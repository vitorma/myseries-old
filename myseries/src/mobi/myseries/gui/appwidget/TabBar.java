package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.preferences.Preferences;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

public class TabBar {
    private Context context;
    private RemoteViews appWidgetView;

    private TabBar(Context context, RemoteViews appWidgetView) {
        this.context = context;
        this.appWidgetView = appWidgetView;
    }

    public static TabBar from(Context context, RemoteViews appWidgetView) {
        return new TabBar(context, appWidgetView);
    }

    public TabBar setUpFor(int appWidgetId) {
        this.showSelectedTabMarkFrom(appWidgetId);

        this.appWidgetView.setOnClickPendingIntent(R.id.recentTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.RECENT));
        this.appWidgetView.setOnClickPendingIntent(R.id.nextTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.NEXT));
        this.appWidgetView.setOnClickPendingIntent(R.id.upcomingTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.UPCOMING));

        return this;
    }

    private void showSelectedTabMarkFrom(int appWidgetId) {
        int scheduleMode = Preferences.forAppWidget(appWidgetId).scheduleMode();

        switch (scheduleMode) {
            case ScheduleMode.RECENT:
                this.appWidgetView.setViewVisibility(R.id.selectRecent, View.VISIBLE);
                this.appWidgetView.setViewVisibility(R.id.selectNext, View.INVISIBLE);
                this.appWidgetView.setViewVisibility(R.id.selectUpcoming, View.INVISIBLE);
                break;
            case ScheduleMode.NEXT:
                this.appWidgetView.setViewVisibility(R.id.selectRecent, View.INVISIBLE);
                this.appWidgetView.setViewVisibility(R.id.selectNext, View.VISIBLE);
                this.appWidgetView.setViewVisibility(R.id.selectUpcoming, View.INVISIBLE);
                break;
            case ScheduleMode.UPCOMING:
                this.appWidgetView.setViewVisibility(R.id.selectRecent, View.INVISIBLE);
                this.appWidgetView.setViewVisibility(R.id.selectNext, View.INVISIBLE);
                this.appWidgetView.setViewVisibility(R.id.selectUpcoming, View.VISIBLE);
                break;
            default:
                throw new RuntimeException("Invalid schedule mode");
        }
    }

    private PendingIntent tabServiceIntentFrom(int appWidgetId, int scheduleMode) {
        Intent intent = TabService.newIntent(this.context, appWidgetId, scheduleMode);

        return PendingIntent.getService(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
