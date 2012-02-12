package br.edu.ufcg.aweseries.model;

public interface SeasonSetListener {

    void onChangeNextEpisodeToSee(SeasonSet seasonSet);
    
    void onChangeNumberOfSeenEpisodes(SeasonSet seasonSet);

    void onMerge(SeasonSet seasonSet);

    
}
