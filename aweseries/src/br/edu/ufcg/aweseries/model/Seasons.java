package br.edu.ufcg.aweseries.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import android.util.Log;

public class Seasons {

    private TreeMap<Integer, Season> seasons;

    public Seasons() {
        this.seasons = new TreeMap<Integer, Season>();
    }

    public void addEpisode(Episode episode) {
        if (episode == null) {
            throw new IllegalArgumentException("episode should not be null");
        }

        if (this.hasEpisode(episode)) {
            throw new IllegalArgumentException("episode already exists");
        }

        if (!this.hasSeason(episode.getSeasonNumber())) {
            this.addSeason(episode.getSeasonNumber());
        }

        this.seasons.get(episode.getSeasonNumber()).addEpisode(episode);
    }

    public void addAllEpisodes(List<Episode> episodes) {
        if (episodes == null) {
            throw new IllegalArgumentException("episodes should not be null");
        }

        if (episodes.isEmpty()) {
            Log.w("Seasons", "episodes list is empty");
        }

        for (Episode e : episodes) {
            Log.d("Seasons", "adding episode " + e);
            this.addEpisode(e);
        }
    }

    private void addSeason(int seasonNumber) {
        this.seasons.put(seasonNumber, new Season(seasonNumber));
    }

    private boolean hasSeason(int i) {
        return this.seasons.containsKey(i);
    }

    private boolean hasEpisode(Episode episode) {
        for (Season s : this.seasons.values()) {
            if (s.has(episode)) {
                return true;
            }
        }

        return false;
    }

    public Season getSeason(int i) {
        return this.seasons.get(i);
    }

    public List<Episode> getAllEpisodes() {
        List<Episode> episodes = new ArrayList<Episode>();

        for (Season s : this.seasons.values()) {
            episodes.addAll(s.getEpisodes());
        }

        return episodes;
    }

    public List<Season> toList() {
        
        final List<Season> list = new ArrayList<Season>();
        for (Season season : this.seasons.values()) {
            list.add(season);
        }
        
        return list;
    }

    public Season[] toArray() {
        final Season[] array = new Season[this.seasons.size()];

        int i = 0;
        for (Season s : this.seasons.values()) {
            array[i] = s;
            i++;
        }

        return array;
    }

    public Episode getNextEpisodeToView() {
        for (Episode e : this.getAllEpisodesSortedByDate()) {
            if (!e.wasSeen()) {
                return e;
            }
        }

        return null;
    }

    public Episode getNextEpisodeToAir() {
        final TreeSet<Episode> episodes = this.getAllEpisodesSortedByDate();

        final Date today = new Date();

        for (Episode e : episodes) {
            if (e.airedFrom(today)) {
                return e;
            }
        }

        return null;
    }

    public Episode getLastAiredEpisode() {
        final TreeSet<Episode> episodes = this.getAllEpisodesSortedByDate();

        if (episodes.isEmpty()) return null;

        final Date today = new Date();
        final Iterator<Episode> it = episodes.iterator();

        Episode last = it.next();
        while (it.hasNext()) {
            Episode current = it.next();
            if (current.airedUntil(today)) {
                last = current;
            }
        }

        return last;
    }

    private TreeSet<Episode> getAllEpisodesSortedByDate() {
        TreeSet<Episode> episodes = new TreeSet<Episode>(new Comparator<Episode>() {
            @Override
            public int compare(Episode e1, Episode e2) {
                return e1.getDateFirstAired().compareTo(e2.getDateFirstAired());
            }
        });

        episodes.addAll(this.getAllEpisodes());

        return episodes;
    }
}
