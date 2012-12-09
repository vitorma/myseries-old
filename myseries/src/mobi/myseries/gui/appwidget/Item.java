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

import java.text.DateFormat;
import java.util.Date;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.shared.Android;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Strings;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
        String episodeAirdate = this.relativeTimeStringFor(episode.airDate());

        item.setTextViewText(R.id.episodeAirDate, episodeAirdate);
    }

    private void setUpAirtimeAndNetwork(RemoteViews item, Series series) {
        DateFormat airtimeFormat = android.text.format.DateFormat.getTimeFormat(this.context);
        String airtime = DatesAndTimes.toString(series.airtime(), airtimeFormat, "");
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

    //TODO (Cleber) Move this method to a better place
    private String relativeTimeStringFor(Date airDate) {
        if (airDate == null) {
            return "";
        }

        int days = DatesAndTimes.daysBetween(DatesAndTimes.today(), airDate);

        switch (days) {
            case 0:
                return this.context.getString(R.string.relative_time_today);
            case 1:
                return this.context.getString(R.string.relative_time_tomorrow);
            case -1:
                return this.context.getString(R.string.relative_time_yesterday);
            default:
                if (Math.abs(days) >= DatesAndTimes.DAYS_IN_A_WEEK) {
                    DateFormat dateformat = android.text.format.DateFormat.getDateFormat(this.context);
                    String formattedDate = DatesAndTimes.toString(airDate, dateformat, "");
                    return formattedDate;
                }

                if (days > 0) {
                    return String.format(App.context().getString(R.string.relative_time_future), days);
                }

                return String.format(this.context.getString(R.string.relative_time_past), Math.abs(days));
        }
    }
}
