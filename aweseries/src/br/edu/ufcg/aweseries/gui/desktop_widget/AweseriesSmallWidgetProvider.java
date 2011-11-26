package br.edu.ufcg.aweseries.gui.desktop_widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class AweseriesSmallWidgetProvider extends AweseriesWidgetProvider {
    private static int LIMIT = 2;

    public AweseriesSmallWidgetProvider() {
        super();
    }
    
    @Override
    protected Intent createUpdateIntent(Context context) {
        Intent intent = new Intent(context, AweseriesSmallWidgetProvider.class);
        return intent;
    }

    public static class UpdateServiceSmall extends UpdateService {
        public UpdateServiceSmall() {
            super("br.edu.ufcg.aweseries.gui.desktop_widget.AweseriesSmallWidgetProvider$UpdateServiceSmall");
        }
        
        @Override
        public void onHandleIntent(Intent intent) {
            ComponentName componentName = new ComponentName(this, AweseriesSmallWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);

            Intent i = new Intent(this, AweseriesSmallWidgetProvider.class);
            manager.updateAppWidget(
                    componentName,
                    buildUpdate(this, super.getLayout(), super.getItemLayout(),
                            super.getNoItemLayout(), LIMIT, i));
        }
    }
}
