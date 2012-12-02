/*
 *   AppWidgetV11.java
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
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.shared.Android;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AppWidgetV11 extends AppWidget {

    @SuppressWarnings("deprecation")
    public static void setUp(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews appWidgetView = new RemoteViews(context.getPackageName(), R.layout.appwidget);

        ActionBar.from(context, appWidgetView).setUpFor(appWidgetId);

        if (Android.isIceCreamSandwichOrHigher()) {
            appWidgetView.setRemoteAdapter(R.id.episodeList, adapterIntentFrom(context, appWidgetId));
        } else {
            appWidgetView.setRemoteAdapter(appWidgetId, R.id.episodeList, adapterIntentFrom(context, appWidgetId));
        }

        appWidgetView.setPendingIntentTemplate(R.id.episodeList, episodesIntentTemplateFrom(context, appWidgetId));
        appWidgetView.setEmptyView(R.id.episodeList, R.id.emptyView);

        appWidgetManager.updateAppWidget(appWidgetId, appWidgetView);
        refresh(context, appWidgetId);
    }

    public static void refresh(Context context, int appWidgetId) {
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.episodeList);
    }

    private static Intent adapterIntentFrom(Context context, int appWidgetId) {
        return AdapterService.newIntent(context, appWidgetId);
    }

    private static PendingIntent episodesIntentTemplateFrom(Context context, int appWidgetId) {
        Intent intent = new Intent(context, EpisodesActivity.class);

        return PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        setUp(context, appWidgetManager, appWidgetId);
    }
}