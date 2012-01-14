/*
 *   Season.java
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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Strings;

public class Season implements Iterable<Episode>, EpisodeListener {

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

    public String seriesId() {
        return this.seriesId;
    }

    public int number() {
        return this.number;
    }
    
    public int numberOfEpisodes() {
        return this.map.size();
    }

    public List<Episode> episodes() {
        return new ArrayList<Episode>(this.map.values());
    }

    private int firstEpisodeNumber() {
        return this.map.firstKey();
    }

    private int lastEpisodeNumber() {
        return this.map.lastKey();
    }

    public Episode first() {
        return this.map.get(this.firstEpisodeNumber());
    }

    public Episode last() {
        return this.map.get(this.lastEpisodeNumber());
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

    public Episode nextEpisodeToSee() {
        return this.nextEpisodeToSee;
    }

    public Episode nextEpisodeToAir() {
        final Date today = new Date();
        
        for (final Episode e : this) {
            if (e.airdate() == null) {
                continue;
            }
            
            if (Dates.compare(e.airdate(), today) >= 0) {
                return e;
            }
        }

        return null;
    }

    public Episode lastAiredEpisode() {
        final Date today = new Date();
        final Iterator<Episode> it = this.reversedIterator();

        while (it.hasNext()) {
            final Episode e = it.next();
            if (Dates.compare(e.airdate(), today) <=0) {
             return e; 
            }
        }

        return null;
    }

    public List<Episode> lastAiredNotSeenEpisodes() {
        final Date today = new Date();
        final List<Episode> list = new ArrayList<Episode>();

        for (final Episode e : this) {
            if (Dates.compare(e.airdate(), today) < 0 && !e.wasSeen()) {
                list.add(e);
            }
        }

        return list;
    }

    public List<Episode> nextEpisodesToAir() {
        final Date today = new Date();
        final List<Episode> list = new ArrayList<Episode>();

        for (Episode e : this) {
            if (e.airdate() == null) {
                continue;
            }
            
            if (Dates.compare(e.airdate(), today) >=0) {
                list.add(e);
            }
        }

        return list;
    }

    public void addEpisode(final Episode episode) {
        if (episode == null) {
            throw new IllegalArgumentException("episode should not be null");
        }

        if (!String.valueOf(episode.seriesId()).equals(this.seriesId)) {
            throw new IllegalArgumentException("episode belongs to another series");
        }

        if (episode.seasonNumber() != this.number) {
            throw new IllegalArgumentException("episode belongs to another series");
        }

        if (this.has(episode)) {
            throw new IllegalArgumentException("episode already exists");
        }

        episode.register(this);
        
        this.map.put(episode.number(), episode);
        
        this.updateNextToSee();
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

    //TODO: Test whether other has different seriesId or number than mine (its)
    public void mergeWith(Season other) {
        if (other == null) {
            throw new InvalidParameterException(); //TODO: create a user exception.
        }

        this.mergeAlreadyExistentEpisodesFrom(other);
        this.addNonexistentYetEpisodesFrom(other);

        this.notifyListeners();
    }

    private void mergeAlreadyExistentEpisodesFrom(Season other) {
        for (Episode ourEpisode : episodes()) {
            if (other.has(ourEpisode)) {
                ourEpisode.mergeWith(other.get(ourEpisode.number()));
            }
        }
    }

    private void addNonexistentYetEpisodesFrom(Season other) {        
        for (Episode theirEpisode : other.episodes()) {
            if (!this.has(theirEpisode)) {
                this.addEpisode(theirEpisode);
            }
        }
    }

    @Override
    public Iterator<Episode> iterator() {
        return this.map.values().iterator();
    }

    public Iterator<Episode> reversedIterator() {
        return new Iterator<Episode>() {
            private int episodeNumber = (numberOfEpisodes() > 0) ? Season.this
                    .lastEpisodeNumber() : Integer.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return (numberOfEpisodes() > 0)
                        && (this.episodeNumber >= Season.this.firstEpisodeNumber());
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
        return this.number();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Season) &&
        (((Season) o).number() == this.number());
    }

    @Override
    public String toString() {
        return (this.number() == 0) ? "Special Episodes" : "Season " + this.number();
    }

    private void updateNextToSee() {
        Episode nextEpisodeToSee = findNextEpisodeToSee();
        
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

    @Override
    public void onMarkedAsSeen(Episode e) {
        this.updateNextToSee();
        this.notifyListeners();
    }

    @Override
    public void onMarkedAsNotSeen(Episode e) {
        this.updateNextToSee();
        this.notifyListeners();
    }

    @Override
    public void onMerged(Episode e) {
        this.notifyListeners();
    }
}
