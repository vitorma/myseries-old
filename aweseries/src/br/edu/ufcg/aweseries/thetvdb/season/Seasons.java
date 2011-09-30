package br.edu.ufcg.aweseries.thetvdb.season;

import java.util.List;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.thetvdb.episode.Episode;

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

        for (Episode e : episodes) {
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

    public Season[] toArray() {
        final Season[] array = new Season[this.seasons.size()];

        int i = 0;
        for (Season s : this.seasons.values()) {
            array[i] = s;
            i++;
        }

        return array;
    }
}
