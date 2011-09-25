package br.edu.ufcg.aweseries.thetvdb.season;

import java.util.SortedMap;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.thetvdb.episode.Episode;

public final class Seasons {
	private SortedMap<Integer, Season> seasons;

	public Seasons() {
		this.seasons = new TreeMap<Integer, Season>();
	}

	public void addEpisode(Episode episode) {
		if (!this.containsSeason(episode.getSeasonNumber())) {
			this.addSeason(episode.getSeasonNumber());
		}
		this.seasons.get(episode.getSeasonNumber()).addEpisode(episode);
	}

	private void addSeason(int seasonNumber) {
		this.seasons.put(seasonNumber, new Season(seasonNumber));
	}

	private boolean containsSeason(int i) {
		return this.seasons.containsKey(i);
	}

	public Season[] toArray() {
		Season[] array = new Season[this.seasons.size()];
		int i = 0;
		for (Season s : this.seasons.values()) {
			array[i] = s;
			i++;
		}
		return array;
	}
}
