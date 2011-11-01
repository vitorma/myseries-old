package br.edu.ufcg.aweseries.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;

import android.util.Log;

public class Seasons implements Iterable<Season> {
    private static final Comparator<Episode> EPISODE_COMPARATOR = new Comparator<Episode>() {
        @Override
        public int compare(Episode e1, Episode e2) {
            return e1.compareByDateTo(e2);
        }
    };

    private TreeMap<Integer, Season> map;

    public Seasons() {
        this.map = new TreeMap<Integer, Season>();
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
        this.map.put(seasonNumber, new Season(seasonNumber));
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
                (getNumberOfSeasons() > 0) ? Seasons.this.getFirstSeasonNumber() : Integer.MAX_VALUE;

            @Override
            public boolean hasNext() {
                return (getNumberOfSeasons() > 0) && (this.seasonNumber <= Seasons.this.getLastSeasonNumber());
            }

            @Override
            public Season next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

                Season next = Seasons.this.getSeason(this.seasonNumber);
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
                (getNumberOfSeasons() > 0) ? Seasons.this.getLastSeasonNumber() : Integer.MIN_VALUE;

            @Override
            public boolean hasNext() {
                return (getNumberOfSeasons() > 0) && (this.seasonNumber >= Seasons.this.getFirstSeasonNumber());
            }

            @Override
            public Season next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }

                Season next = Seasons.this.getSeason(this.seasonNumber);
                this.seasonNumber--;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
