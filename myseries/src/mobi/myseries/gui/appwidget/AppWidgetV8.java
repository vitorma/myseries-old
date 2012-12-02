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

import android.appwidget.AppWidgetManager;
import android.content.Context;

public class AppWidgetV8 extends AppWidget {

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
    protected void onUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        refresh(context, appWidgetId);
    }
}
