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
import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.aweseries.util.Strings;

public class Episode {

    private String id;
    private String seriesId;
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
    private Set<DomainObjectListener<Episode>> listeners;

    private Episode(String id, String seriesId, int number, int seasonNumber) {
        if (id == null || Strings.isBlank(id)) {
            throw new IllegalArgumentException("invalid id for episode");
        }

        if (seriesId == null || Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("invalid series id for episode");
        }

        if (number < 0) {
            throw new IllegalArgumentException("invalid number for episode");
        }

        if (seasonNumber < 0) {
            throw new IllegalArgumentException("invalid season number for episode");
        }

        this.listeners = new HashSet<DomainObjectListener<Episode>>();

        this.id = id;
        this.seriesId = seriesId;
        this.number = number;
        this.seasonNumber = seasonNumber;
    }

    //Builder factory---------------------------------------------------------------------------------------------------

    public static Episode.Builder builder() {
        return new Episode.Builder();
    }

    //Interface---------------------------------------------------------------------------------------------------------

    public String id() {
        return this.id;
    }

    public String seriesId() {
        return this.seriesId;
    }

    public int number() {
        return this.number;
    }

    public int seasonNumber() {
        return this.seasonNumber;
    }

    public boolean isSpecial() {
        return this.seasonNumber == 0;
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

    public void markSeenAs(boolean seen) {
        this.seen = seen;
        this.notifyListeners();
    }

    public void markAsSeen() {
        this.seen = true;
        this.notifyListeners();
    }

    public void markAsNotSeen() {
        this.seen = false;
        this.notifyListeners();
    }

    public void mergeWith(Episode other) {
        if (other == null) {
            throw new IllegalArgumentException("other should not be null");
        }
        
        if (!other.id.equals(this.id)) {
            throw new IllegalArgumentException("other should have the same id as this");
        }

        if (!other.seriesId.equals(this.seriesId)) {
            throw new IllegalArgumentException("other should have the same seriesId as this");
        }

        if (other.number != this.number) {
            throw new IllegalArgumentException("other should have the same number as this");
        }

        if (other.seasonNumber != this.seasonNumber) {
            throw new IllegalArgumentException("other should have the same seasonNumber as this");
        }

        this.name = other.name;
        this.airdate = other.airdate;
        this.overview = other.overview;
        this.directors = other.directors;
        this.writers = other.writers;
        this.guestStars = other.guestStars;
        this.poster = other.poster;
        
        this.notifyListeners();
    }

    //Object------------------------------------------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Episode) &&
               ((Episode) obj).id.equals(this.id);
    }

    @Override
    public String toString() {
        return this.name;
    }

    //Listeners---------------------------------------------------------------------------------------------------------

    //TODO It's better to have an EpisodeListener interface with methods like:
    //          onMarkedAsSeen(Episode)
    //          onMarkedAsNotSeen(Episode)
    //          onMerged(Episode)
    //
    //     For example, a season can maintain a field for counting how many episodes were seen. This field could be
    //     increased, decreased or not changed, depending on which of the methods above was called.
    //
    //     This approach will provide efficiency to Season#wasSeen, once there will be no need to check all episodes of
    //     a season each time the state of an episode changes.

    private void notifyListeners() {
        for (final DomainObjectListener<Episode> listener : this.listeners) {
            listener.onUpdate(this);            
        }
    }

    public boolean addListener(DomainObjectListener<Episode> listener) {
        return this.listeners.add(listener);
    }
    
    public boolean removeListener(DomainObjectListener<Episode> listener) {
        return this.listeners.remove(listener);
    }

    //Builder-----------------------------------------------------------------------------------------------------------

    public static class Builder {

        private String id;
        private String seriesId;
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
            this.number = -1;
            this.seasonNumber = -1;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withSeriesId(String seriesId) {
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

        public Builder withDirector(String director) {
            this.directors = director;
            return this;
        }

        public Builder withWriter(String writer) {
            this.writers = writer;
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
