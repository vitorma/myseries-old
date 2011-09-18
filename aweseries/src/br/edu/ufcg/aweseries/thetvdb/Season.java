package br.edu.ufcg.aweseries.thetvdb;

/**
 * Represents a season.
 */
public class Season {
    /**
     * The number of this series.
     */
    private int number;
    
    /**
     * The episodes of this series.
     */
    private java.util.List<Episode> episodes;

    /**
     * The poster of this series.
     */
    private String poster;

    /**
     * The number of the season.
     * 
     * @param seasonNumber
     */
    public Season(final int seasonNumber) {
        // TODO Auto-generated constructor stub
    }

    /**
     * Adds an episode to this series.
     * 
     * @param episode The episode to add. If the episode is already in the
     *            series, it will not be added.
     */
    public void addEpisode(final Episode episode) {

    }

    /**
     * Returns the list of episodes in this series.
     * 
     * @return The list of episodes
     */
    public java.util.List<Episode> getEpisodes() {
        return null;
    }
    
    /**
     * Returns the i-th episode of this season.
     * 
     * @param i Episode index
     * @return The episode at index i, if any
     */
    public Episode getEpisodeAt(int i) {
        return null;
    }

    /**
     * Returns the next episode to be aired.
     * 
     * @return The next episode
     */
    public Episode getNextEpisode() {
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
        return 0;
    }
    /**
     * Returns the poster of this series.
     * 
     * @return The poster
     */
    public String getPoster() {
        return null;
    }
    
    /**
     * Returns the index of a given episode.
     * 
     * @param episode The episode to search
     * @return The index of the episode
     */
    public int indexOf(Episode episode) {
        return -1;
    }
    
    /**
     * Returns true if the i-th episode was marked as viewed.
     * 
     * @param i The index of the episode to query
     * @return True if episode at index i was marked as viewed
     */
    public boolean isViewed(int i) {
        return false;
    }
    
    /**
     * Marks all episodes in this season as not viewed.
     */
    public void markAllAsNotViewed() {
        
    }

    /**
     * Marks all episodes in this season as viewed.
     */
    public void markAllAsViewed() {
        
    }
    
    /**
     * Marks the i-th episode as not viewed.
     * 
     * @param i Index of the episode to mark
     */
    public void markAsNotViewed(int i) {
        
    }
        
    /**
     * Marks the i-th episode as viewed.
     * 
     * @param i Index of the episode to mark
     */
    public void markAsViewed(int i) {
        
    }
}
