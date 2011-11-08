package br.edu.ufcg.aweseries.gui.desktop_widget;

import java.util.Iterator;
import java.util.List;

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
    private static final int layout = R.layout.aweseries_desktop_widget;
    private static final int itemLayout = R.layout.widget_list_item;
    private static final int noItemLayout = R.layout.text_only_list_item;
    private static final String REFRESH = "br.edu.ufcg.aweseries.gui.desktop_widget.REFRESH";

    private static final int LIMIT = 6;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (REFRESH.equals(intent.getAction())) {
            context.startService(createUpdateIntent(context));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(createUpdateIntent(context));
    }

    private Intent createUpdateIntent(Context context) {
        return new Intent(context, UpdateService.class);
    }

    public static class UpdateService extends IntentService {

        public UpdateService() {
            super("br.edu.ufcg.aweseries.gui.desktop_widget.AweseriesWidgetProvider$UpdateService");
        }

        @Override
        public void onHandleIntent(Intent intent) {
            ComponentName me = new ComponentName(this, AweseriesWidgetProvider.class);
            AppWidgetManager mgr = AppWidgetManager.getInstance(this);

            Intent i = new Intent(this, AweseriesWidgetProvider.class);
            mgr.updateAppWidget(me, buildUpdate(this, layout, itemLayout, noItemLayout, LIMIT, i));
        }

        protected RemoteViews buildUpdate(Context context, int layout, int itemLayout, int noItemLayout, int limit, Intent updateIntent) {
            RemoteViews views = new RemoteViews(context.getPackageName(), layout);
            views.removeAllViews(R.id.innerLinearLayout);

            List<Episode> recent = seriesProvider.recentNotSeenEpisodes();

            if (recent.isEmpty()) {
                Log.d("Widget", "recent list is empty");
                RemoteViews item = new RemoteViews(context.getPackageName(), noItemLayout);
                item.setTextViewText(R.id.itemName, context.getString(R.string.upToDate));
                views.addView(R.id.innerLinearLayout, item);
            } else {
                Log.d("Widget", "recent list is not empty");
                
                int viewsToAdd = limit;
                Iterator<Episode> it = recent.iterator();
                
                while (it.hasNext() && viewsToAdd > 0) {
                    Episode e = it.next();
                    Series series = seriesProvider.getSeries(e.getSeriesId());
                    Season season = series.getSeasons().getSeason(e.getSeasonNumber());

                    Bitmap poster = series.getPoster().getImage();
                    String pre = String.format("S%02d" + "E%02d", season.getNumber(), e.getNumber());

                    RemoteViews item = new RemoteViews(context.getPackageName(), itemLayout);
                    if (poster != null) {
                        item.setImageViewBitmap(R.id.widgetPoster, poster);
                    }
                    item.setTextViewText(R.id.widgetEpisodeSeriesTextView, series.getName());
                    item.setTextViewText(R.id.widgetEpisodeNameTextView, pre + " - " + e.getName());
                    item.setTextViewText(R.id.widgetEpisodeDateTextView, e.getFirstAiredAsString());
                    
                    views.addView(R.id.innerLinearLayout, item);
                    viewsToAdd--;
                }
            }

            Intent intent = new Intent(context, RecentAndUpcomingEpisodesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.innerLinearLayout, pendingIntent);

            updateIntent.setAction(REFRESH);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
            views.setOnClickPendingIntent(R.id.ImageButtonWidget, pi);

            return views;
        }
    }
}
