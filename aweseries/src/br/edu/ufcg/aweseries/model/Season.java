package br.edu.ufcg.aweseries.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class Season implements Iterable<Episode> {

    private final int number;
    private final TreeMap<Integer, Episode> map;

    public Season(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("invalid number for season");
        }

        this.number = number;
        this.map = new TreeMap<Integer, Episode>();
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

        for (Episode e : this) {
            if (e.airedUntil(today) && !e.wasSeen()) {
                list.add(e);
            }
        }

        return list;
    }

    public void addEpisode(final Episode episode) {
        if (episode == null) {
            throw new IllegalArgumentException("episode should not be null");
        }

        if (this.has(episode)) {
            throw new IllegalArgumentException("episode already exists");
        }

        this.map.put(episode.getNumber(), episode);
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
}
