/*
 *   Series.java
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

import java.util.HashSet;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.util.Strings;

public class Series implements DomainObjectListener<SeasonSet> {

    public static class Builder {
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

        public Builder withId(String id) {
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

        public Builder withAirsDay(String airsDay) {
            this.airsDay = airsDay;
            return this;
        }

        public Builder withAirsTime(String airsTime) {
            this.airsTime = airsTime;
            return this;
        }

        public Builder withFirstAired(String firstAired) {
            this.firstAired = firstAired;
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

        public Builder withPoster(Bitmap posterBitmap) {
            this.posterBitmap = posterBitmap;
            return this;
        }

        public Builder withPoster(byte[] posterBitmap) {
            if (posterBitmap != null) {
                this.posterBitmap = BitmapFactory.decodeByteArray(posterBitmap, 0, posterBitmap.length);
            }
            return this;
        }

        public Builder withEpisode(Episode episode) {
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
    private Poster poster;
    private SeasonSet seasons;
    private Set<DomainObjectListener<Series>> listeners;

    public Series(String id, String name) {
        if (id == null || Strings.isBlank(id)) {
            throw new IllegalArgumentException("invalid id for series");
        }

        if (name == null || Strings.isBlank(name)) {
            throw new IllegalArgumentException("invalid name for series");
        }

        this.id = id;
        this.name = name;
        
        this.listeners = new HashSet<DomainObjectListener<Series>>();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;
    }

    public String getAirsDay() {
        return this.airsDay;
    }

    public String getAirsTime() {
        return this.airsTime;
    }

    public String getAirsDayAndTime() {
        return Strings.isBlank(this.getAirsDay())
               ? ""
               : this.getAirsDay().substring(0, 3) + " " + this.getAirsTime();
    }

    public String getFirstAired() {
        return this.firstAired;
    }

    public String getRuntime() {
        return this.runtime;
    }

    public String getNetwork() {
        return this.network;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getGenres() {
        return this.genres;
    }

    public String getActors() {
        return this.actors;
    }

    public Poster getPoster() {
        return this.poster;
    }

    public SeasonSet getSeasons() {
        return this.seasons;
    }

    public void setStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("invalid status for series");
        }

        this.status = status;
    }

    public void setAirsDay(String airsDay) {
        if (airsDay == null) {
            throw new IllegalArgumentException("invalid airs day for series");
        }

        this.airsDay = airsDay;
    }

    public void setAirsTime(String airsTime) {
        if (airsTime == null) {
            throw new IllegalArgumentException("invalid airs time for series");
        }

        this.airsTime = airsTime;
    }

    public void setFirstAired(String firstAired) {
        if (firstAired == null) {
            throw new IllegalArgumentException("invalid first aired for series");
        }

        this.firstAired = firstAired;
    }

    public void setRuntime(String runtime) {
        if (runtime == null) {
            throw new IllegalArgumentException("invalid runtime for series");
        }

        this.runtime = runtime;
    }

    public void setNetwork(String network) {
        if (network == null) {
            throw new IllegalArgumentException("invalid network for series");
        }

        this.network = network;
    }

    public void setOverview(String overview) {
        if (overview == null) {
            throw new IllegalArgumentException("invalid overview for series");
        }

        this.overview = overview;
    }

    public void setGenres(String genres) {
        if (genres == null) {
            throw new IllegalArgumentException("invalid genres for series");
        }

        this.genres = genres;
    }

    public void setActors(String actors) {
        if (actors == null) {
            throw new IllegalArgumentException("invalid actors for series");
        }

        this.actors = actors;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    public void setSeasons(SeasonSet seasons) {
        if ((seasons == null) || !seasons.getSeriesId().equals(this.id)) {
            throw new IllegalArgumentException("invalid seasons for series");
        }

        seasons.addListener(this);
        this.seasons = seasons;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Series) &&
               ((Series) obj).getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return this.getName();
    }

    //TODO: Test
    public boolean hasPoster() {
        return this.poster != null;
    }

    //TODO Test
    public boolean isContinuing() {
        return this.status.equals("Continuing");
    }

    //TODO Test
    public boolean isEnded() {
        return this.status.equals("Ended");
    }

    @Override
    public void onUpdate(SeasonSet entity) {
        this.notifyListeners();
    }

    //TODO Test
    public boolean addListener(DomainObjectListener<Series> listener) {
        return this.listeners.add(listener);
    }

    //TODO Test
    public boolean removeListener(DomainObjectListener<Series> listener) {
        return this.listeners.remove(listener);
    }
    
    //TODO: Test me
    //      Test whether other has a different seriesId than mine (its)
    public void mergeWith(Series other) {
        if (other == null) {
            throw new IllegalArgumentException(); //TODO: create a custom Exception
        }

        this.name = other.name;
        this.status = other.status;
        this.airsDay = other.airsDay;
        this.airsTime = other.airsTime;
        this.firstAired = other.firstAired;
        this.runtime = other.runtime;
        this.network = other.network;
        this.overview = other.overview;
        this.genres = other.genres;
        this.actors = other.actors;
        this.poster = other.poster;
        this.seasons.mergeWith(other.seasons);

        this.notifyListeners();
    }

    private void notifyListeners() {
        for (DomainObjectListener<Series> listener : this.listeners) {
            listener.onUpdate(this);            
        }        
    }
}
