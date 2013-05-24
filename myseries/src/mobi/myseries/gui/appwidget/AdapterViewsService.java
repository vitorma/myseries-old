package mobi.myseries.gui.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViewsService;

public class AdapterViewsService extends RemoteViewsService {

    public static Intent newIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, AdapterViewsService.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return intent;
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AdapterViewsFactory(this.getApplicationContext(), intent);
    }
}
