package br.edu.ufcg.aweseries.thetvdb;

import java.util.HashMap;
import java.util.Map;

public final class Seasons {
	private Map<Integer, Season> seasons;

	public Seasons() {
		this.seasons = new HashMap<Integer, Season>();
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
}
