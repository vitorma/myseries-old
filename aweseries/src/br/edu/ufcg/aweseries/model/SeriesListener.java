package br.edu.ufcg.aweseries.model;

public interface SeriesListener {
    
    void onChangeNextEpisodeToSee(Series series);

    void onMerge(Series series);

    void onChangeNumberOfSeenEpisodes(Series series);

}
