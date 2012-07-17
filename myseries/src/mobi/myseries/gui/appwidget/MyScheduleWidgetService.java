package mobi.myseries.gui.appwidget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViewsService;

public class MyScheduleWidgetService extends RemoteViewsService {

    public static Intent newIntent(Context context, int appWidgetId, int scheduleMode, int sortMode) {
        Intent serviceIntent = new Intent(context, MyScheduleWidgetService.class)
            .putExtra(MyScheduleWidgetExtra.APPWIDGET_ID, appWidgetId)
            .putExtra(MyScheduleWidgetExtra.SCHEDULE_MODE, scheduleMode)
            .putExtra(MyScheduleWidgetExtra.SORT_MODE, sortMode);

        return serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyScheduleWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
