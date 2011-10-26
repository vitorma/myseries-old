package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

public interface SeriesProviderListener {

    public void onUnfollowing(Series series);

    public void onFollowing(Series series);
    
    public void onMarkedAsSeen(Episode episode);

    public void onMarkedAsNotSeen(Episode episode);
    
    public void onMarkedAsSeen(Season season);
    
    public void onMarkedAsNotSeen(Season season);
}
