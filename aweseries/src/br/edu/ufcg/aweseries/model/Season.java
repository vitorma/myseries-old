/*
 *   Season.java
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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.util.Strings;

public class Season implements Iterable<Episode>, DomainObjectListener<Episode> {

    private int number;
    private String seriesId;
    private TreeMap<Integer, Episode> map;
    private Set<DomainObjectListener<Season>> listeners;
    private Episode nextEpisodeToSee;
    

    public Season(String seriesId, int number) {
        if ((seriesId == null) || Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("invalid series id for season");
        }

        if (number < 0) {
            throw new IllegalArgumentException("invalid number for season");
        }

        this.seriesId = seriesId;
        this.number = number;
        this.map = new TreeMap<Integer, Episode>();
        this.listeners = new HashSet<DomainObjectListener<Season>>();
    }

    public String getSeriesId() {
        return this.seriesId;
    }

    public int getNumber() {
        return this.number;
    }
    
    public int getNumberOfEpisodes() {
        return this.map.size();
    }

    public List<Episode> getEpisodes() {
        return new ArrayList<Episode>(this.map.values());
    }

    private int getFirstEpisodeNumber() {
        return this.map.firstKey();
    }

    private int getLastEpisodeNumber() {
        return this.map.lastKey();
    }

    public Episode getFirst() {
        return this.map.get(this.getFirstEpisodeNumber());
    }

    public Episode getLast() {
        return this.map.get(this.getLastEpisodeNumber());
    }

    public Episode get(int episodeNumber) {
        return this.map.get(episodeNumber);
    }

    public boolean has(Episode episode) {
        return this.map.containsValue(episode);
    }

    private Episode findNextEpisodeToSee() {
        for (final Episode e : this) {
            if (!e.wasSeen()) return e;
        }

        return null;
    }
    
    public Episode getNextEpisodeToSee() {
        return this.nextEpisodeToSee;
    }

    public Episode getNextEpisodeToAir() {
        final Date today = new Date();

        for (final Episode e : this) {
            if (e.airedFrom(today)) return e;
        }

        return null;
    }

    public Episode getLastAiredEpisode() {
        final Date today = new Date();
        final Iterator<Episode> it = this.reversedIterator();

        while (it.hasNext()) {
            final Episode e = it.next();
            if (e.airedUntil(today)) return e;
        }

        return null;
    }

    public List<Episode> getLastAiredNotSeenEpisodes() {
        final Date today = new Date();
        final List<Episode> list = new ArrayList<Episode>();

        for (final Episode e : this) {
            if (e.airedBefore(today) && !e.wasSeen()) {
                list.add(e);
            }
        }

        return list;
    }

    public List<Episode> getNextEpisodesToAir() {
        final Date today = new Date();
        final List<Episode> list = new ArrayList<Episode>();

        for (Episode e : this) {
            if (e.airedFrom(today)) {
                list.add(e);
            }
        }

        return list;
    }

    public void addEpisode(final Episode episode) {
        if (episode == null) {
            throw new IllegalArgumentException("episode should not be null");
        }

        if (!episode.getSeriesId().equals(this.seriesId)) {
            throw new IllegalArgumentException("episode belongs to another series");
        }

        if (episode.getSeasonNumber() != this.number) {
            throw new IllegalArgumentException("episode belongs to another series");
        }

        if (this.has(episode)) {
            throw new IllegalArgumentException("episode already exists");
        }

        episode.addListener(this);
        
        this.map.put(episode.getNumber(), episode);
        
        this.onUpdate(episode);
    }
    
    public boolean areAllSeen() {
        for (final Episode e : this) {
            if (!e.wasSeen()) {
                return false;
            }
        }
        
        return true;
    }

    public void markAllAsSeen() {
        for (final Episode e : this) {
            e.markAsSeen();
        }
    }

    public void markAllAsNotSeen() {
        for (final Episode e : this) {
            e.markAsNotSeen();
        }
    }
    
    public void mergeWith(Season other) {
        if (other == null) {
            throw new InvalidParameterException(); //TODO: create a user exception.
        }
        
        for (Episode ourEpisode : getEpisodes()) {
            if (other.has(ourEpisode)) {
                ourEpisode.mergeWith(other.get(ourEpisode.getNumber()));
            }
        }
        
        for (Episode theirEpisode : other.getEpisodes()) {
            if (!this.has(theirEpisode)) {
                this.addEpisode(theirEpisode);
            }
        }
        
        this.notifyListeners();
    }

    @Override
    public Iterator<Episode> iterator() {
        return this.map.values().iterator();
    }

    public Iterator<Episode> reversedIterator() {
        return new Iterator<Episode>() {
            private int episodeNumber =
                (getNumberOfEpisodes() > 0) ? Season.this.getLastEpisodeNumber() : Integer.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return (getNumberOfEpisodes() > 0) && (this.episodeNumber >= Season.this.getFirstEpisodeNumber());
            }

            @Override
            public Episode next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

                Episode next = Season.this.get(this.episodeNumber);
                this.episodeNumber--;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int hashCode() {
        return this.getNumber();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Season) &&
        (((Season) o).getNumber() == this.getNumber());
    }

    @Override
    public String toString() {
        return (this.getNumber() == 0) ? "Special Episodes" : "Season " + this.getNumber();
    }

    @Override
    public void onUpdate(Episode episode) {
        Episode nextEpisodeToSee = findNextEpisodeToSee();
        
        if (nextEpisodeToSee == this.nextEpisodeToSee) {
            return;
        }
        
        if (nextEpisodeToSee == null || !nextEpisodeToSee.equals(this.nextEpisodeToSee)) {
            this.notifyListeners();
        }
                
        this.nextEpisodeToSee = nextEpisodeToSee;
    }

    public boolean addListener(DomainObjectListener<Season> listener) {
        return this.listeners.add(listener);        
    }

    public boolean removeListener(DomainObjectListener<Season> listener) {
        return this.listeners.remove(listener);        
    }
    
    private void notifyListeners() {
        for (final DomainObjectListener<Season> listener : this.listeners) {
            listener.onUpdate(this);
        }
    }
}
