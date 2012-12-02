/*
 *   RefreshService.java
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

import mobi.myseries.shared.Android;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class RefreshService extends IntentService {

    public RefreshService() {
        super("mobi.myseries.gui.appwidget.RefreshService");
    }

    public static Intent newIntent(Context context, int appWidgetId) {
        Intent intent = new Intent(context, RefreshService.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

        if (Android.isHoneycombOrHigher()) {
            AppWidgetV11.refresh(this.getApplicationContext(), appWidgetId);
        } else {
            AppWidgetV8.refresh(this.getApplicationContext(), appWidgetId);
        }
    }
}
