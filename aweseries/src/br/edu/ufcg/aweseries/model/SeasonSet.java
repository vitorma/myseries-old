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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.util.Validate;

public class SeasonSet implements Iterable<Season>, SeasonListener {
    private TreeMap<Integer, Season> map;
    private int seriesId;
    private Set<DomainObjectListener<SeasonSet>> domainObjectListeners;
    private List<SeasonSetListener> listeners;

    public SeasonSet(int seriesId) {
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");

        this.seriesId = seriesId;
        this.map = new TreeMap<Integer, Season>();
        this.domainObjectListeners = new HashSet<DomainObjectListener<SeasonSet>>();
        this.listeners = new LinkedList<SeasonSetListener>();
    }

    public int seriesId() {
        return this.seriesId;
    }

    private int firstSeasonNumber() {
        return this.map.firstKey();
    }

    public int lastSeasonNumber() {
        return this.map.lastKey();
    }

    public int numberOfSeasons() {
        return this.map.size();
    }

    public void addEpisode(Episode episode) {
        Validate.isNonNull(episode, "episode should not be null");
        
        Validate.isTrue(episode.seriesId() == this.seriesId, "episode belongs to another series");

        if (!this.hasSeason(episode.seasonNumber())) {
            this.addSeason(episode.seasonNumber());
        }

        this.map.get(episode.seasonNumber()).addEpisode(episode);
        this.notifyListeners();
    }

    public void addAllEpisodes(List<Episode> episodes) {
        Validate.isNonNull(episodes, "episodes should not be null");
        
        for (Episode e : episodes) {
            this.addEpisode(e);
        }
    }

    private void addSeason(int seasonNumber) {
        Season newSeason = new Season(this.seriesId,  seasonNumber);
        newSeason.register(this);
        
        this.map.put(seasonNumber, newSeason);
    }

    private boolean hasSeason(int seasonNumber) {
        return this.map.containsKey(seasonNumber);
    }

    public Season season(int seasonNumber) {
        return this.map.get(seasonNumber);
    }

    public List<Episode> allEpisodes() {
        List<Episode> episodes = new ArrayList<Episode>();

        for (Season s : this.map.values()) {
            episodes.addAll(s.episodes());
        }

        return episodes;
    }

    public List<Season> toList() {
        final List<Season> list = new ArrayList<Season>();

        for (Season season : this.map.values()) {
            list.add(season);
        }

        return list;
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

    public Episode nextEpisodeToSee() {
        for (final Season s : this.map.values()) {
            final Episode next = s.nextEpisodeToSee();
            if (next != null) return next;
        }

        return null;
    }

    public Episode nextEpisodeToAir() {
        for (final Season s : this.map.values()) {
            final Episode next = s.nextEpisodeToAir();
            if (next != null) return next;
        }

        return null;
    }

    public Episode lastAiredEpisode() {
        final Iterator<Season> it = this.reversedIterator();

        while (it.hasNext()) {
            final Episode last = it.next().lastAiredEpisode();
            if (last != null) return last;
        }

        return null;
    }


    public List<Episode> lastAiredNotSeenEpisodes() {
        List<Episode> list = new ArrayList<Episode>();

        for (Season s : this) {
            list.addAll(s.lastAiredNotSeenEpisodes());
        }

        return list;
    }

    public List<Episode> nextEpisodesToAir() {
        List<Episode> list = new ArrayList<Episode>();

        for (Season s : this) {
            list.addAll(s.nextEpisodesToAir());
        }

        return list;
    }

    @Override
    public Iterator<Season> iterator() {
        return new Iterator<Season>() {
            private int seasonNumber = (numberOfSeasons() > 0) ? SeasonSet.this
                    .firstSeasonNumber() : Integer.MAX_VALUE;

            @Override
            public boolean hasNext() {
                return (numberOfSeasons() > 0)
                        && (this.seasonNumber <= SeasonSet.this.lastSeasonNumber());
            }

            @Override
            public Season next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

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
            private int seasonNumber =
                (numberOfSeasons() > 0) ? SeasonSet.this.lastSeasonNumber() : Integer.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return (numberOfSeasons() > 0)
                        && (this.seasonNumber >= SeasonSet.this.firstSeasonNumber());
            }

            @Override
            public Season next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

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
    
    @Deprecated
    public boolean addListener(DomainObjectListener<SeasonSet> listener) {
        return this.domainObjectListeners.add(listener);
    }
    
    @Deprecated
    public boolean removeListener(DomainObjectListener<SeasonSet> listener) {
        return this.domainObjectListeners.remove(listener);
    }
    
    public boolean addListener(SeasonSetListener listener) {
        Validate.isNonNull(listener, "listener must not be null.");
        return this.listeners.add(listener);
    }
    
    public boolean removeListener(SeasonSetListener listener) {
        return this.listeners.remove(listener);
    }

    @Deprecated
    private void notifyListeners() {
        for (DomainObjectListener<SeasonSet> listener : this.domainObjectListeners) {
            listener.onUpdate(this);            
        }        
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
    }

    @Override
    public void onChangeNextEpisodeToSee(Season season) {
        //TODO A better implementation
        this.notifyListeners();
    }

    @Override
    public void onMarkAsNotSeen(Season season) {
        //TODO A better implementation
        this.notifyListeners();
    }

    @Override
    public void onMarkAsSeen(Season season) {
        //TODO A better implementation
        this.notifyListeners();
    }

    @Override
    public void onMerge(Season season) {
        //TODO A better implementation
        this.notifyListeners();
    }
}
