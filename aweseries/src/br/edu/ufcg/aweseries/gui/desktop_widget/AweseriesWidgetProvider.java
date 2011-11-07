package br.edu.ufcg.aweseries.gui.desktop_widget;

import java.util.List;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.gui.RecentEpisodesActivity;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

public class AweseriesWidgetProvider extends AppWidgetProvider {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final int layout = R.layout.aweseries_desktop_widget;
    private static final int itemLayout = R.layout.episode_alone_list_item;
    private static final int noItemLayout = R.layout.text_only_list_item;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(createUpdateIntent(context));
    }

    private Intent createUpdateIntent(Context context) {
        return new Intent(context, UpdateService.class);
    }

    public static class UpdateService extends IntentService {

        public UpdateService() {
            super("gui.desktop_widget.AweseriesWidgetProvider$UpdateService");
        }

        @Override
        public void onHandleIntent(Intent intent) {
            ComponentName me = new ComponentName(this, AweseriesWidgetProvider.class);
            AppWidgetManager mgr = AppWidgetManager.getInstance(this);

            Intent i = new Intent(this, AweseriesWidgetProvider.class);
            mgr.updateAppWidget(me, buildUpdate(this, layout, itemLayout, noItemLayout, i));
        }

        protected RemoteViews buildUpdate(Context context, int layout, int itemLayout, int noItemLayout, Intent updateIntent) {
            RemoteViews views = new RemoteViews(context.getPackageName(), layout);
            views.removeAllViews(layout);

            List<Episode> recent = seriesProvider.recentNotSeenEpisodes();

            if (recent.isEmpty()) {
                RemoteViews item = new RemoteViews(context.getPackageName(), noItemLayout);
                item.setTextViewText(R.id.itemName, context.getString(R.string.upToDate));
                views.addView(layout, item);
            } else {
                for (Episode e : recent) {
                    Series series = seriesProvider.getSeries(e.getSeriesId());
                    Season season = series.getSeasons().getSeason(e.getSeasonNumber());

                    RemoteViews item = new RemoteViews(context.getPackageName(), itemLayout);
                    item.setTextViewText(R.id.episodeNameTextView, e.getName());
                    item.setTextViewText(R.id.episodeSeriesTextView, series.getName());
                    item.setTextViewText(R.id.episodeSeasonEpisodeTextView, String.format("Season %02d - Episode %02d", season.getNumber(), e.getNumber()));
                    item.setTextViewText(R.id.episodeDateTextView, e.getFirstAiredAsString());

                    views.addView(layout, item);
                }
            }

            Intent intent = new Intent(context, RecentEpisodesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(layout, pendingIntent);

            return views;
        }
    }
}
