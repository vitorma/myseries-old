/*
 *   Series.java
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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.edu.ufcg.aweseries.util.Validate;

public class Series implements SeasonSetListener {
    public static final int INVALID_ID = -1;

    private int id;
    private String name;
    private String status;
    private String airDate;
    private String airDay;
    private String airTime;
    private String runtime;
    private String network;
    private String overview;
    private String genres;
    private String actors;
    private String posterFileName;
    private SeasonSet seasons;

    private List<SeriesListener> listeners;

    //Construction------------------------------------------------------------------------------------------------------

    private Series(int id, String name) {
        Validate.isTrue(id >= 0, "id should be non-negative");
        Validate.isNonBlank(name, "name");

        this.id = id;
        this.name = name;

        this.seasons = new SeasonSet(this.id);
        this.seasons.register(this);
        this.listeners = new LinkedList<SeriesListener>();
    }

    //Building----------------------------------------------------------------------------------------------------------

    public static Series.Builder builder() {
        return new Series.Builder();
    }

    //Immutable---------------------------------------------------------------------------------------------------------

    public int id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    //Mutable-----------------------------------------------------------------------------------------------------------

    public String status() {
        return this.status;
    }

    public String airDate() {
        return this.airDate;
    }

    public String airDay() {
        return this.airDay;
    }

    public String airTime() {
        return this.airTime;
    }

    public String runtime() {
        return this.runtime;
    }

    public String network() {
        return this.network;
    }

    public String overview() {
        return this.overview;
    }

    public String genres() {
        return this.genres;
    }

    public String actors() {
        return this.actors;
    }

    public String posterFileName() {
        return this.posterFileName;
    }

    //Seasons-----------------------------------------------------------------------------------------------------------

    public SeasonSet seasons() {
        return this.seasons;
    }

    public Season season(int number) {
        return this.seasons.season(number);
    }

    //Episodes----------------------------------------------------------------------------------------------------------

    public List<Episode> episodes() {
        return this.seasons.allEpisodes();
    }

    public int numberOfEpisodes() {
        return this.seasons.allEpisodes().size();//TODO this.seasons.numberOfEpisodes();
    }

    public int numberOfSeenEpisodes() {
        return this.seasons.numberOfSeenEpisodes();
    }

    public Episode nextEpisodeToSee() {
        return this.seasons.nextEpisodeToSee();
        
    }

    public Series includingAll(Collection<Episode> episodes) {
        this.seasons.includingAll(episodes);
        return this;
    }

    //Merge-------------------------------------------------------------------------------------------------------------

    public void mergeWith(Series other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(other.id == this.id, "other should have the same id as this");
        Validate.isTrue(other.name.equals(this.name), "other should have the same name as this");

        this.status = other.status;
        this.airDate = other.airDate;
        this.airDay = other.airDay;
        this.airTime = other.airTime;
        this.runtime = other.runtime;
        this.network = other.network;
        this.overview = other.overview;
        this.genres = other.genres;
        this.actors = other.actors;
        this.posterFileName = other.posterFileName;
        this.seasons.mergeWith(other.seasons);

        this.notifyThatWasMerged();
    }

    //SeriesListener----------------------------------------------------------------------------------------------------

    public boolean register(SeriesListener listener) {
        Validate.isNonNull(listener, "listener");

        for (SeriesListener l : this.listeners) {
            if (l == listener) return false;
        }

        return this.listeners.add(listener);
    }

    public boolean deregister(SeriesListener listener) {
        Validate.isNonNull(listener, "listener");

        for (int i = 0; i < this.listeners.size(); i++) {
            if (this.listeners.get(i) == listener) {
                this.listeners.remove(i);
                return true;
            }
        }

        return false;
    }

    private void notifyThatNumberOfSeenEpisodesChanged() {
        for (SeriesListener l : this.listeners) {
            l.onChangeNumberOfSeenEpisodes(this);
        }
    }

    private void notifyThatNextToSeeChanged() {
        for (SeriesListener l : this.listeners) {
            l.onChangeNextEpisodeToSee(this);
        }
    }

    private void notifyThatWasMerged() {
        for (SeriesListener l : this.listeners) {
            l.onMerge(this);
        }
    }

    //SeasonSetListener-------------------------------------------------------------------------------------------------

    @Override
    public void onChangeNextEpisodeToSee(SeasonSet seasonSet) {
        this.notifyThatNextToSeeChanged();
    }
    
    @Override
    public void onChangeNumberOfSeenEpisodes(SeasonSet seasonSet) {
        this.notifyThatNumberOfSeenEpisodesChanged();
        
    }

    @Override
    public void onMerge(SeasonSet seasonSet) {
        //Series is not interested in this event
    }

    //Object------------------------------------------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Series && ((Series) obj).id == this.id;
    }

    //Builder-----------------------------------------------------------------------------------------------------------

    public static class Builder {
        private int id;
        private String name;
        private String status;
        private String airDate;
        private String airDay;
        private String airTime;
        private String runtime;
        private String network;
        private String overview;
        private String genres;
        private String actors;
        private String posterFileName;

        private Set<Episode> episodes;

        private Builder() {
            this.id = Series.INVALID_ID;
            this.episodes = new HashSet<Episode>();
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withAirDate(String airDate) {
            this.airDate = airDate;
            return this;
        }

        public Builder withAirDay(String airDay) {
            this.airDay = airDay;
            return this;
        }

        public Builder withAirTime(String airTime) {
            this.airTime = airTime;
            return this;
        }

        public Builder withRuntime(String runtime) {
            this.runtime = runtime;
            return this;
        }

        public Builder withNetwork(String network) {
            this.network = network;
            return this;
        }

        public Builder withOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder withGenres(String genres) {
            this.genres = genres;
            return this;
        }

        public Builder withActors(String actors) {
            this.actors = actors;
            return this;
        }

        public Builder withPosterFileName(String posterFileName) {
            this.posterFileName = posterFileName;
            return this;
        }

        public Builder withEpisode(Episode episode) {
            this.episodes.add(episode);
            return this;
        }

        public Series build() {
            final Series series = new Series(this.id, this.name);

            series.status = this.status;
            series.airDay = this.airDay;
            series.airTime = this.airTime;
            series.airDate = this.airDate;
            series.runtime = this.runtime;
            series.network = this.network;
            series.overview = this.overview;
            series.genres = this.genres;
            series.actors = this.actors;
            series.posterFileName = this.posterFileName;

            return series.includingAll(this.episodes);
        }
    }
}
