package br.edu.ufcg.aweseries.gui.desktop_widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class AweseriesMedWidgetProvider extends AweseriesWidgetProvider {
    private static int LIMIT = 4;

    public AweseriesMedWidgetProvider() {
        super();
    }
    
    @Override
    protected Intent createUpdateIntent(Context context) {
        Intent intent = new Intent(context, UpdateServiceMed.class);
        return intent;
    }
    
    public static class UpdateServiceMed extends UpdateService {
        public UpdateServiceMed() {
            super("br.edu.ufcg.aweseries.gui.desktop_widget.AweseriesMedWidgetProvider$UpdateServiceMed");
        }
        
        @Override
        public void onHandleIntent(Intent intent) {
            ComponentName componentName = new ComponentName(this, AweseriesMedWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);

            Intent i = new Intent(this, AweseriesMedWidgetProvider.class);
            manager.updateAppWidget(
                    componentName,
                    buildUpdate(this, super.getLayout(), super.getItemLayout(),
                            super.getNoItemLayout(), LIMIT, i));
        }
    }
}
