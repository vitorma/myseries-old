/*
 *   MyScheduleWidgetProviderLarge.java
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


import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyScheduleWidgetProviderLarge extends AppWidgetProvider {
    static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    static final ImageProvider imageProvider = App.environment().imageProvider();
    protected static final int layout = R.layout.appwidget_myschedule_old;
    protected static final int itemLayout = R.layout.appwidget_myschedule_item;
    protected static final int noItemLayout = R.layout.appwidget_empty_view;
    static final String REFRESH = "mobi.myseries.gui.appwidget.REFRESH";
    static final String ADD = "mobi.myseries.gui.appwidget.ADD";
    static final String REMOVE = "mobi.myseries.gui.appwidget.REMOVE";
    protected static final int LIMIT = 9;
    static final String NUMBER_OF_ITEMS = "mobi.myseries.gui.appwidget.numberOfItems";

    private int numberOfItems;

    public MyScheduleWidgetProviderLarge() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.numberOfItems = extras.getInt(NUMBER_OF_ITEMS);
            Log.d("Widget.onReceive","numberOfItems=" + this.numberOfItems);
        }

        if (REFRESH.equals(intent.getAction())) {
            context.startService(this.createUpdateIntent(context));
        } else if (ADD.equals(intent.getAction())) {
            this.numberOfItems++;
            context.startService(this.createUpdateIntent(context));
        } else if (REMOVE.equals(intent.getAction()) && this.numberOfItems > 0) {
            this.numberOfItems--;
            context.startService(this.createUpdateIntent(context));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(this.createUpdateIntent(context));
    }

    protected Intent createUpdateIntent(Context context) {
        Intent i = new Intent(context, this.updateServiceClass());
        i.putExtra(NUMBER_OF_ITEMS, this.numberOfItems);
        return i;
    }

    protected Class updateServiceClass() {
        return MyScheduleWidgetServiceLarge.class;
    }
}
