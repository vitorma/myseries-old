/*
 *   ScheduleWidgetV8.java
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

import mobi.myseries.gui.preferences.Preferences;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

public class ScheduleWidgetV8 extends AppWidgetProvider {

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
            Log.d("Widget", "Deleted " + appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("Widget", "onEnabled called");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("Widget", "onDisabled called");
    }
}
