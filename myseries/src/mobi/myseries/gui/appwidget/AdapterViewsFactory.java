package mobi.myseries.gui.appwidget;

import java.util.List;

import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.preferences.Preferences;
import mobi.myseries.gui.preferences.SchedulePreferences.AppWidgetPreferences;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class AdapterViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private int appWidgetId;
    private RemoteViews loadingView;
    private List<Episode> episodes;

    public AdapterViewsFactory(Context context, Intent intent) {
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
        AppWidgetPreferences prefs = Preferences.forAppWidget(this.appWidgetId);

        int scheduleMode = prefs.scheduleMode();
        ScheduleSpecification specification = prefs.fullSpecification();

        switch(scheduleMode) {
            case ScheduleMode.RECENT:
                this.episodes = App.schedule().modeRecent(specification).episodes();
                break;
            case ScheduleMode.NEXT:
                this.episodes = App.schedule().modeNext(specification).episodes();
                break;
            case ScheduleMode.UPCOMING:
                this.episodes = App.schedule().modeUpcoming(specification).episodes();
                break;
        }
    }
}