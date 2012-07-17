package mobi.myseries.gui.appwidget;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.myseries.MySeriesActivity;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.shared.Dates;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MyScheduleWidgetServiceLarge extends IntentService {
    private static final Comparator<Episode> COMPARATOR =
        EpisodeComparator.byNewestFirst();


    //Experimental
    protected int numberOfItems;

    public MyScheduleWidgetServiceLarge() {
        super("mobi.myseries.gui.appwidget.MyScheduleWidgetServiceLarge");
    }

    public MyScheduleWidgetServiceLarge(String s) {
        super(s);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        this.numberOfItems = intent.getExtras().getInt(MyScheduleWidgetProviderLarge.NUMBER_OF_ITEMS);

        ComponentName cn = new ComponentName(this, this.widgetClass());
        AppWidgetManager mgr = AppWidgetManager.getInstance(this);

        mgr.updateAppWidget(cn, this.buildUpdate(this, MyScheduleWidgetProviderLarge.layout, MyScheduleWidgetProviderLarge.itemLayout, MyScheduleWidgetProviderLarge.noItemLayout, this.numberOfItems));
    }

    protected RemoteViews buildUpdate(Context context, int layout, int itemLayout, int noItemLayout, int limit) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), layout);
        rv.removeAllViews(R.id.container);

        SortedSet<Episode> recent = sortedSetBy(MyScheduleWidgetProviderLarge.seriesProvider.recentEpisodes());

        if (recent.isEmpty()) {
            RemoteViews item = new RemoteViews(context.getPackageName(), noItemLayout);
            item.setTextViewText(R.id.itemName, context.getString(R.string.up_to_date));
            rv.addView(R.id.container, item);
        } else {
            int viewToAdd = 0;
            Iterator<Episode> it = recent.iterator();
            Log.d("BuildingUpdate", "views to add = " + viewToAdd);

            while (it.hasNext() && viewToAdd < limit) {
                Episode e = it.next();
                Series series = MyScheduleWidgetProviderLarge.seriesProvider.getSeries(e.seriesId());
                Season season = series.seasons().season(e.seasonNumber());

                RemoteViews item = new RemoteViews(context.getPackageName(), itemLayout);

                item.setImageViewBitmap(R.id.poster, MyScheduleWidgetProviderLarge.imageProvider.getPosterOf(series));
                item.setTextViewText(R.id.seriesName, series.name());
                String episodeName = String.format(
                        this.getString(R.string.episode_number_format), season.number(), e.number());
                item.setTextViewText(R.id.episodeNumber, episodeName);
                item.setTextViewText(
                        R.id.episodeAirDate,
                        Dates.toString(e.airDate(), App.environment().localization().dateFormat(), ""));

                //Launch EpisodesActivity - item.onClick()
                Intent i = EpisodesActivity.newIntent(context, e.seriesId(), e.seasonNumber(), e.number());
                PendingIntent pi = PendingIntent.getActivity(context, 5+viewToAdd, i, PendingIntent.FLAG_UPDATE_CURRENT);
                item.setOnClickPendingIntent(R.id.item, pi);

                rv.addView(R.id.container, item);
                viewToAdd++;
                Log.d("BuildingUpdate", "views to add = " + viewToAdd);
            }
        }

        //Launch MySeries - homeButton.onClick()
        Intent i1 = new Intent(context, MySeriesActivity.class);
        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi1 = PendingIntent.getActivity(context, 1, i1, 0);
        rv.setOnClickPendingIntent(R.id.homeButton, pi1);

        //Launch MySchedule - scheduleButton.onClick()
        Intent i2 = new Intent(context, MyScheduleActivity.class);
        i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi2 = PendingIntent.getActivity(context, 2, i2, 0);
        rv.setOnClickPendingIntent(R.id.scheduleButton, pi2);

        //Perform refresh - buttonRefresh.onClick() and receiving broadcasts
        Intent i3 = new Intent(this, this.widgetClass());
        i3.putExtra(MyScheduleWidgetProviderLarge.NUMBER_OF_ITEMS, this.numberOfItems);
        i3.setAction(MyScheduleWidgetProviderLarge.REFRESH);
        PendingIntent pi3 = PendingIntent.getBroadcast(this, 3, i3, PendingIntent.FLAG_CANCEL_CURRENT);
        rv.setOnClickPendingIntent(R.id.refreshButton, pi3);

        //Add items
        Intent i4 = new Intent(this, this.widgetClass());
        i4.putExtra(MyScheduleWidgetProviderLarge.NUMBER_OF_ITEMS, this.numberOfItems);
        i4.setAction(MyScheduleWidgetProviderLarge.ADD);
        PendingIntent pi4 = PendingIntent.getBroadcast(this, 4, i4, PendingIntent.FLAG_CANCEL_CURRENT);
        rv.setOnClickPendingIntent(R.id.moreItemsButton, pi4);

        //Remove items
        Intent i5 = new Intent(this, this.widgetClass());
        i5.putExtra(MyScheduleWidgetProviderLarge.NUMBER_OF_ITEMS, this.numberOfItems);
        i5.setAction(MyScheduleWidgetProviderLarge.REMOVE);
        PendingIntent pi5 = PendingIntent.getBroadcast(this, 5, i5, PendingIntent.FLAG_CANCEL_CURRENT);
        rv.setOnClickPendingIntent(R.id.lessItemsButton, pi5);

        return rv;
    }

    private static SortedSet<Episode> sortedSetBy(List<Episode> list) {
        SortedSet<Episode> sorted = new TreeSet<Episode>(COMPARATOR);
        sorted.addAll(list);
        return sorted;
    }

    protected int layout() {
        return MyScheduleWidgetProviderLarge.layout;
    }

    protected int itemLayout() {
        return MyScheduleWidgetProviderLarge.itemLayout;
    }

    public int noItemLayout() {
        return MyScheduleWidgetProviderLarge.noItemLayout;
    }

    protected Class widgetClass() {
        return MyScheduleWidgetProviderLarge.class;
    }
}