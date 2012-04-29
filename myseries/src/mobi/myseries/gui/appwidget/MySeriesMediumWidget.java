/*
 *   MySeriesMediumWidget.java
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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class MySeriesMediumWidget extends MySeriesWidget {
    private static int LIMIT = 4;

    @Override
    protected Intent createUpdateIntent(Context context) {
        Intent intent = new Intent(context, UpdateServiceMedium.class);
        return intent;
    }

    public static class UpdateServiceMedium extends UpdateService {
        public UpdateServiceMedium() {
            super("mobi.myseries.gui.appwidget.MySeriesMediumWidget$UpdateServiceMedium");
        }

        @Override
        public void onHandleIntent(Intent intent) {
            ComponentName componentName = new ComponentName(this, MySeriesMediumWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            Intent i = new Intent(this, MySeriesMediumWidget.class);

            manager.updateAppWidget(
                    componentName,
                    buildUpdate(this, super.layout(), super.itemLayout(), super.noItemLayout(), LIMIT, i)
            );
        }
    }
}
