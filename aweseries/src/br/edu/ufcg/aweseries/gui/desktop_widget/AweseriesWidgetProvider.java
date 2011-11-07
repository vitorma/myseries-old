package br.edu.ufcg.aweseries.gui.desktop_widget;

import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.gui.UpcomingEpisodesActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AweseriesWidgetProvider extends AppWidgetProvider {
    SeriesProvider seriesProvider = App.environment().seriesProvider();
    
    final int itemLayout = R.layout.text_only_list_item;
    final int layout = R.layout.aweseries_desktop_widget;
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        int N = appWidgetIds.length;
        for (int i = 0; i < N; ++i) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, UpcomingEpisodesActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            final RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.aweseries_desktop_widget);
            
            views.setOnClickPendingIntent(R.id.linearLayout, pendingIntent);
                        
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    
}
