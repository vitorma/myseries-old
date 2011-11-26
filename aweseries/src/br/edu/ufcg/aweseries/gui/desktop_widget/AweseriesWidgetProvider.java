package br.edu.ufcg.aweseries.gui.desktop_widget;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.gui.RecentAndUpcomingEpisodesActivity;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

public class AweseriesWidgetProvider extends AppWidgetProvider {

    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    protected static final int layout = R.layout.aweseries_desktop_widget;
    protected static final int itemLayout = R.layout.widget_list_item;
    protected static final int noItemLayout = R.layout.text_only_list_item;
    private static final String REFRESH = "br.edu.ufcg.aweseries.gui.desktop_widget.REFRESH";

    protected static final int LIMIT = 9;

    public static class UpdateService extends IntentService {

        public UpdateService() {
            super("br.edu.ufcg.aweseries.gui.desktop_widget.AweseriesWidgetProvider$UpdateService");
        }

        public UpdateService(String s) {
            super(s);
        }

        @Override
        public void onHandleIntent(Intent intent) {
            final ComponentName me = new ComponentName(this, AweseriesWidgetProvider.class);
            final AppWidgetManager mgr = AppWidgetManager.getInstance(this);

            final Intent i = new Intent(this, AweseriesWidgetProvider.class);
            mgr.updateAppWidget(me, this.buildUpdate(this, layout, itemLayout, noItemLayout, 9, i));
        }

        protected RemoteViews buildUpdate(Context context, int layout, int itemLayout,
                int noItemLayout, int limit, Intent updateIntent) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), layout);
            views.removeAllViews(R.id.innerLinearLayout);

            final SortedSet<Episode> recent = this.sortedSetBy(seriesProvider
                    .recentNotSeenEpisodes());

            if (recent.isEmpty()) {
                Log.d("Widget", "recent list is empty");
                final RemoteViews item = new RemoteViews(context.getPackageName(), noItemLayout);
                item.setTextViewText(R.id.itemName, context.getString(R.string.up_to_date));
                views.addView(R.id.innerLinearLayout, item);
            } else {
                Log.d("Widget", "recent list is not empty");

                int viewsToAdd = limit;
                final Iterator<Episode> it = recent.iterator();

                while (it.hasNext() && (viewsToAdd > 0)) {
                    final Episode e = it.next();
                    final Series series = seriesProvider.getSeries(e.getSeriesId());
                    final Season season = series.getSeasons().getSeason(e.getSeasonNumber());

                    final RemoteViews item = new RemoteViews(context.getPackageName(), itemLayout);
                    if (series.hasPoster()) {
                        final Bitmap poster = series.getPoster().getImage();
                        item.setImageViewBitmap(R.id.widgetPoster, poster);
                    }
                    item.setTextViewText(R.id.widgetEpisodeSeriesTextView, series.getName());
                    final String pre = String.format(
                            this.getString(R.string.season_and_episode_format_short),
                            season.getNumber(), e.getNumber());
                    item.setTextViewText(R.id.widgetEpisodeNameTextView, String.format(
                            pre + this.getString(R.string.separator) + e.getName()));
                    item.setTextViewText(R.id.widgetEpisodeDateTextView, e.getFirstAiredAsString());

                    views.addView(R.id.innerLinearLayout, item);
                    viewsToAdd--;
                }
            }

            final Intent intent = new Intent(context, RecentAndUpcomingEpisodesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.innerLinearLayout, pendingIntent);

            updateIntent.setAction(REFRESH);
            final PendingIntent pi = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
            views.setOnClickPendingIntent(R.id.ImageButtonWidget, pi);

            return views;
        }

        private SortedSet<Episode> sortedSetBy(List<Episode> list) {
            final SortedSet<Episode> sorted = new TreeSet<Episode>(this.comparator());
            sorted.addAll(list);
            return sorted;
        }

        private Comparator<Episode> comparator() {
            return new Comparator<Episode>() {
                @Override
                public int compare(Episode episodeA, Episode episodeB) {
                    final int byDate = episodeB.compareByDateTo(episodeA);
                    return (byDate == 0) ? episodeB.compareByNumberTo(episodeA) : byDate;
                }
            };
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

    public AweseriesWidgetProvider() {
        super();
    }

}
