/*
 *   SeasonSet.java
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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.util.Validate;

public class SeasonSet implements Iterable<Season>, SeasonListener {
    private TreeMap<Integer, Season> map;
    private int seriesId;
    private List<SeasonSetListener> listeners;
    private Episode nextEpisodeToSee;

    public SeasonSet(int seriesId) {
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");

        this.seriesId = seriesId;
        this.map = new TreeMap<Integer, Season>();
        this.listeners = new LinkedList<SeasonSetListener>();
    }

    //TODO Remove
    public void addAllEpisodes(List<Episode> episodes) {
        Validate.isNonNull(episodes, "episodes should not be null");

        for (Episode e : episodes) {
            this.addEpisode(e);
        }
    }

    public SeasonSet includingAll(Collection<Episode> episodes) {
        Validate.isNonNull(episodes, "episodes");

        for (Episode e : episodes) {
            this.addEpisode(e);
        }

        return this;
    }

    public void addEpisode(Episode episode) {
        Validate.isNonNull(episode, "episode should not be null");

        Validate.isTrue(episode.seriesId() == this.seriesId, "episode belongs to another series");

        if (!this.hasSeason(episode.seasonNumber())) {
            this.addSeason(episode.seasonNumber());
        }

        this.map.get(episode.seasonNumber()).including(episode);
    }

    private void addSeason(int seasonNumber) {
        Season newSeason = new Season(this.seriesId, seasonNumber);
        newSeason.register(this);

        this.map.put(seasonNumber, newSeason);
    }

    public List<Episode> allEpisodes() {
        List<Episode> episodes = new ArrayList<Episode>();

        for (Season s : this.map.values()) {
            episodes.addAll(s.episodes());
        }

        return episodes;
    }

    public List<Episode> episodesBy(Specification<Episode> specification) {
        List<Episode> result = new ArrayList<Episode>();

        for (Season s : this) {
            result.addAll(s.episodesBy(specification));
        }

        return result;
    }

    public int lastSeasonNumber() {
        return this.map.lastKey();
    }

    public void mergeWith(SeasonSet other) {
        Validate.isNonNull(other, "other seasonSet to merge should not be null");

        for (Season s : this.map.values()) {
            if (other.hasSeason(s.number())) {
                s.mergeWith(other.season(s.number()));
            }
        }

        for (Season s : other.map.values()) {
            if (!this.hasSeason(s.number())) {
                this.addAllEpisodes(s.episodes());
            }
        }

        this.notifyMerge();
    }

    public Episode nextEpisodeToSee() {
        return this.nextEpisodeToSee;
    }

    public int numberOfSeasons() {
        return this.map.size();
    }

    public Season season(int seasonNumber) {
        return this.map.get(seasonNumber);
    }

    public int seriesId() {
        return this.seriesId;
    }

    public Season[] toArray() {
        final Season[] array = new Season[this.numberOfSeasons()];

        int i = 0;
        for (Season s : this) {
            array[i] = s;
            i++;
        }

        return array;
    }

    public List<Season> toList() {
        final List<Season> list = new ArrayList<Season>();

        for (Season season : this.map.values()) {
            list.add(season);
        }

        return list;
    }

    //Auxiliary methods ----------------------------------------------------------------------------

    private Episode findNextEpisodeToSee() {
        for (final Season s : this.map.values()) {
            final Episode next = s.nextEpisodeToSee();
            if (next != null)
                return next;
        }

        return null;
    }

    private int firstSeasonNumber() {
        return this.map.firstKey();
    }

    private boolean hasSeason(int seasonNumber) {
        return this.map.containsKey(seasonNumber);
    }

    private void updateNextEpisodeToSee() {
        Episode next = this.findNextEpisodeToSee();

        if (this.nextEpisodeToSee == next)
            return;

        if (this.nextEpisodeToSee != null && this.nextEpisodeToSee.equals(next))
            return;

        this.nextEpisodeToSee = next;
        this.notifyChangeNextEpisodeToSee();

    }

    //Iterators ------------------------------------------------------------------------------------

    @Override
    public Iterator<Season> iterator() {
        return new Iterator<Season>() {
            private int seasonNumber = SeasonSet.this.numberOfSeasons() > 0 ? SeasonSet.this.firstSeasonNumber()
                    : Integer.MAX_VALUE;

            @Override
            public boolean hasNext() {
                return SeasonSet.this.numberOfSeasons() > 0
                && this.seasonNumber <= SeasonSet.this.lastSeasonNumber();
            }

            @Override
            public Season next() {
                if (!this.hasNext())
                    throw new NoSuchElementException();

                Season next = SeasonSet.this.season(this.seasonNumber);
                this.seasonNumber++;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Iterator<Season> reversedIterator() {
        return new Iterator<Season>() {
            private int seasonNumber = SeasonSet.this.numberOfSeasons() > 0 ? SeasonSet.this.lastSeasonNumber()
                    : Integer.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return SeasonSet.this.numberOfSeasons() > 0
                && this.seasonNumber >= SeasonSet.this.firstSeasonNumber();
            }

            @Override
            public Season next() {
                if (!this.hasNext())
                    throw new NoSuchElementException();

                Season next = SeasonSet.this.season(this.seasonNumber);
                this.seasonNumber--;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    //Listeners ------------------------------------------------------------------------------------
    
    public boolean deregister(SeasonSetListener listener) {
        return this.listeners.remove(listener);
    }
    
    public boolean register(SeasonSetListener listener) {
        Validate.isNonNull(listener, "listener must not be null.");
        
        if (this.listeners.contains(listener)) {
            return false;
        }
        
        return this.listeners.add(listener);
    }

    private void notifyChangeNextEpisodeToSee() {
        for (SeasonSetListener listener : this.listeners) {
            listener.onChangeNextEpisodeToSee(this);
        }
    }

    private void notifyMerge() {
        for (SeasonSetListener listener : this.listeners) {
            listener.onMerge(this);
        }
    }

    //SeasonListener methods -----------------------------------------------------------------------

    @Override
    public void onChangeNextEpisodeToSee(Season season) {
        this.nextEpisodeToSee = this.findNextEpisodeToSee();
    }

    @Override
    public void onMarkAsNotSeen(Season season) {
        this.updateNextEpisodeToSee();
    }

    @Override
    public void onMarkAsSeen(Season season) {
        this.updateNextEpisodeToSee();
    }

    @Override
    public void onMerge(Season season) {
        //SeasonSet is not interested in this event
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Season season) {
        // TODO A better implementation
    }
}
