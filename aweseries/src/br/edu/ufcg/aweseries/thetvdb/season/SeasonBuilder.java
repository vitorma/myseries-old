package br.edu.ufcg.aweseries.thetvdb.season;

import java.util.List;

import br.edu.ufcg.aweseries.thetvdb.episode.Episode;

public class SeasonBuilder {
    private int seasonNumber;
    private List<Episode> episodes;

    /**
     * Creates a new instance of the SeasonBuilder class.
     */
    public SeasonBuilder() {
    }

    /**
     * Sets the number of the season under construction.
     *
     * @param seasonNumber The number of the season.
     */
    public void withSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    /**
     * Sets a List of {@link br.edu.ufcg.aweseries.thetvdb.episode.Episode} to the season under
     * construction.
     *
     * @param episodes The episodes the series.
     */
    public void withEpisodes(java.util.List<Episode> episodes) {
        this.episodes = episodes;
    }

    /**
     * Builds and returns the season.
     *
     * @return The built season with the given number and episodes.
     */
    public Season build() {
        Season season = new Season(this.seasonNumber);

        if (this.episodes != null) {
            for (Episode episode : this.episodes) {
                season.addEpisode(episode);
            }
        }

        return season;
    }
}
