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

import br.edu.ufcg.aweseries.util.Strings;

public class Series implements DomainEntityListener<SeasonSet> {
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
    private Set<DomainEntityListener<Series>> listeners;

    public Series(String id, String name) {
        if (id == null || Strings.isBlank(id)) {
            throw new IllegalArgumentException("invalid id for series");
        }

        if (name == null || Strings.isBlank(name)) {
            throw new IllegalArgumentException("invalid name for series");
        }

        this.id = id;
        this.name = name;
        
        this.listeners = new HashSet<DomainEntityListener<Series>>();
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
        this.notifyListeners();
    }

    public void setAirsDay(String airsDay) {
        if (airsDay == null) {
            throw new IllegalArgumentException("invalid airs day for series");
        }

        this.airsDay = airsDay;
        this.notifyListeners();
    }

    public void setAirsTime(String airsTime) {
        if (airsTime == null) {
            throw new IllegalArgumentException("invalid airs time for series");
        }

        this.airsTime = airsTime;
        this.notifyListeners();
    }

    public void setFirstAired(String firstAired) {
        if (firstAired == null) {
            throw new IllegalArgumentException("invalid first aired for series");
        }

        this.firstAired = firstAired;
        this.notifyListeners();
    }

    public void setRuntime(String runtime) {
        if (runtime == null) {
            throw new IllegalArgumentException("invalid runtime for series");
        }

        this.runtime = runtime;
        this.notifyListeners();
    }

    public void setNetwork(String network) {
        if (network == null) {
            throw new IllegalArgumentException("invalid network for series");
        }

        this.network = network;
        this.notifyListeners();
    }

    public void setOverview(String overview) {
        if (overview == null) {
            throw new IllegalArgumentException("invalid overview for series");
        }

        this.overview = overview;
        this.notifyListeners();
    }

    public void setGenres(String genres) {
        if (genres == null) {
            throw new IllegalArgumentException("invalid genres for series");
        }

        this.genres = genres;
        this.notifyListeners();
    }

    public void setActors(String actors) {
        if (actors == null) {
            throw new IllegalArgumentException("invalid actors for series");
        }

        this.actors = actors;
        this.notifyListeners();
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
        this.notifyListeners();
    }

    public void setSeasons(SeasonSet seasons) {
        if ((seasons == null) || !seasons.getSeriesId().equals(this.id)) {
            throw new IllegalArgumentException("invalid seasons for series");
        }

        seasons.addListener(this);
        this.seasons = seasons;
        this.notifyListeners();
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
    public boolean addListener(DomainEntityListener<Series> listener) {
        return this.listeners.add(listener);
    }

    //TODO Test
    public boolean removeListener(DomainEntityListener<Series> listener) {
        return this.listeners.remove(listener);
    }
    
    //TODO: Test me
    public void mergeWith(Series other) {
        if (other == null) {
            throw new IllegalArgumentException(); //TODO: create a custom Exception
        }
        
        for (Season season : this.seasons) {
            Season otherSeason = other.seasons.getSeason(season.getNumber());
            if (otherSeason != null) {
                season.mergeWith(otherSeason);
            }
        }
        
        for (Season otherSeason : other.seasons) {
            if (this.seasons.getSeason(otherSeason.getNumber()) == null) {
                this.seasons.addAllEpisodes(otherSeason.getEpisodes());
            }
        }
        
        this.notifyListeners();
        
    }

    private void notifyListeners() {
        for (DomainEntityListener<Series> listener : this.listeners) {
            listener.onUpdate(this);            
        }        
    }
}
