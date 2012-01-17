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

package br.edu.ufcg.aweseries.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.edu.ufcg.aweseries.util.Validate;

public class Episode {
    private int id;
    private int seriesId;
    private int number;
    private int seasonNumber;
    private String name;
    private Date airdate;
    private String overview;
    private String directors;
    private String writers;
    private String guestStars;
    private String poster;

    private boolean seen;
    private List<EpisodeListener> listeners; 

    private Episode(int id, int seriesId, int number, int seasonNumber) {
        Validate.isTrue(id >= 0, "id should be non-negative");
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");
        Validate.isTrue(number >= 0, "number should be non-negative");
        Validate.isTrue(seasonNumber >= 0, "seasonNumber should be non-negative");

        this.id = id;
        this.seriesId = seriesId;
        this.number = number;
        this.seasonNumber = seasonNumber;

        this.listeners = new LinkedList<EpisodeListener>();
    }

    //Builder factory---------------------------------------------------------------------------------------------------

    public static Episode.Builder builder() {
        return new Episode.Builder();
    }

    //Interface---------------------------------------------------------------------------------------------------------

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

    public String name() {
        return this.name;
    }

    public Date airdate() {
        return this.airdate;
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

    public String poster() {
        return this.poster;
    }

    public boolean wasSeen() {
        return this.seen;
    }

    public void markAsSeen() {
        if (!seen) {
            this.seen = true;
            this.notifyOfMarkAsSeen();
        }
    }

    public void markAsNotSeen() {
        if (seen) {
            this.seen = false;
            this.notifyOfMarkAsNotSeen();
        }
    }

    public void mergeWith(Episode other) {
        Validate.isNonNull(other, "other should be non-null");
        Validate.isTrue(other.id == this.id, "other should have the same id as this");
        Validate.isTrue(other.seriesId == this.seriesId, "other should have the same seriesId as this");
        Validate.isTrue(other.number == this.number, "other should have the same number as this");
        Validate.isTrue(other.seasonNumber == this.seasonNumber, "other should have the same seasonNumber as this");

        this.name = other.name;
        this.airdate = other.airdate;
        this.overview = other.overview;
        this.directors = other.directors;
        this.writers = other.writers;
        this.guestStars = other.guestStars;
        this.poster = other.poster;

        this.notifyOfMerge();
    }

    //Listeners---------------------------------------------------------------------------------------------------------
    
    public boolean register(EpisodeListener listener) {
        return !this.isRegistered(listener) && this.listeners.add(listener);
    }

    public boolean deregister(EpisodeListener listener) {
        return this.isRegistered(listener) && this.listeners.remove(listener);
    }

    private boolean isRegistered(EpisodeListener listener) {
        Validate.isNonNull(listener, "listener should be non-null");

        for (EpisodeListener l : this.listeners) {
            if (l == listener) return true;
        }
        
        return false;
    }

    private void notifyOfMarkAsSeen() {
        for (EpisodeListener listener : this.listeners) {
            listener.onMarkAsSeen(this);
        }
    }

    private void notifyOfMarkAsNotSeen() {
        for (EpisodeListener listener : this.listeners) {
            listener.onMarkAsNotSeen(this);
        }
    }

    private void notifyOfMerge() {
        for (EpisodeListener listener : this.listeners) {
            listener.onMerge(this);
        }
    }
    
    //Object------------------------------------------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Episode) && ((Episode) obj).id == this.id;
    }

    //Builder-----------------------------------------------------------------------------------------------------------

    public static class Builder {
        private static final int INVALID_ID = -1;
        private static final int INVALID_SERIES_ID = -1;
        private static final int INVALID_NUMBER = -1;
        private static final int INVALID_SEASON_NUMBER = -1;

        private int id;
        private int seriesId;
        private int number;
        private int seasonNumber;
        private String name;
        private Date airdate;
        private String overview;
        private String directors;
        private String writers;
        private String guestStars;
        private String poster;
        private boolean seen;

        private Builder() {
            this.id = INVALID_ID;
            this.seriesId = INVALID_SERIES_ID;
            this.number = INVALID_NUMBER;
            this.seasonNumber = INVALID_SEASON_NUMBER;
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

        public Builder withAirdate(Date airdate) {
            this.airdate = airdate;
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

        public Builder withPoster(String poster) {
            this.poster = poster;
            return this;
        }

        public Builder withSeen(boolean seen) {
            this.seen = seen;
            return this;
        }

        public Episode build() {
            Episode episode = new Episode(this.id, this.seriesId, this.number, this.seasonNumber);

            episode.name = this.name;
            episode.airdate = this.airdate;
            episode.overview = this.overview;
            episode.directors = this.directors;
            episode.writers = this.writers;
            episode.guestStars = this.guestStars;
            episode.poster = this.poster;
            episode.seen = this.seen;

            return episode;
        }
    }
}
