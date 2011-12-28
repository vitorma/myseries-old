/*
 *   EpisodeBuilder.java
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


package br.edu.ufcg.aweseries.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EpisodeBuilder {
    private static final String DEFAULT_STRING = "";
    private static final String DEFAULT_NAME = "Unnamed Episode";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String id;
    private String seriesId;
    private int number;
    private int seasonNumber;
    private String name;
    private Date firstAired;
    private String overview;
    private String director;
    private String writer;
    private String guestStars;
    private String poster;
    private boolean viewed;

    public EpisodeBuilder() {
        this.number = -1;
        this.seasonNumber = -1;
    }

    public EpisodeBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public EpisodeBuilder withSeriesId(String seriesId) {
        this.seriesId = seriesId;
        return this;
    }

    public EpisodeBuilder withNumber(int number) {
        this.number = number;
        return this;
    }

    public EpisodeBuilder withNumber(String number) {
        try {
            this.number = Integer.valueOf(number);
        } catch (NumberFormatException e) {
            //Do nothing - number already is -1;
        }
        return this;
    }

    public EpisodeBuilder withSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
        return this;
    }

    public EpisodeBuilder withSeasonNumber(String seasonNumber) {
        try {
            this.seasonNumber = Integer.valueOf(seasonNumber);
        } catch (NumberFormatException e) {
            //Do nothing - seasonNumber already is -1
        }
        return this;
    }

    public EpisodeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EpisodeBuilder withFirstAired(String firstAired) {
        try {
            this.firstAired = dateFormat.parse(firstAired);
        } catch (Exception e) {
            //Do nothing - firstAired already is null
        }
        return this;
    }

//    public EpisodeBuilder withFirstAired(long firstAired) {
//        this.firstAired = new Date(firstAired);
//        return this;
//    }

    public EpisodeBuilder withOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public EpisodeBuilder withDirector(String director) {
        this.director = director;
        return this;
    }

    public EpisodeBuilder withWriter(String writer) {
        this.writer = writer;
        return this;
    }

    public EpisodeBuilder withGuestStars(String guestStars) {
        this.guestStars = guestStars;
        return this;
    }

    public EpisodeBuilder withPoster(String poster) {
        this.poster = poster;
        return this;
    }

    public EpisodeBuilder withViewed(boolean viewed) {
        this.viewed = viewed;
        return this;
    }

    public Episode build() {
        Episode episode = new Episode(this.id, this.seriesId, this.number, this.seasonNumber);

        episode.setName(this.name != null ? this.name : DEFAULT_NAME);
        episode.setFirstAired(this.firstAired);
        episode.setOverview(this.overview != null ? this.overview : DEFAULT_STRING);
        episode.setDirector(this.director != null ? this.director : DEFAULT_STRING);
        episode.setWriter(this.writer != null ? this.writer : DEFAULT_STRING);
        episode.setGuestStars(this.guestStars != null ? this.guestStars : DEFAULT_STRING);
        episode.setPoster(this.poster != null ? this.poster : DEFAULT_STRING);
        episode.markWetherSeen(this.viewed);

        return episode;
    }
}
