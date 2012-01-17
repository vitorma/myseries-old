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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Validate;

public class Season implements Iterable<Episode>, EpisodeListener {
    private int seriesId;
    private int number;
    private TreeMap<Integer, Episode> episodes;

    private int numberOfSeenEpisodes;
    private Episode nextEpisodeToSee;
    private List<SeasonListener> listeners;

    public Season(int seriesId, int number) {
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");
        Validate.isTrue(number >= 0, "number should be non-negative");

        this.seriesId = seriesId;
        this.number = number;
        this.episodes = new TreeMap<Integer, Episode>();
        this.listeners = new LinkedList<SeasonListener>();
    }

    //Fields------------------------------------------------------------------------------------------------------------

    public int seriesId() {
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

    public boolean wasSeen() {
        return this.numberOfSeenEpisodes == this.episodes.size();
    }

    public Episode nextEpisodeToSee() {
        return this.nextEpisodeToSee;
    }

    public Episode nextEpisodeToAir() {
        Date today = new Date(); //TODO should be a parameter

        for (Episode e : this) {
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
        Date today = new Date(); //TODO should be a parameter

        Iterator<Episode> it = this.reversedIterator();
        while (it.hasNext()) {
            Episode e = it.next();
            if (Dates.compare(e.airdate(), today) <=0) {
             return e; 
            }
        }

        return null;
    }

    public List<Episode> lastAiredNotSeenEpisodes() {
        Date today = new Date(); //TODO should be a parameter
        List<Episode> list = new ArrayList<Episode>();

        for (Episode e : this) {
            if (Dates.compare(e.airdate(), today) < 0 && !e.wasSeen()) {
                list.add(e);
            }
        }

        return list;
    }

    public List<Episode> nextEpisodesToAir() {
        Date today = new Date(); //TODO should be a parameter
        List<Episode> list = new ArrayList<Episode>();

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

    public void addEpisode(Episode episode) {
        Validate.isNonNull(episode, "episode should be non-null");
        Validate.isTrue(episode.seriesId() == this.seriesId, "episode should have the same seriesId as this");
        Validate.isTrue(episode.seasonNumber() == this.number, "episode should have the same seasonNumber as this");
        Validate.isTrue(!this.has(episode), "episode should be not already included in this");

        this.episodes.put(episode.number(), episode);

        if (episode.wasSeen()) {
            this.numberOfSeenEpisodes++;
        }

        if (nextToSeeShouldBe(episode)) {
            this.nextEpisodeToSee = episode;
        }

        episode.register(this);
    }

    private boolean nextToSeeShouldBe(Episode e) {
        return !e.wasSeen() && (this.nextEpisodeToSee == null || this.nextEpisodeToSee.number() > e.number());
    }

    public void markAsSeen() {
        for (Episode e : this) {
            e.markAsSeen();
        }
    }

    public void markAsNotSeen() {
        for (Episode e : this) {
            e.markAsNotSeen();
        }
    }

    public void mergeWith(Season other) {
        Validate.isNonNull(other, "other should be non-null");
        Validate.isTrue(other.seriesId == this.seriesId, "other should have the same seriesId as this");
        Validate.isTrue(other.number == this.number, "other should have the same number as this");

        this.mergeAlreadyExistentEpisodesFrom(other);
        this.addNonExistentYetEpisodesFrom(other);

        this.notifyThatWasMerged();
    }

    private void mergeAlreadyExistentEpisodesFrom(Season other) {
        for (Episode e : this.episodes.values()) {
            if (other.has(e)) {
                e.mergeWith(other.get(e.number()));
            }
        }
    }

    private void addNonExistentYetEpisodesFrom(Season other) {        
        for (Episode e : other.episodes.values()) {
            if (!this.has(e)) {
                this.addEpisode(e);
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
            private int episodeNumber =
                numberOfEpisodes() > 0 ? Season.this.lastEpisodeNumber() : Integer.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return numberOfEpisodes() > 0 && this.episodeNumber >= Season.this.firstEpisodeNumber();
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
        return this.episodes.size();
    }
    
    private int firstEpisodeNumber() {
        return this.episodes.firstKey();
    }

    private int lastEpisodeNumber() {
        return this.episodes.lastKey();
    }

    //Listeners---------------------------------------------------------------------------------------------------------
    
    public boolean register(SeasonListener listener) {
        return !this.isRegistered(listener) && this.listeners.add(listener);
    }
    
    public boolean deregister(SeasonListener listener) {
        return this.isRegistered(listener) && this.listeners.remove(listener);
    }
    
    private boolean isRegistered(SeasonListener listener) {
        Validate.isNonNull(listener, "listener should be non-null");
        
        for (SeasonListener l : this.listeners) {
            if (l == listener) return true;
        }
        
        return false;
    }
    
    private void notifyThatWasMarkedAsSeen() {
        for (SeasonListener l : this.listeners) {
            l.onMarkAsSeen(this);
        }
    }
    
    private void notifyThatWasMarkedAsNotSeen() {
        for (SeasonListener l : this.listeners) {
            l.onMarkAsNotSeen(this);
        }
    }

    private void notifyThatNextToSeeChanged() {
        for (SeasonListener l : this.listeners) {
            l.onChangeNextEpisodeToSee(this);
        }
    }

    private void notifyThatWasMerged() {
        for (SeasonListener l : this.listeners) {
            l.onMerge(this);
        }
    }

    //EpisodeListener---------------------------------------------------------------------------------------------------

    @Override
    public void onMarkAsSeen(Episode e) {
        this.numberOfSeenEpisodes++;

        if (this.wasSeen()) {
            this.notifyThatWasMarkedAsSeen();
            this.nextEpisodeToSee = null;
            this.notifyThatNextToSeeChanged();
        }

        if (!this.wasSeen() && e.equals(this.nextEpisodeToSee)) {
            this.nextEpisodeToSee = this.findNextEpisodeToSee();
            this.notifyThatNextToSeeChanged();
        }
    }

    @Override
    public void onMarkAsNotSeen(Episode e) {
        if (this.wasSeen()) {
            this.notifyThatWasMarkedAsNotSeen();
        }

        if (this.nextToSeeShouldBe(e)) {
            this.nextEpisodeToSee = e;
            this.notifyThatNextToSeeChanged();
        }

        this.numberOfSeenEpisodes--;
    }
    
    private Episode findNextEpisodeToSee() {
        for (Episode e : this) {
            if (!e.wasSeen()) return e;
        }
        
        return null;
    }

    @Override
    public void onMerge(Episode e) {
        //Season is not interested in this event
    }

    //Object------------------------------------------------------------------------------------------------------------

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (prime + this.seriesId) + this.number;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Season)) return false;
        Season other = (Season) obj;
        return other.seriesId == this.seriesId && other.number == this.number;
    }
}
