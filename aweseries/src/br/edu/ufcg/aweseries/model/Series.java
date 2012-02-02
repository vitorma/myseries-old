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

import java.util.HashSet;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.util.Strings;
import br.edu.ufcg.aweseries.util.Validate;

public class Series implements DomainObjectListener<SeasonSet> {
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
    private Poster poster;
    private SeasonSet seasons;

    private Set<DomainObjectListener<Series>> listeners;//TODO List<SeriesListener>

    private Series(int id, String name) {
        Validate.isTrue(id >= 0, "id should be non-negative");

        if (name == null || Strings.isBlank(name))//TODO Use Validate
            throw new IllegalArgumentException("invalid name for series");

        this.id = id;
        this.name = name;

        this.listeners = new HashSet<DomainObjectListener<Series>>();
    }

    public int id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

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

    //TODO move this to GUI
    public String airsDayAndTime() {
        return Strings.isBlank(this.airDay()) ? "" : this.airDay().substring(0, 3) + " "
                + this.airTime();
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

    public Poster poster() {
        return this.poster;
    }

    public SeasonSet seasons() {
        return this.seasons;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Series && ((Series) obj).id == this.id;
    }

    //TODO Remove
    @Override
    public String toString() {
        return this.name;
    }

    //TODO Remove
    public boolean hasPoster() {
        return this.poster != null;
    }

    //TODO Implement SeasonSetListener
    @Override
    public void onUpdate(SeasonSet entity) {
        this.notifyListeners();
    }

    //TODO Register SeriesListener
    public boolean addListener(DomainObjectListener<Series> listener) {
        return this.listeners.add(listener);
    }

    //TODO Deregister SeriesListener
    public boolean removeListener(DomainObjectListener<Series> listener) {
        return this.listeners.remove(listener);
    }

    //FIXME Check that other.id == this.id
    public void mergeWith(Series other) {
        if (other == null)
            throw new IllegalArgumentException(); //TODO: create a custom Exception

        this.name = other.name;
        this.status = other.status;
        this.airDate = other.airDate;
        this.airDay = other.airDay;
        this.airTime = other.airTime;
        this.runtime = other.runtime;
        this.network = other.network;
        this.overview = other.overview;
        this.genres = other.genres;
        this.actors = other.actors;
        this.poster = other.poster;
        this.seasons.mergeWith(other.seasons);

        this.notifyListeners();
    }

    //TODO Specific notifications to SeriesListener
    private void notifyListeners() {
        for (DomainObjectListener<Series> listener : this.listeners) {
            listener.onUpdate(this);
        }
    }

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
        private Bitmap posterBitmap;//TODO Keep a Poster instead of a Bitmap
        private SeasonSet seasons;//TODO Create at the Series' constructor

        //TODO Turn private: create public static Series.Builder Series.builder()
        public Builder() {
            this.id = Series.INVALID_ID;
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

        //TODO withPoster(Poster)

        //TODO Remove
        public Builder withPoster(Bitmap posterBitmap) {
            this.posterBitmap = posterBitmap;
            return this;
        }

        //TODO Remove
        public Builder withPoster(byte[] posterBitmap) {
            if (posterBitmap != null) {
                this.posterBitmap = BitmapFactory.decodeByteArray(posterBitmap, 0,
                        posterBitmap.length);
            }
            return this;
        }

        //FIXME episode.seriesId == this.id (Be careful! withId was already called?)
        //      It is better keeping a collection of episodes and including all ones in a SeasonSet created at the
        //      Series' constructor.
        public Builder withEpisode(Episode episode) {
            if (episode == null)
                return this;

            if (this.seasons == null) {
                this.seasons = new SeasonSet(episode.seriesId());
            }

            this.seasons.addEpisode(episode);
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
            series.poster = this.posterBitmap != null ? new Poster(this.posterBitmap) : null;// TODO assign this.poster
            series.seasons = this.seasons != null ? this.seasons : new SeasonSet(this.id);//TODO series.seasons.includingAll

            return series;
        }
    }
}
