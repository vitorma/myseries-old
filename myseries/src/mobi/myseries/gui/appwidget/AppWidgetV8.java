/*
 *   AppWidgetV8.java
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

import mobi.myseries.application.App;
import mobi.myseries.application.broadcast.BroadcastAction;
import mobi.myseries.gui.preferences.Preferences;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class AppWidgetV8 extends AppWidgetProvider {

    public static void setUp(Context context, int appWidgetId) {
        startService(context, appWidgetId, Action.SETUP);
    }

    public static void refresh(Context context, int appWidgetId) {
        startService(context, appWidgetId, Action.REFRESH);
    }

    private static void startService(Context context, int appWidgetId, String action) {
        context.startService(SetupService.newIntent(context, appWidgetId, action));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            refresh(context, appWidgetIds[i]);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            Preferences.removeEntriesRelatedToAppWidget(appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO (Cleber) Receive broadcasts about ADD, REMOVE and UPDATE
        if (BroadcastAction.SEEN_MARKUP.equals(intent.getAction())) {
            AppWidgetManager awm = AppWidgetManager.getInstance(App.context());
            int[] ids = awm.getAppWidgetIds(new ComponentName(App.context(), AppWidgetV8.class));
            this.onUpdate(App.context(), awm, ids);
        } else {
            super.onReceive(context, intent);
        }
    }
}
