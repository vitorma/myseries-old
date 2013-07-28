package mobi.myseries.gui.appwidget;

import java.util.List;

import mobi.myseries.application.App;
import mobi.myseries.application.preferences.ScheduleWidgetPreferences;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Episode;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ScheduleWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private int appWidgetId;
    private RemoteViews loadingView;
    private List<Episode> episodes;

    public ScheduleWidgetViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        this.loadingView = Item.from(context).loading();
    }

    @Override
    public void onCreate() { }

    @Override
    public void onDestroy() { }

    @Override
    public int getCount() {
        return this.episodes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return Item.from(this.context).createFor(this.episodes.get(position));
    }

    @Override
    public RemoteViews getLoadingView() {
        return this.loadingView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        this.loadEpisodes();
    }

    private void loadEpisodes() {
        ScheduleWidgetPreferences prefs = App.preferences().forScheduleWidget(this.appWidgetId);

        int scheduleMode = prefs.scheduleMode();
        ScheduleSpecification specification = prefs.fullSpecification();

        switch(scheduleMode) {
            case ScheduleMode.AIRED:
                this.episodes = App.schedule().aired(specification).episodes();
                break;
            case ScheduleMode.TO_WATCH:
                this.episodes = App.schedule().toWatch(specification).episodes();
                break;
            case ScheduleMode.UNAIRED:
                this.episodes = App.schedule().unaired(specification).episodes();
                break;
        }
    }
}