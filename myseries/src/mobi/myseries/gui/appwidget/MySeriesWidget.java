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
import mobi.myseries.gui.EpisodeComparator;
import mobi.myseries.gui.schedule.MyScheduleActivity;
import mobi.myseries.shared.Dates;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MySeriesWidget extends AppWidgetProvider {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final ImageProvider imageProvider = App.environment().imageProvider();
    protected static final int layout = R.layout.aweseries_desktop_widget;
    protected static final int itemLayout = R.layout.widget_list_item;
    protected static final int noItemLayout = R.layout.text_only_list_item;
    private static final String REFRESH = "mobi.myseries.gui.appwidget.REFRESH";
    protected static final int LIMIT = 9;

    public static class UpdateService extends IntentService {
        private static final Comparator<Episode> COMPARATOR =
            EpisodeComparator.reversedByAirdateThenBySeasonThenByNumber();

        public UpdateService() {
            super("mobi.myseries.gui.appwidget.MySeriesWidget$UpdateService");
        }

        public UpdateService(String s) {
            super(s);
        }

        @Override
        public void onHandleIntent(Intent intent) {
            ComponentName cn = new ComponentName(this, MySeriesWidget.class);
            AppWidgetManager mgr = AppWidgetManager.getInstance(this);
            Intent i = new Intent(this, MySeriesWidget.class);

            mgr.updateAppWidget(cn, this.buildUpdate(this, layout, itemLayout, noItemLayout, 9, i));
        }

        protected RemoteViews buildUpdate(
                Context context, int layout, int itemLayout, int noItemLayout, int limit, Intent updateIntent) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), layout);
            rv.removeAllViews(R.id.innerLinearLayout);

            SortedSet<Episode> recent = sortedSetBy(seriesProvider.recentEpisodes());

            if (recent.isEmpty()) {
                RemoteViews item = new RemoteViews(context.getPackageName(), noItemLayout);
                item.setTextViewText(R.id.itemName, context.getString(R.string.up_to_date));
                rv.addView(R.id.innerLinearLayout, item);
            } else {
                int viewsToAdd = limit;
                Iterator<Episode> it = recent.iterator();

                while (it.hasNext() && viewsToAdd > 0) {
                    Episode e = it.next();
                    Series series = seriesProvider.getSeries(e.seriesId());
                    Season season = series.seasons().season(e.seasonNumber());

                    RemoteViews item = new RemoteViews(context.getPackageName(), itemLayout);
 
                    item.setImageViewBitmap(R.id.widgetPoster, imageProvider.getPosterOf(series));
                    item.setTextViewText(R.id.widgetEpisodeSeriesTextView, series.name());
                    String episodeName = String.format(
                            this.getString(R.string.season_and_episode_format_short), season.number(), e.number());
                    item.setTextViewText(R.id.widgetEpisodeNameTextView, episodeName);
                    item.setTextViewText(
                            R.id.widgetEpisodeDateTextView,
                            Dates.toString(e.airDate(), App.environment().localization().dateFormat(), ""));

                    rv.addView(R.id.innerLinearLayout, item);
                    viewsToAdd--;
                }
            }

            Intent intent = new Intent(context, MyScheduleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            rv.setOnClickPendingIntent(R.id.innerLinearLayout, pendingIntent);

            updateIntent.setAction(REFRESH);

            PendingIntent pi = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
            rv.setOnClickPendingIntent(R.id.ImageButtonWidget, pi);

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
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (REFRESH.equals(intent.getAction())) {
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
        return new Intent(context, UpdateService.class);
    }

    public MySeriesWidget() {
        super();
    }
}
