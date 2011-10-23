package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;

public interface SeriesProviderListener {

    public void onUnfollowing(Series series);

    public void onFollowing(Series series);
    
    public void onEpisodeMarkedAsViewed(Episode episode);

    public void onEpisodeMarkedAsNotViewed(Episode episode);
}
