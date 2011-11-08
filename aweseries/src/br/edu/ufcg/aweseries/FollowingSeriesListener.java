package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.model.Series;

public interface FollowingSeriesListener {

    void onFollowing(Series followedSeries);

    void onUnfollowing(Series unfollowedSeries);
}
