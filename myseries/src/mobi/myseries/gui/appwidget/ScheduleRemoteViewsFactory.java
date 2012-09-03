/*
 *   ScheduleRemoteViewsFactory.java
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

import java.util.Collections;
import java.util.List;

import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.SortMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.gui.shared.Extra;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ScheduleRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private int appWidgetId;
    private RemoteViews loadingView;
    private List<Episode> episodes;

    public ScheduleRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getExtras().getInt(Extra.APPWIDGET_ID);
        this.loadingView = Item.from(context).loading();
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        return this.episodes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return Item.from(this.context).createFor(this.episodes.get(position));
    }

    @Override
    public RemoteViews getLoadingView() {
        return this.loadingView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        this.loadEpisodes();
    }

    private void loadEpisodes() {
        int scheduleMode = AppWidgetPreferenceActivity.scheduleModeBy(this.context, this.appWidgetId);
        int sortMode = AppWidgetPreferenceActivity.sortModeBy(this.context, this.appWidgetId);

        switch(scheduleMode) {
            case ScheduleMode.RECENT:
                this.episodes = App.schedule().recent().getEpisodes();
                break;
            case ScheduleMode.NEXT:
                this.episodes = App.schedule().next().getEpisodes();
                break;
            case ScheduleMode.UPCOMING:
                this.episodes = App.schedule().upcoming().getEpisodes();
                break;
        }

        switch(sortMode) {
            case SortMode.OLDEST_FIRST:
                Collections.sort(this.episodes, EpisodeComparator.byOldestFirst());
                break;
            case SortMode.NEWEST_FIRST:
                Collections.sort(this.episodes, EpisodeComparator.byNewestFirst());
                break;
        }
    }
}