package br.edu.ufcg.aweseries.thetvdb.season;

import br.edu.ufcg.aweseries.thetvdb.episode.Episode;

/**
 * Represents a season.
 */
public class Season {
    /**
     * The number of this series.
     */
    private final int number;

    /**
     * The episodes of this series.
     */
    private final java.util.List<Episode> episodes;

    public Season(int seasonNumber) {
        this.number = seasonNumber;
        this.episodes = new java.util.ArrayList<Episode>();
    }

    /**
     * Adds an episode to this series.
     * 
     * @param episode The episode to add. If the episode is already in the
     *            series, it will not be added.
     */
    public void addEpisode(final Episode episode) {
        if (!this.getEpisodes().contains(episode)) {
            this.episodes.add(episode);
        }
    }

    /**
     * Returns the i-th episode of this season.
     * 
     * @param i Episode index
     * @return The episode at index i, if any
     */
    public Episode getEpisodeAt(final int i) {
        return this.episodes.get(i);
    }

    /**
     * Returns the list of episodes in this series.
     * 
     * @return The list of episodes
     */
    public java.util.List<Episode> getEpisodes() {
        return this.episodes;
    }

    /**
     * Returns the next episode to be aired.
     * 
     * @return The next episode
     */
    public Episode getNextEpisode() {
        for (final Episode episode : this.getEpisodes()) {
            if (!episode.isViewed()) {
                return episode;
            }
        }

        return null;
    }

    /**
     * Returns the number of this season.
     * 
     * @return Number of season
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * Returns the number of episodes in this series.
     * 
     * @return The number of episodes
     */
    public int getNumberOfEpisodes() {
        return this.getEpisodes().size();
    }

    /**
     * Returns the index of a given episode.
     * 
     * @param episode The episode to search
     * @return The index of the episode
     */
    public int indexOf(final Episode episode) {
        return this.getEpisodes().indexOf(episode);
    }

    /**
     * Returns true if the i-th episode was marked as viewed.
     * 
     * @param i The index of the episode to query
     * @return True if episode at index i was marked as viewed
     */
    public boolean isViewed(final int i) {
        return this.getEpisodeAt(i).isViewed();
    }

    /**
     * Marks all episodes in this season as not viewed.
     */
    public void markAllAsNotViewed() {
        for (final Episode episode : this.getEpisodes()) {
            episode.markAsNotViewed();
        }
    }

    /**
     * Marks all episodes in this season as viewed.
     */
    public void markAllAsViewed() {
        for (final Episode episode : this.getEpisodes()) {
            episode.markAsViewed();
        }
    }

    /**
     * Marks the i-th episode as not viewed.
     * 
     * @param i Index of the episode to mark
     */
    public void markAsNotViewed(final int i) {
        this.getEpisodeAt(i).markAsNotViewed();
    }

    /**
     * Marks the i-th episode as viewed.
     * 
     * @param i Index of the episode to mark
     */
    public void markAsViewed(final int i) {
        this.getEpisodeAt(i).markAsViewed();
    }

    public String toString() {
    	return this.getNumber() == 0 ? "Special Episodes" : "Season " + this.getNumber();
    }
}
