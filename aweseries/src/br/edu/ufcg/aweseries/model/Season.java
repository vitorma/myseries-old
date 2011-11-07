package br.edu.ufcg.aweseries.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.util.Strings;

public class Season implements Iterable<Episode>, DomainEntityListener<Episode> {

    private int number;
    private String seriesId;
    private TreeMap<Integer, Episode> map;
    private Set<DomainEntityListener<Season>> listeners;
    private boolean allSeen;

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
        this.listeners = new HashSet<DomainEntityListener<Season>>();
        this.allSeen = false;
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

    public Episode getNextEpisodeToSee() {
        for (final Episode e : this) {
            if (!e.wasSeen()) return e;
        }

        return null;
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
            if (e.airedUntil(today) && !e.wasSeen()) {
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
        
        if (this.allSeen && !episode.wasSeen() || !this.allSeen && !episode.wasSeen()) {
            this.updateAllSeen();
        }
        
        this.notifyListeners();
    }
    
    public void updateAllSeen() {
        for (final Episode e : this) {
            if (!e.wasSeen()) {
                this.allSeen = false;
                return;
            }
        }
        
        this.allSeen = true;
    }
    
    public boolean areAllSeen() {
        return this.allSeen;
    }

    public void markAllAsSeen() {
        for (final Episode e : this) {
            e.markAsSeen();
        }
        
        this.updateAllSeen();
        this.notifyListeners();
    }

    public void markAllAsNotSeen() {
        for (final Episode e : this) {
            e.markAsNotSeen();
        }
        
        this.updateAllSeen();
        this.notifyListeners();
    }

    @Override
    public Iterator<Episode> iterator() {
        return new Iterator<Episode>() {
            private int episodeNumber =
                (getNumberOfEpisodes() > 0) ? Season.this.getFirstEpisodeNumber() : Integer.MAX_VALUE;

            @Override
            public boolean hasNext() {
                return (getNumberOfEpisodes() > 0) && (this.episodeNumber <= Season.this.getLastEpisodeNumber());
            }

            @Override
            public Episode next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

                Episode next = Season.this.get(this.episodeNumber);
                this.episodeNumber++;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
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
        if ((!this.allSeen && episode.wasSeen()) || (this.allSeen && !episode.wasSeen())) {
            this.updateAllSeen();
            this.notifyListeners();
        }
    }

    private void notifyListeners() {
        for (final DomainEntityListener<Season> listener : this.listeners) {
            listener.onUpdate(this);
        }
    }
}
