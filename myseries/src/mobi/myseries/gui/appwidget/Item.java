/*
 *   Item.java
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
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.shared.Android;
import mobi.myseries.shared.Dates;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

public class Item {
    private Context context;

    private Item(Context context) {
        this.context = context;
    }

    public static Item from(Context context) {
        return new Item(context);
    }

    public RemoteViews empty() {
        return new RemoteViews(this.context.getPackageName(), R.layout.appwidget_empty_view);
    }

    public RemoteViews loading() {
        return new RemoteViews(this.context.getPackageName(), R.layout.appwidget_loading_view);
    }

    public RemoteViews createFor(Episode episode) {
        Series series = App.getSeries(episode.seriesId());

        RemoteViews item = new RemoteViews(this.context.getPackageName(), R.layout.appwidget_myschedule_item);

        this.setupSeriesPoster(item, series);
        this.setupSeriesName(item, series);
        this.setupEpisodeNumber(item, episode);
        this.setupEpisodeAirdate(item, episode);
        this.setupOnClickIntent(item, episode);

        return item;
    }

    private void setupSeriesPoster(RemoteViews item, Series series) {
        item.setImageViewBitmap(R.id.seriesPoster, App.seriesPoster(series.id()));
    }

    private void setupSeriesName(RemoteViews item, Series series) {
        item.setTextViewText(R.id.seriesName, series.name());
    }

    private void setupEpisodeNumber(RemoteViews item, Episode episode) {
        String format = this.context.getString(R.string.episode_number_format);
        String episodeNumber = String.format(format, episode.seasonNumber(), episode.number());

        item.setTextViewText(R.id.episodeNumber, episodeNumber);
    }

    private void setupEpisodeAirdate(RemoteViews item, Episode episode) {
        String unavailableDate = this.context.getString(R.string.unavailable_date);
        String episodeAirdate = Dates.toString(episode.airDate(), App.dateFormat(), unavailableDate);

        item.setTextViewText(R.id.episodeAirDate, episodeAirdate);
    }

    private void setupOnClickIntent(RemoteViews item, Episode episode) {
        if (Android.isHoneycombOrHigher()) {
            this.setupOnClickFillInIntent(item, episode);
        } else {
            this.setupOnClickPendingIntent(item, episode);
        }
    }

    private void setupOnClickPendingIntent(RemoteViews item, Episode episode) {
        Intent intent = EpisodesActivity.newIntent(
                this.context, episode.seriesId(), episode.seasonNumber(), episode.number());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this.context, this.requestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        item.setOnClickPendingIntent(R.id.itemPanel, pendingIntent);
    }

    private void setupOnClickFillInIntent(RemoteViews item, Episode episode) {
        Bundle extras = new Bundle();

        extras.putInt(EpisodesActivity.Extra.SERIES_ID, episode.seriesId());
        extras.putInt(EpisodesActivity.Extra.SEASON_NUMBER, episode.seasonNumber());
        extras.putInt(EpisodesActivity.Extra.EPISODE_NUMBER, episode.number());

        Intent intent = new Intent().putExtras(extras);

        item.setOnClickFillInIntent(R.id.itemPanel, intent);
    }

    private int requestCode() {
        return (int) System.currentTimeMillis();
    }
}