package mobi.myseries.gui.appwidget;

import mobi.myseries.application.App;
import mobi.myseries.application.broadcast.BroadcastService;
import mobi.myseries.gui.shared.Extra;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class TabService extends IntentService {

    public TabService() {
        super("mobi.myseries.gui.appwidget.TabService");
    }

    public static Intent newIntent(Context context, int appWidgetId, int scheduleMode) {
        Intent intent = new Intent(context, TabService.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(Extra.SCHEDULE_MODE, scheduleMode);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        int scheduleMode = intent.getExtras().getInt(Extra.SCHEDULE_MODE);

        App.preferences().forScheduleWidget(appWidgetId).putScheduleMode(scheduleMode);

        new BroadcastService(this.getApplicationContext()).broadcastUpdate();
    }
}
