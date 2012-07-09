/*
 *   MySeriesWidget.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.gui.appwidget;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodeDetailsActivity;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.myseries.MySeriesActivity;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.shared.Dates;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class MySeriesWidget extends AppWidgetProvider {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final ImageProvider imageProvider = App.environment().imageProvider();
    protected static final int layout = R.layout.appwidget;
    protected static final int itemLayout = R.layout.appwidget_item;
    protected static final int noItemLayout = R.layout.appwidget_noitem_deletemeasap;
    private static final String REFRESH = "mobi.myseries.gui.appwidget.REFRESH";
    private static final String ADD = "mobi.myseries.gui.appwidget.ADD";
    private static final String REMOVE = "mobi.myseries.gui.appwidget.REMOVE";
    protected static final int LIMIT = 9;
    private static final String NUMBER_OF_ITEMS = "mobi.myseries.gui.appwidget.numberOfItems";

    private int numberOfItems;

    public MySeriesWidget() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.numberOfItems = extras.getInt(NUMBER_OF_ITEMS);
            Log.d("Widget.onReceive","numberOfItems=" + this.numberOfItems);
        }

        if (REFRESH.equals(intent.getAction())) {
            context.startService(this.createUpdateIntent(context));
        } else if (ADD.equals(intent.getAction())) {
            this.numberOfItems++;
            context.startService(this.createUpdateIntent(context));
        } else if (REMOVE.equals(intent.getAction()) && this.numberOfItems > 0) {
            this.numberOfItems--;
            context.startService(this.createUpdateIntent(context));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(this.createUpdateIntent(context));
    }

    protected Intent createUpdateIntent(Context context) {
        Intent i = new Intent(context, this.updateServiceClass());
        i.putExtra(NUMBER_OF_ITEMS, this.numberOfItems);
        return i;
    }

    protected Class updateServiceClass() {
        return UpdateService.class;
    }

    //UpdateService-----------------------------------------------------------------------------------------------------

    public static class UpdateService extends IntentService {
        private static final Comparator<Episode> COMPARATOR =
            EpisodeComparator.reversedByAirdateThenBySeasonThenByNumber();


        //Experimental
        protected int numberOfItems;

        public UpdateService() {
            super("mobi.myseries.gui.appwidget.MySeriesWidget$UpdateService");
        }

        public UpdateService(String s) {
            super(s);
        }

        @Override
        public void onHandleIntent(Intent intent) {
            this.numberOfItems = intent.getExtras().getInt(NUMBER_OF_ITEMS);

            ComponentName cn = new ComponentName(this, this.widgetClass());
            AppWidgetManager mgr = AppWidgetManager.getInstance(this);

            mgr.updateAppWidget(cn, this.buildUpdate(this, layout, itemLayout, noItemLayout, this.numberOfItems));
        }

        protected RemoteViews buildUpdate(Context context, int layout, int itemLayout, int noItemLayout, int limit) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), layout);
            rv.removeAllViews(R.id.container);

            SortedSet<Episode> recent = sortedSetBy(seriesProvider.recentEpisodes());

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
                    Series series = seriesProvider.getSeries(e.seriesId());
                    Season season = series.seasons().season(e.seasonNumber());

                    RemoteViews item = new RemoteViews(context.getPackageName(), itemLayout);
 
                    item.setImageViewBitmap(R.id.poster, imageProvider.getPosterOf(series));
                    item.setTextViewText(R.id.seriesName, series.name());
                    String episodeName = String.format(
                            this.getString(R.string.episode_number_format), season.number(), e.number());
                    item.setTextViewText(R.id.episodeNumber, episodeName);
                    item.setTextViewText(
                            R.id.episodeAirDate,
                            Dates.toString(e.airDate(), App.environment().localization().dateFormat(), ""));

                    //Launch EpisodesActivity - item.onClick()
                    Intent i = EpisodeDetailsActivity.newIntent(context, e.seriesId(), e.seasonNumber(), e.number());
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
            i3.putExtra(NUMBER_OF_ITEMS, this.numberOfItems);
            i3.setAction(REFRESH);
            PendingIntent pi3 = PendingIntent.getBroadcast(this, 3, i3, PendingIntent.FLAG_CANCEL_CURRENT);
            rv.setOnClickPendingIntent(R.id.refreshButton, pi3);

            //Experimental - add items
            Intent i4 = new Intent(this, this.widgetClass());
            i4.putExtra(NUMBER_OF_ITEMS, this.numberOfItems);
            i4.setAction(ADD);
            PendingIntent pi4 = PendingIntent.getBroadcast(this, 4, i4, PendingIntent.FLAG_CANCEL_CURRENT);
            rv.setOnClickPendingIntent(R.id.moreItemsButton, pi4);

            //Experimental - remove items
            Intent i5 = new Intent(this, this.widgetClass());
            i5.putExtra(NUMBER_OF_ITEMS, this.numberOfItems);
            i5.setAction(REMOVE);
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
            return layout;
        }

        protected int itemLayout() {
            return itemLayout;
        }

        public int noItemLayout() {
            return noItemLayout;
        }

        protected Class widgetClass() {
            return MySeriesWidget.class;
        }
    }
}
