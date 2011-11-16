/*
 *   SeasonSet.java
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import android.util.Log;
import br.edu.ufcg.aweseries.util.Strings;

public class SeasonSet implements Iterable<Season>, DomainEntityListener<Season> {
    private TreeMap<Integer, Season> map;
    private String seriesId;
    private Set<DomainEntityListener<SeasonSet>> listeners;

    public SeasonSet(String seriesId) {
        if ((seriesId == null) || Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("invalid series id for season set");
        }

        this.seriesId = seriesId;
        this.map = new TreeMap<Integer, Season>();
        this.listeners = new HashSet<DomainEntityListener<SeasonSet>>();
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

        if (!episode.getSeriesId().equals(this.seriesId)) {
            throw new IllegalArgumentException("episode belongs to another series");
        }

        if (!this.hasSeason(episode.getSeasonNumber())) {
            this.addSeason(episode.getSeasonNumber());
        }

        this.map.get(episode.getSeasonNumber()).addEpisode(episode);
    }

    public void addAllEpisodes(List<Episode> episodes) {
        if (episodes == null) {
            throw new IllegalArgumentException("episodes should not be null");
        }

        for (Episode e : episodes) {
            Log.d("Seasons", "adding episode " + e);
            this.addEpisode(e);
        }
    }

    private void addSeason(int seasonNumber) {
        Season newSeason = new Season(this.seriesId, seasonNumber);
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

        for (Season s : this) {
            episodes.addAll(s.getEpisodes());
        }

        return episodes;
    }

    public List<Season> toList() {
        final List<Season> list = new ArrayList<Season>();

        for (Season season : this) {
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
        for (final Season s : this) {
            final Episode next = s.getNextEpisodeToSee();
            if (next != null) return next;
        }

        return null;
    }

    public Episode getNextEpisodeToAir() {
        for (final Season s : this) {
            final Episode next = s.getNextEpisodeToAir();
            if (next != null) return next;
        }

        return null;
    }

    public Episode getLastAiredEpisode() {
        final Iterator<Season> it = this.reversedIterator();

        while (it.hasNext()) {
            final Episode last = it.next().getLastAiredEpisode();
            if (last != null) return last;
        }

        return null;
    }


    public List<Episode> getLastAiredNotSeenEpisodes() {
        List<Episode> list = new ArrayList<Episode>();

        for (Season s : this) {
            list.addAll(s.getLastAiredNotSeenEpisodes());
        }

        return list;
    }

    public List<Episode> getNextEpisodesToAir() {
        List<Episode> list = new ArrayList<Episode>();

        for (Season s : this) {
            list.addAll(s.getNextEpisodesToAir());
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
    
    public boolean addListener(DomainEntityListener<SeasonSet> listener) {
        return this.listeners.add(listener);
    }
    
    public boolean removeListener(DomainEntityListener<SeasonSet> listener) {
        return this.listeners.remove(listener);
    }
    
    private void notifyListeners() {
        for (DomainEntityListener<SeasonSet> listener : this.listeners) {
            listener.onUpdate(this);            
        }        
    }
}
