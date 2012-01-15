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
    private String seriesId;//TODO Change type to int
    private TreeMap<Integer, Episode> episodes;
    private Set<DomainObjectListener<Season>> listeners;//TODO List<SeasonListener>
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
        this.episodes = new TreeMap<Integer, Episode>();
        this.listeners = new HashSet<DomainObjectListener<Season>>();
    }

    //Fields------------------------------------------------------------------------------------------------------------

    public String seriesId() {
        return this.seriesId;
    }

    public int number() {
        return this.number;
    }

    public List<Episode> episodes() {
        return new ArrayList<Episode>(this.episodes.values());
    }

    //Queries-----------------------------------------------------------------------------------------------------------
    //TODO Once the new user interface is defined and it changed the application requirements with respect to query
    //     episodes, some of these methods will be removed and others will be added.

    public boolean has(Episode episode) {
        return this.episodes.containsValue(episode);
    }
    
    public Episode get(int episodeNumber) {
        return this.episodes.get(episodeNumber);
    }

    public boolean areAllSeen() {
        //TODO Rename as wasSeen
        //TODO Cost can be constant
        for (final Episode e : this) {
            if (!e.wasSeen()) {
                return false;
            }
        }
        
        return true;
    }

    public Episode nextEpisodeToSee() {
        return this.nextEpisodeToSee;
    }

    public Episode nextEpisodeToAir() {
        //TODO today should be given as a parameter
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
        //TODO today should be given as a parameter
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
        //TODO today should be given as a parameter
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
        //TODO today should be given as a parameter
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

    //Changes-----------------------------------------------------------------------------------------------------------

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
        
        this.episodes.put(episode.number(), episode);
        
        this.updateNextToSee();
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
        //TODO: Test whether other has different seriesId or number than mine (its)
        if (other == null) {
            throw new InvalidParameterException(); //TODO: throw IllegalArgumentException
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

    //Iteration---------------------------------------------------------------------------------------------------------

    @Override
    public Iterator<Episode> iterator() {
        return this.episodes.values().iterator();
    }

    public Iterator<Episode> reversedIterator() {
        //TODO Check whether this iterator is really needed, otherwise remove it
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

    public int numberOfEpisodes() {
        //TODO Turn private
        return this.episodes.size();
    }
    
    private int firstEpisodeNumber() {
        return this.episodes.firstKey();
    }

    private int lastEpisodeNumber() {
        return this.episodes.lastKey();
    }

    //Listeners---------------------------------------------------------------------------------------------------------
    //TODO Users should implement SeasonListener

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

    //EpisodeListener---------------------------------------------------------------------------------------------------

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

    private void updateNextToSee() {
        Episode nextEpisodeToSee = findNextEpisodeToSee();
        this.nextEpisodeToSee = nextEpisodeToSee;
    }

    private Episode findNextEpisodeToSee() {
        for (final Episode e : this) {
            if (!e.wasSeen()) return e;
        }

        return null;
    }
    
    //Object------------------------------------------------------------------------------------------------------------
    //TODO Performance - access the field instead of call the accessor

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
        //TODO Internationalization - remove these strings
        return (this.number() == 0) ? "Special Episodes" : "Season " + this.number();
    }
}
