/*
 *   Episode.java
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.edu.ufcg.aweseries.util.Strings;

public class Episode {
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

    private boolean seen;

    private Set<DomainObjectListener<Episode>> listeners;

    public Episode(String id, String seriesId, int number, int seasonNumber) {
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
        this.setDirector("");
        this.setGuestStars("");
        this.setName("");
        this.setOverview("");
        this.setPoster("");
        this.setWriter("");
        this.markWetherSeen(false);
    }

    public String getId() {
        return this.id;
    }

    public String getSeriesId() {
        return this.seriesId;
    }

    public int getNumber() {
        return this.number;
    }

    public int getSeasonNumber() {
        return this.seasonNumber;
    }

    public boolean isSpecial() {
        return this.getSeasonNumber() == 0;
    }

    public String getName() {
        return this.name;
    }

    public Date getFirstAired() {
        return this.firstAired;
    }

    public boolean hasFirstAired() {
        return this.firstAired != null;
    }

    public String getFirstAiredAsString() {
        return (this.hasFirstAired()) ? dateFormat.format(this.getFirstAired()) : "";
    }

    public boolean airedBefore(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.hasFirstAired() ? (this.getFirstAired().compareTo(date) < 0) : false;
    }

    public boolean airedUntil(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.hasFirstAired() ? (this.getFirstAired().compareTo(date) <= 0) : false;
    }

    public boolean airedAt(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.hasFirstAired() ? (this.getFirstAired().compareTo(date) == 0) : false;
    }

    public boolean airedFrom(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.hasFirstAired() ? (this.getFirstAired().compareTo(date) >= 0) : false;
    }

    public boolean airedAfter(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.hasFirstAired() ? (this.getFirstAired().compareTo(date) > 0) : false;
    }

    public int compareByDateTo(Episode other) {
        if (!this.hasFirstAired()) {
            return other.hasFirstAired() ? 1 : this.compareByNumberTo(other);
        }

        return other.hasFirstAired() ? this.getFirstAired().compareTo(other.getFirstAired()) : -1;
    }

    public int compareByNumberTo(Episode other) {
        return (this.getSeasonNumber() != other.getSeasonNumber())
               ? (this.getSeasonNumber() - other.getSeasonNumber())
               : (this.getNumber() - other.getNumber());
    }

    public String getOverview() {
        return this.overview;
    }

    public String getDirector() {
        return this.director;
    }

    public String getWriter() {
        return this.writer;
    }

    public String getGuestStars() {
        return this.guestStars;
    }

    public String getPoster() {
        return this.poster;
    }

    public boolean wasSeen() {
        return this.seen;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name should not be null");
        }
        
        this.name = name;
        
        this.notifyListeners();
    }

    public void setFirstAired(Date firstAired) {
        this.firstAired = firstAired;
        
        this.notifyListeners();
    }

    public void setOverview(String overview) {
        if (overview == null) {
            throw new IllegalArgumentException("Overview should not be null");
        }

        this.overview = overview;
        
        this.notifyListeners();
    }

    public void setDirector(String director) {
        if (director == null) {
            throw new IllegalArgumentException("Director should not be null");
        }

        this.director = director;
        
        this.notifyListeners();
    }

    public void setWriter(String writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer should not be null");
        }

        this.writer = writer;
        
        this.notifyListeners();
    }

    public void setGuestStars(String guestStars) {
        if (guestStars == null) {
            throw new IllegalArgumentException("Guest stars should not be null");
        }

        this.guestStars = guestStars;
        this.notifyListeners();
    }

    public void setPoster(String poster) {
        if (poster == null) {
            throw new IllegalArgumentException("Poster should not be null");
        }

        this.poster = poster;
        
        this.notifyListeners();
    }

    public void markWetherSeen(boolean seen) {
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

    @Override
    public int hashCode() {
        return  this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Episode) &&
               ((Episode) obj).getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return this.getName();
    }
    
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
    
    public void mergeWith(Episode other) {
        if (other == null) {
            throw new IllegalArgumentException(); //TODO: use a user exception 
        }
        
        this.name = other.name;
        this.firstAired = other.firstAired;
        this.overview = other.overview;
        this.director = other.director;
        this.writer = other.writer;
        this.guestStars = other.guestStars;
        this.poster = other.poster;
        
        this.notifyListeners();
    }
}
