/*
 *   SeriesBuilder.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SeriesBuilder {
    private static final String DEFAULT_STRING = "";

    private String id;
    private String name;
    private String status;
    private String airsDay;
    private String airsTime;
    private String firstAired;
    private String runtime;
    private String network;
    private String overview;
    private String genres;
    private String actors;
    private Bitmap posterBitmap;
    private SeasonSet seasons;

    public SeriesBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public SeriesBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SeriesBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public SeriesBuilder withAirsDay(String airsDay) {
        this.airsDay = airsDay;
        return this;
    }

    public SeriesBuilder withAirsTime(String airsTime) {
        this.airsTime = airsTime;
        return this;
    }

    public SeriesBuilder withFirstAired(String firstAired) {
        this.firstAired = firstAired;
        return this;
    }

    public SeriesBuilder withRuntime(String runtime) {
        this.runtime = runtime;
        return this;
    }

    public SeriesBuilder withNetwork(String network) {
        this.network = network;
        return this;
    }

    public SeriesBuilder withOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public SeriesBuilder withGenres(String genres) {
        this.genres = genres;
        return this;
    }

    public SeriesBuilder withActors(String actors) {
        this.actors = actors;
        return this;
    }

    public SeriesBuilder withPoster(Bitmap posterBitmap) {
        this.posterBitmap = posterBitmap;
        return this;
    }

    public SeriesBuilder withPoster(byte[] posterBitmap) {
        if (posterBitmap != null) {
            this.posterBitmap = BitmapFactory.decodeByteArray(posterBitmap, 0, posterBitmap.length);
        }
        return this;
    }

    public SeriesBuilder withEpisode(Episode episode) {
        if (episode == null) {
            return this;
        }

        if (this.seasons == null) {
            this.seasons = new SeasonSet(episode.getSeriesId());
        }

        this.seasons.addEpisode(episode);
        return this;
    }

    public Series build() {
        final Series series = new Series(this.id, this.name);

        series.setStatus(this.status != null ? this.status : DEFAULT_STRING);
        series.setAirsDay(this.airsDay != null ? this.airsDay : DEFAULT_STRING);
        series.setAirsTime(this.airsTime != null ? this.airsTime : DEFAULT_STRING);
        series.setFirstAired(this.firstAired != null ? this.firstAired : DEFAULT_STRING);
        series.setRuntime(this.runtime != null ? this.runtime : DEFAULT_STRING);
        series.setNetwork(this.network != null ? this.network : DEFAULT_STRING);
        series.setOverview(this.overview != null ? this.overview : DEFAULT_STRING);
        series.setGenres(this.genres != null ? this.genres : DEFAULT_STRING);
        series.setActors(this.actors != null ? this.actors : DEFAULT_STRING);
        series.setPoster(this.posterBitmap != null ? new Poster(this.posterBitmap) : null);
        series.setSeasons(this.seasons != null ? this.seasons : new SeasonSet(this.id));

        return series;
    }
}
