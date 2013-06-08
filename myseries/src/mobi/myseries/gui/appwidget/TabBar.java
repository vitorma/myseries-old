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
        this.highlightSelectedTab(appWidgetId);

        this.appWidgetView.setOnClickPendingIntent(R.id.recentTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.RECENT));
        this.appWidgetView.setOnClickPendingIntent(R.id.nextTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.NEXT));
        this.appWidgetView.setOnClickPendingIntent(R.id.upcomingTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.UPCOMING));

        return this;
    }

    private void highlightSelectedTab(int appWidgetId) {
        int scheduleMode = Preferences.forScheduleWidget(appWidgetId).scheduleMode();

        switch (scheduleMode) {
            case ScheduleMode.RECENT:
                this.highLightRecentTab();
                break;
            case ScheduleMode.NEXT:
                this.hightlightNextTab();
                break;
            case ScheduleMode.UPCOMING:
                this.highlightUpcomingTab();
                break;
            default:
                throw new RuntimeException("Invalid schedule mode");
        }
    }

    private void highLightRecentTab() {
        this.show(R.id.recentTabIndicator);
        this.hide(R.id.nextTabIndicator);
        this.hide(R.id.upcomingTabIndicator);

        this.setTextColorToWhite(R.id.recentTab);
        this.setTextColorToTranslucentWhite(R.id.nextTab);
        this.setTextColorToTranslucentWhite(R.id.upcomingTab);
    }

    private void hightlightNextTab() {
        this.hide(R.id.recentTabIndicator);
        this.show(R.id.nextTabIndicator);
        this.hide(R.id.upcomingTabIndicator);

        this.setTextColorToTranslucentWhite(R.id.recentTab);
        this.setTextColorToWhite(R.id.nextTab);
        this.setTextColorToTranslucentWhite(R.id.upcomingTab);
    }

    private void highlightUpcomingTab() {
        this.hide(R.id.recentTabIndicator);
        this.hide(R.id.nextTabIndicator);
        this.show(R.id.upcomingTabIndicator);

        this.setTextColorToTranslucentWhite(R.id.recentTab);
        this.setTextColorToTranslucentWhite(R.id.nextTab);
        this.setTextColorToWhite(R.id.upcomingTab);
    }

    private void hide(int viewId) {
        this.appWidgetView.setViewVisibility(viewId, View.INVISIBLE);
    }

    private void show(int viewResourceId) {
        this.appWidgetView.setViewVisibility(viewResourceId, View.VISIBLE);
    }

    private void setTextColorToWhite(int viewId) {
        this.appWidgetView.setTextColor(viewId, this.color(R.color.white));
    }

    private void setTextColorToTranslucentWhite(int viewId) {
        this.appWidgetView.setTextColor(viewId, this.color(R.color.translucent_white));
    }

    private int color(int colorId) {
        return this.context.getResources().getColor(colorId);
    }

    private PendingIntent tabServiceIntentFrom(int appWidgetId, int scheduleMode) {
        Intent intent = TabService.newIntent(this.context, appWidgetId, scheduleMode);

        return PendingIntent.getService(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
