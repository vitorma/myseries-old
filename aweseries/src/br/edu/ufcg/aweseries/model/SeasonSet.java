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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.util.Numbers;
import br.edu.ufcg.aweseries.util.Strings;

public class SeasonSet implements Iterable<Season>, DomainObjectListener<Season> {
    private TreeMap<Integer, Season> map;
    private String seriesId;
    private Set<DomainObjectListener<SeasonSet>> listeners;

    public SeasonSet(String seriesId) {
        if ((seriesId == null) || Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("invalid series id for season set");
        }

        this.seriesId = seriesId;
        this.map = new TreeMap<Integer, Season>();
        this.listeners = new HashSet<DomainObjectListener<SeasonSet>>();
    }

    public String getSeriesId() {
        return this.seriesId;
    }

    private int getFirstSeasonNumber() {
        return this.map.firstKey();
    }

    public int getLastSeasonNumber() {
        return this.map.lastKey();
    }

    public int getNumberOfSeasons() {
        return this.map.size();
    }

    public void addEpisode(Episode episode) {
        if (episode == null) {
            throw new IllegalArgumentException("episode should not be null");
        }

        if (!String.valueOf(episode.seriesId()).equals(this.seriesId)) {
            throw new IllegalArgumentException("episode belongs to another series");
        }

        if (!this.hasSeason(episode.seasonNumber())) {
            this.addSeason(episode.seasonNumber());
        }

        this.map.get(episode.seasonNumber()).addEpisode(episode);
        this.notifyListeners();
    }

    public void addAllEpisodes(List<Episode> episodes) {
        if (episodes == null) {
            throw new IllegalArgumentException("episodes should not be null");
        }

        for (Episode e : episodes) {
            this.addEpisode(e);
        }
    }

    private void addSeason(int seasonNumber) {
        final int invalidSeriesId = -1;
        Season newSeason = new Season(Numbers.parseInt(this.seriesId, invalidSeriesId),  seasonNumber);
        newSeason.addListener(this);
        
        this.map.put(seasonNumber, newSeason);
    }

    private boolean hasSeason(int seasonNumber) {
        return this.map.containsKey(seasonNumber);
    }

    public Season getSeason(int seasonNumber) {
        return this.map.get(seasonNumber);
    }

    public List<Episode> getAllEpisodes() {
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
        final Season[] array = new Season[this.getNumberOfSeasons()];

        int i = 0;
        for (Season s : this) {
            array[i] = s;
            i++;
        }

        return array;
    }

    public Episode getNextEpisodeToSee() {
        for (final Season s : this.map.values()) {
            final Episode next = s.nextEpisodeToSee();
            if (next != null) return next;
        }

        return null;
    }

    public Episode getNextEpisodeToAir() {
        for (final Season s : this.map.values()) {
            final Episode next = s.nextEpisodeToAir();
            if (next != null) return next;
        }

        return null;
    }

    public Episode getLastAiredEpisode() {
        final Iterator<Season> it = this.reversedIterator();

        while (it.hasNext()) {
            final Episode last = it.next().lastAiredEpisode();
            if (last != null) return last;
        }

        return null;
    }


    public List<Episode> getLastAiredNotSeenEpisodes() {
        List<Episode> list = new ArrayList<Episode>();

        for (Season s : this) {
            list.addAll(s.lastAiredNotSeenEpisodes());
        }

        return list;
    }

    public List<Episode> getNextEpisodesToAir() {
        List<Episode> list = new ArrayList<Episode>();

        for (Season s : this) {
            list.addAll(s.nextEpisodesToAir());
        }

        return list;
    }

    @Override
    public Iterator<Season> iterator() {
        return new Iterator<Season>() {
            private int seasonNumber =
                (getNumberOfSeasons() > 0) ? SeasonSet.this.getFirstSeasonNumber() : Integer.MAX_VALUE;

            @Override
            public boolean hasNext() {
                return (getNumberOfSeasons() > 0) && (this.seasonNumber <= SeasonSet.this.getLastSeasonNumber());
            }

            @Override
            public Season next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

                Season next = SeasonSet.this.getSeason(this.seasonNumber);
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
                (getNumberOfSeasons() > 0) ? SeasonSet.this.getLastSeasonNumber() : Integer.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return (getNumberOfSeasons() > 0) && (this.seasonNumber >= SeasonSet.this.getFirstSeasonNumber());
            }

            @Override
            public Season next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

                Season next = SeasonSet.this.getSeason(this.seasonNumber);
                this.seasonNumber--;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public void onUpdate(Season entity) {
        this.notifyListeners();
    }
    
    public boolean addListener(DomainObjectListener<SeasonSet> listener) {
        return this.listeners.add(listener);
    }
    
    public boolean removeListener(DomainObjectListener<SeasonSet> listener) {
        return this.listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (DomainObjectListener<SeasonSet> listener : this.listeners) {
            listener.onUpdate(this);            
        }        
    }

    public void mergeWith(SeasonSet other) {
        if (other == null) {
            throw new IllegalArgumentException("other seasonSet to merge should not be null");
        }

        for (Season s : this.map.values()) {
            if (other.hasSeason(s.number())) {
                s.mergeWith(other.getSeason(s.number()));
            }
        }

        for (Season s : other.map.values()) {
            if (!this.hasSeason(s.number())) {
                this.addAllEpisodes(s.episodes());
            }
        }
    }
}
