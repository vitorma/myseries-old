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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private Date firstAired;
    private String overview;
    private String directors;
    private String writers;
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
        this.markSeenAs(false);
    }

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
        return this.seasonNumber() == 0;
    }

    public String name() {
        return this.name;
    }

    public Date firstAired() {
        return this.firstAired;
    }

    public boolean wasAired() {
        return this.firstAired != null;
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

    @Override
    public int hashCode() {
        return  this.id().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Episode) &&
               ((Episode) obj).id().equals(this.id());
    }

    @Override
    public String toString() {
        return this.name();
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

    //TODO: Test whether other has same id -----------------------------------------------------------------------------

    public void mergeWith(Episode other) {
        if (other == null) {
            throw new IllegalArgumentException("other should not be null");
        }
        
        this.name = other.name;
        this.firstAired = other.firstAired;
        this.overview = other.overview;
        this.directors = other.directors;
        this.writers = other.writers;
        this.guestStars = other.guestStars;
        this.poster = other.poster;

        this.notifyListeners();
    }

    //TODO: Move this method to an utility class of myseries.gui--------------------------------------------------------

    public String firstAiredAsString() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return (this.wasAired()) ? dateFormat.format(this.firstAired()) : "";
    }

    //TODO: Move these methods to a comparator--------------------------------------------------------------------------

    public boolean airedBefore(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.wasAired() ? (this.firstAired().compareTo(date) < 0) : false;
    }

    public boolean airedUntil(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.wasAired() ? (this.firstAired().compareTo(date) <= 0) : false;
    }

    public boolean airedAt(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.wasAired() ? (this.firstAired().compareTo(date) == 0) : false;
    }

    public boolean airedFrom(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.wasAired() ? (this.firstAired().compareTo(date) >= 0) : false;
    }

    public boolean airedAfter(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date should not be null");
        }

        return this.wasAired() ? (this.firstAired().compareTo(date) > 0) : false;
    }

    public int compareByDateTo(Episode other) {
        if (!this.wasAired()) {
            return other.wasAired() ? 1 : this.compareByNumberTo(other);
        }

        return other.wasAired() ? this.firstAired().compareTo(other.firstAired()) : -1;
    }

    public int compareByNumberTo(Episode other) {
        return (this.seasonNumber() != other.seasonNumber())
               ? (this.seasonNumber() - other.seasonNumber())
               : (this.number() - other.number());
    }

    //TODO: Remove or turn private these methods------------------------------------------------------------------------

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name should not be null");
        }
        
        this.name = name;
    }

    public void setFirstAired(Date firstAired) {
        this.firstAired = firstAired;
    }

    public void setOverview(String overview) {
        if (overview == null) {
            throw new IllegalArgumentException("Overview should not be null");
        }

        this.overview = overview;
    }

    public void setDirector(String director) {
        if (director == null) {
            throw new IllegalArgumentException("Director should not be null");
        }

        this.directors = director;
    }

    public void setWriter(String writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer should not be null");
        }

        this.writers = writer;
    }

    public void setGuestStars(String guestStars) {
        if (guestStars == null) {
            throw new IllegalArgumentException("Guest stars should not be null");
        }

        this.guestStars = guestStars;
    }

    public void setPoster(String poster) {
        if (poster == null) {
            throw new IllegalArgumentException("Poster should not be null");
        }

        this.poster = poster;
    }
}
