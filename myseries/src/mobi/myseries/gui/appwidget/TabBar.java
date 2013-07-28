package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
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

        this.appWidgetView.setOnClickPendingIntent(R.id.toWatchTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.TO_WATCH));
        this.appWidgetView.setOnClickPendingIntent(R.id.airedTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.AIRED));
        this.appWidgetView.setOnClickPendingIntent(R.id.unairedTab, this.tabServiceIntentFrom(appWidgetId, ScheduleMode.UNAIRED));

        return this;
    }

    private void highlightSelectedTab(int appWidgetId) {
        int scheduleMode = App.preferences().forScheduleWidget(appWidgetId).scheduleMode();

        switch (scheduleMode) {
            case ScheduleMode.AIRED:
                this.highLightRecentTab();
                break;
            case ScheduleMode.TO_WATCH:
                this.hightlightNextTab();
                break;
            case ScheduleMode.UNAIRED:
                this.highlightUpcomingTab();
                break;
            default:
                throw new RuntimeException("Invalid schedule mode");
        }
    }

    private void highLightRecentTab() {
        this.hide(R.id.toWatchTabIndicator);
        this.show(R.id.airedTabIndicator);
        this.hide(R.id.unairedTabIndicator);

        this.setTextColorToTranslucentWhite(R.id.toWatchTab);
        this.setTextColorToWhite(R.id.airedTab);
        this.setTextColorToTranslucentWhite(R.id.unairedTab);
    }

    private void hightlightNextTab() {
        this.show(R.id.toWatchTabIndicator);
        this.hide(R.id.airedTabIndicator);
        this.hide(R.id.unairedTabIndicator);

        this.setTextColorToWhite(R.id.toWatchTab);
        this.setTextColorToTranslucentWhite(R.id.airedTab);
        this.setTextColorToTranslucentWhite(R.id.unairedTab);
    }

    private void highlightUpcomingTab() {
        this.hide(R.id.toWatchTabIndicator);
        this.hide(R.id.airedTabIndicator);
        this.show(R.id.unairedTabIndicator);

        this.setTextColorToTranslucentWhite(R.id.toWatchTab);
        this.setTextColorToTranslucentWhite(R.id.airedTab);
        this.setTextColorToWhite(R.id.unairedTab);
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
