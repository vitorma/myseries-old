package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.model.Series;

public interface SeriesProviderListener {

    public void onUnfollowing(Series series);

    public void onFollowing(Series series);
}
