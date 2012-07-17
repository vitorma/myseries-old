package mobi.myseries.gui.appwidget;

import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.shared.Dates;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class MyScheduleWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final ImageProvider IMAGE_PROVIDER = App.environment().imageProvider();
    private static final DateFormat DATE_FORMAT = App.environment().localization().dateFormat();

    private Context context;
    private int scheduleMode;
    private int sortMode;

    private RemoteViews loadingView;

    private List<Episode> episodes;
    private HashMap<Integer, Series> series;
    private HashMap<Integer, Bitmap> seriesPosters;

    public MyScheduleWidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;

        Bundle extras = intent.getExtras();
        this.scheduleMode = extras.getInt(MyScheduleWidgetExtra.SCHEDULE_MODE);
        this.sortMode = extras.getInt(MyScheduleWidgetExtra.SORT_MODE);

        this.loadingView = new RemoteViews(context.getPackageName(), R.layout.appwidget_loading_view);

        this.series = new HashMap<Integer, Series>();
        this.seriesPosters = new HashMap<Integer, Bitmap>();
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        return this.episodes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Episode e = episodes.get(position);
        Series series = this.series.get(e.seriesId());

        RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.appwidget_myschedule_item);

        item.setImageViewBitmap(R.id.poster, this.seriesPosters.get(e.seriesId()));
        item.setTextViewText(R.id.seriesName, series.name());
        String episodeName = String.format(this.context.getString(R.string.episode_number_format), e.seasonNumber(), e.number());
        item.setTextViewText(R.id.episodeNumber, episodeName);

        String unavailableDate = this.context.getString(R.string.unavailable_date);
        String episodeAirdate = Dates.toString(e.airDate(), DATE_FORMAT, unavailableDate);
        item.setTextViewText(R.id.episodeAirDate, episodeAirdate);

        Bundle extras = new Bundle();
        extras.putInt(EpisodesActivity.Extra.SERIES_ID, e.seriesId());
        extras.putInt(EpisodesActivity.Extra.SEASON_NUMBER, e.seasonNumber());
        extras.putInt(EpisodesActivity.Extra.EPISODE_NUMBER, e.number());
        Intent fillInIntent = new Intent().putExtras(extras);
        item.setOnClickFillInIntent(R.id.item, fillInIntent);

        return item;
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
        switch (this.scheduleMode) {
            case ScheduleMode.RECENT:
                this.episodes = SERIES_PROVIDER.recentEpisodes();
                break;
            case ScheduleMode.TODAY:
                this.episodes = SERIES_PROVIDER.todayEpisodes();
                break;
            case ScheduleMode.UPCOMING:
                this.episodes = SERIES_PROVIDER.upcomingEpisodes();
                break;
        }

        switch (this.sortMode) {
            case SortMode.OLDEST_FIRST:
                Collections.sort(this.episodes, EpisodeComparator.byOldestFirst());
                break;
            case SortMode.NEWEST_FIRST:
                Collections.sort(this.episodes, EpisodeComparator.byNewestFirst());
                break;
        }

        this.series.clear();
        this.seriesPosters.clear();

        for (Episode e : this.episodes) {
            if (!this.seriesPosters.containsKey(e.seriesId())) {
                Series series = SERIES_PROVIDER.getSeries(e.seriesId());
                this.series.put(series.id(), series);
                Bitmap seriesPoster = IMAGE_PROVIDER.getPosterOf(series);
                this.seriesPosters.put(series.id(), seriesPoster);
            }
        }
    }
}