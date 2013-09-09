package mobi.myseries.application.following;

import java.util.Collection;

import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.model.Series;

public abstract class BaseSeriesFollowingListener implements SeriesFollowingListener {

    @Override
    public void onStartToFollow(ParcelableSeries seriesToFollow) { }

    @Override
    public void onStartToUnfollow(Series seriesToUnfollow) { }

    @Override
    public void onStartToUnfollowAll(Collection<Series> allSeriesToUnfollow) { }

    @Override
    public void onSuccessToFollow(Series followedSeries) { }

    @Override
    public void onSuccessToUnfollow(Series unfollowedSeries) { }

    @Override
    public void onSuccessToUnfollowAll(Collection<Series> allUnfollowedSeries) { }

    @Override
    public void onFailToFollow(ParcelableSeries series, Exception e) { }

    @Override
    public void onFailToUnfollow(Series seriesToUnfollow, Exception e) { }

    @Override
    public void onFailToUnfollowAll(Collection<Series> allSeriesToUnfollow, Exception e) { }
}
