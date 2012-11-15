/*
 *   ActionBar.java
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
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.myseries.MySeriesActivity;
import mobi.myseries.gui.preferences.SchedulePreferences;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class ActionBar {
    private Context context;
    private RemoteViews appWidgetView;

    private ActionBar(Context context, RemoteViews appWidgetView) {
        this.context = context;
        this.appWidgetView = appWidgetView;
    }

    public static ActionBar from(Context context, RemoteViews appWidgetView) {
        return new ActionBar(context, appWidgetView);
    }

    public ActionBar setUpFor(int appWidgetId) {
        this.appWidgetView.setTextViewText(R.id.titleView, this.titleFrom(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(R.id.homeButton, this.homeIntentFrom(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(R.id.scheduleButton, this.scheduleIntentFrom(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(R.id.configureButton, this.preferencesIntentFrom(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(R.id.refreshButton, this.refreshIntentFrom(appWidgetId));

        return this;
    }

    private CharSequence titleFrom(int appWidgetId) {
        int scheduleMode = SchedulePreferences.forAppWidget(appWidgetId).scheduleMode();

        switch (scheduleMode) {
            case ScheduleMode.RECENT:
                return this.context.getText(R.string.recent);
            case ScheduleMode.NEXT:
                return this.context.getText(R.string.next);
            case ScheduleMode.UPCOMING:
                return this.context.getText(R.string.upcoming);
            default:
                return null;
        }
    }

    private PendingIntent homeIntentFrom(int appWidgetId) {
        Intent intent = new Intent(this.context, MySeriesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent scheduleIntentFrom(int appWidgetId) {
        int scheduleMode = SchedulePreferences.forAppWidget(appWidgetId).scheduleMode();

        Intent intent = MyScheduleActivity.newIntent(this.context, scheduleMode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent preferencesIntentFrom(int appWidgetId) {
        Intent intent = AppWidgetPreferenceActivity.newIntent(this.context, appWidgetId);

        return PendingIntent.getActivity(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent refreshIntentFrom(int appWidgetId) {
        Intent intent = RefreshService.newIntent(this.context, appWidgetId);

        return PendingIntent.getService(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
