package mobi.myseries.application.following;

import java.util.Collection;

import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;

public interface SeriesFollowingListener {
    public void onStartToFollow(SearchResult seriesToFollow);
    public void onStartToUnfollow(Series seriesToUnfollow);
    public void onStartToUnfollowAll(Collection<Series> allSeriesToUnfollow);
    public void onSuccessToFollow(Series followedSeries);
    public void onSuccessToUnfollow(Series unfollowedSeries);
    public void onSuccessToUnfollowAll(Collection<Series> allUnfollowedSeries);
    public void onFailToFollow(SearchResult series, Exception e);
    public void onFailToUnfollow(Series seriesToUnfollow, Exception e);
    public void onFailToUnfollowAll(Collection<Series> allSeriesToUnfollow, Exception e);
}
