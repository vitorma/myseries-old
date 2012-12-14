/*
 *   Episode.java
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

package mobi.myseries.domain.model;

import java.util.Date;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;

public class Episode implements Publisher<EpisodeListener> {
    private int id;
    private int seriesId;
    private int number;
    private int seasonNumber;
    private String name;
    private Date airDate;
    private Time airtime;
    private String overview;
    private String directors;
    private String writers;
    private String guestStars;
    private String imageFileName;
    private boolean seenMark;
    private ListenerSet<EpisodeListener> listeners;
    private boolean beingMarkedBySeason;

    private Episode(int id, int seriesId, int number, int seasonNumber) {
        Validate.isTrue(id >= 0, "id should be non-negative");
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");
        Validate.isTrue(number >= 0, "number should be non-negative");
        Validate.isTrue(seasonNumber >= 0, "seasonNumber should be non-negative");

        this.id = id;
        this.seriesId = seriesId;
        this.number = number;
        this.seasonNumber = seasonNumber;

        this.listeners = new ListenerSet<EpisodeListener>();
    }

    public static Episode.Builder builder() {
        return new Episode.Builder();
    }

    public int id() {
        return this.id;
    }

    public int seriesId() {
        return this.seriesId;
    }

    public int number() {
        return this.number;
    }

    public int seasonNumber() {
        return this.seasonNumber;
    }

    public boolean isSpecial() {
        return this.seasonNumber == Season.SPECIAL_EPISODES_SEASON_NUMBER;
    }

    public boolean isNotSpecial() {
        return !this.isSpecial();
    }

    public String name() {
        return this.name;
    }

    public Date airDate() {
        return this.airDate;
    }

    public Time airtime() {
        return this.airtime;
    }

    public Episode withAirtime(Time airtime) {
        this.airtime = airtime;
        return this;
    }

    public String overview() {
        return this.overview;
    }

    public String directors() {
        return this.directors;
    }

    public String writers() {
        return this.writers;
    }

    public String guestStars() {
        return this.guestStars;
    }

    public String imageFileName() {
        return this.imageFileName;
    }

    public boolean wasSeen() {
        return this.seenMark;
    }

    public boolean wasNotSeen() {
        return !this.seenMark;
    }

    public void markAsSeen() {
        if (!this.seenMark) {
            this.seenMark = true;
            this.notifyThatWasMarkedAsSeen();
        }
    }

    public void markAsNotSeen() {
        if (this.seenMark) {
            this.seenMark = false;
            this.notifyThatWasMarkedAsNotSeen();
        }
    }

    void setBeingMarkedBySeason(boolean b) {
        this.beingMarkedBySeason = b;
    }

    public void mergeWith(Episode other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(other.id == this.id, "other should have the same id as this");
        Validate.isTrue(other.seriesId == this.seriesId, "other should have the same seriesId as this");
        Validate.isTrue(other.seasonNumber == this.seasonNumber, "other should have the same seasonNumber as this");

        this.number = other.number;
        this.name = other.name;
        this.airDate = other.airDate;
        this.airtime = other.airtime;
        this.overview = other.overview;
        this.directors = other.directors;
        this.writers = other.writers;
        this.guestStars = other.guestStars;
        this.imageFileName = other.imageFileName;
    }

    @Override
    public boolean register(EpisodeListener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(EpisodeListener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyThatWasMarkedAsSeen() {
        for (EpisodeListener listener : this.listeners) {
            if (this.beingMarkedBySeason) {
                listener.onMarkAsSeenBySeason(this);
            } else {
                listener.onMarkAsSeen(this);
            }
        }
    }

    private void notifyThatWasMarkedAsNotSeen() {
        for (EpisodeListener listener : this.listeners) {
            if (this.beingMarkedBySeason) {
                listener.onMarkAsNotSeenBySeason(this);
            } else {
                listener.onMarkAsNotSeen(this);
            }
        }
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (obj.getClass() != Episode.class)) {
            return false;
        }

        Episode other = (Episode) obj;

        return this.id == other.id;
    }

    public static class Builder {
        private int id;
        private int seriesId;
        private int number;
        private int seasonNumber;
        private String name;
        private Date airDate;
        private Time airtime;
        private String overview;
        private String directors;
        private String writers;
        private String guestStars;
        private String imageFileName;
        private boolean seenMark;

        private Builder() {
            this.id = Invalid.EPISODE_ID;
            this.seriesId = Invalid.SERIES_ID;
            this.number = Invalid.EPISODE_NUMBER;
            this.seasonNumber = Invalid.SEASON_NUMBER;
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withSeriesId(int seriesId) {
            this.seriesId = seriesId;
            return this;
        }

        public Builder withNumber(int number) {
            this.number = number;
            return this;
        }

        public Builder withSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAirDate(Date airDate) {
            this.airDate = airDate;
            return this;
        }

        public Builder withAirtime(Time airtime) {
            this.airtime = airtime;
            return this;
        }

        public Builder withOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder withDirectors(String directors) {
            this.directors = directors;
            return this;
        }

        public Builder withWriters(String writers) {
            this.writers = writers;
            return this;
        }

        public Builder withGuestStars(String guestStars) {
            this.guestStars = guestStars;
            return this;
        }

        public Builder withImageFileName(String imageFileName) {
            this.imageFileName = imageFileName;
            return this;
        }

        public Builder withSeenMark(boolean seenMark) {
            this.seenMark = seenMark;
            return this;
        }

        public Episode build() {
            Episode episode = new Episode(this.id, this.seriesId, this.number, this.seasonNumber);

            episode.name = this.name;
            episode.airDate = this.airDate;
            episode.airtime = this.airtime;
            episode.overview = this.overview;
            episode.directors = this.directors;
            episode.writers = this.writers;
            episode.guestStars = this.guestStars;
            episode.imageFileName = this.imageFileName;
            episode.seenMark = this.seenMark;

            return episode;
        }
    }
}
