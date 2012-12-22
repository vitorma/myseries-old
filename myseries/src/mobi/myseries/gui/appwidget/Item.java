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
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.shared.Android;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.RelativeDay;
import mobi.myseries.shared.Strings;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

public class Item {
    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterFrom(App.resources());
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
        Series series = App.seriesProvider().getSeries(episode.seriesId());

        RemoteViews item = new RemoteViews(this.context.getPackageName(), R.layout.appwidget_item);

        this.setUpSeriesPoster(item, series);
        this.setUpEpisodeAirdate(item, episode);
        this.setUpAirtimeAndNetwork(item, series);
        this.setUpSeriesName(item, series);
        this.setUpEpisodeName(item, episode);
        this.setUpOnClickIntent(item, episode);

        return item;
    }

    private void setUpSeriesPoster(RemoteViews item, Series series) {
        Bitmap seriesPoster = App.imageService().getSmallPosterOf(series);

        item.setImageViewBitmap(R.id.seriesPoster, Objects.nullSafe(seriesPoster, GENERIC_POSTER));
    }

    private void setUpEpisodeAirdate(RemoteViews item, Episode episode) {
        RelativeDay relativeAirDay = DatesAndTimes.parse(episode.airDate(), null);
        String airDate = DatesAndTimes.toString(episode.airDate(), DateFormat.getDateFormat(this.context), "");

        item.setTextViewText(R.id.episodeAirDate, LocalText.of(relativeAirDay, airDate));
    }

    private void setUpAirtimeAndNetwork(RemoteViews item, Series series) {
        String airtime = DatesAndTimes.toString(series.airtime(), DateFormat.getTimeFormat(this.context), "");
        String network = Strings.isBlank(series.network()) ? "" : " " + series.network();

        item.setTextViewText(R.id.airtimeAndNetwork, airtime + network);
    }

    private void setUpSeriesName(RemoteViews item, Series series) {
        item.setTextViewText(R.id.seriesName, series.name());
    }

    private void setUpEpisodeName(RemoteViews item, Episode episode) {
        String format = this.context.getString(R.string.episode_number_format);
        String episodeNumber = String.format(format, episode.seasonNumber(), episode.number());

        item.setTextViewText(R.id.episodeNumber, episodeNumber + " " + episode.name());
    }

    private void setUpOnClickIntent(RemoteViews item, Episode episode) {
        if (Android.isHoneycombOrHigher()) {
            this.setupOnClickFillInIntent(item, episode);
        } else {
            this.setUpOnClickPendingIntent(item, episode);
        }
    }

    private void setUpOnClickPendingIntent(RemoteViews item, Episode episode) {
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
