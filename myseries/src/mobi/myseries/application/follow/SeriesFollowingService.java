package mobi.myseries.application.follow;

import java.util.Collection;

import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;
import mobi.myseries.application.broadcast.BroadcastAction;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.shared.Validate;
import android.util.SparseBooleanArray;

public class SeriesFollowingService extends ApplicationService<SeriesFollowingListener> {
    private ImageService imageService;
    private SparseBooleanArray seriesBeingFollowed;

    public SeriesFollowingService(Environment context, ImageService imageService) {
        super(context);

        Validate.isNonNull(imageService, "imageService");

        this.imageService = imageService;
        this.seriesBeingFollowed = new SparseBooleanArray();
    }

    /* Interface */

    public void follow(ParcelableSeries series) {
        Validate.isNonNull(series, "series");

        this.run(new FollowSeriesTask(series));
    }

    //XXX (Cleber) Run this method asynchronously
    public void unfollow(Series series) {
        Validate.isNonNull(series, "series");

        this.environment().seriesRepository().delete(series);

        this.imageService.removeAllImagesOf(series);

        this.notifyOnUnfollowing(series);
    }

    //XXX (Cleber) Run this method asynchronously
    public void unfollowAll(Collection<Series> allSeries) {
        Validate.isNonNull(allSeries, "allSeries");

        this.environment().seriesRepository().deleteAll(allSeries);

        for (Series series : allSeries) {
            this.imageService.removeAllImagesOf(series);
        }

        this.notifyOnUnfollowingAll(allSeries);
    }

    public boolean follows(Series series) {
        Validate.isNonNull(series, "series");

        return this.environment().seriesRepository().contains(series);
    }

    public Series getFollowedSeries(int seriesId) {
        return this.environment().seriesRepository().get(seriesId);
    }

    public Collection<Series> getAllFollowedSeries() {
        return this.environment().seriesRepository().getAll();
    }

    public boolean isTryingToFollowSeries(int seriesId) {
        return this.seriesBeingFollowed.get(seriesId, false);
    }

    /* Auxiliary */

    private Series addSeries(int seriesId) throws ParsingFailedException, ConnectionFailedException {
        Series series = this.environment().addSeriesSource().fetchSeries(seriesId);

        this.environment().seriesRepository().insert(series);
        this.imageService.downloadAndSavePosterOf(series);

        return series;
    }

    private void notifyOnStart(final ParcelableSeries seriesToFollow) {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                SeriesFollowingService.this.seriesBeingFollowed.put(seriesToFollow.tvdbIdAsInt(), true);

                for (SeriesFollowingListener listener : SeriesFollowingService.this.listeners()) {
                    //FIXME ParcelableSeries instead of Series in the call below
                    listener.onFollowingStart(seriesToFollow.toSeries());
                }
            }
        });
    }

    private void notifyOnFollowing(final Series followedSeries) {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                SeriesFollowingService.this.seriesBeingFollowed.delete(followedSeries.id());

                for (SeriesFollowingListener listener : SeriesFollowingService.this.listeners()) {
                    listener.onFollowing(followedSeries);
                }

                SeriesFollowingService.this.broadcast(BroadcastAction.ADDITION);
            }
        });
    }

    private void notifyOnFailure(final ParcelableSeries seriesToFollow, final Exception exception) {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                SeriesFollowingService.this.seriesBeingFollowed.delete(seriesToFollow.tvdbIdAsInt());

                for (SeriesFollowingListener listener : SeriesFollowingService.this.listeners()) {
                    //FIXME ParcelableSeries instead of Series in the call below
                    listener.onFollowingFailure(seriesToFollow.toSeries(), exception);
                }
            }
        });
    }

    private void notifyOnUnfollowing(Series unfollowedSeries) {
        for (SeriesFollowingListener listener : this.listeners()) {
            listener.onStopFollowing(unfollowedSeries);
        }

        this.broadcast(BroadcastAction.REMOVAL);
    }

    private void notifyOnUnfollowingAll(Collection<Series> allUnfollowedSeries) {
        for (SeriesFollowingListener listener : this.listeners()) {
            listener.onStopFollowingAll(allUnfollowedSeries);
        }

        this.broadcast(BroadcastAction.REMOVAL);
    }

    private class FollowSeriesTask implements Runnable {
        private final ParcelableSeries seriesToFollow;

        public FollowSeriesTask(ParcelableSeries seriesToFollow) {
            this.seriesToFollow = seriesToFollow;
        }

        @Override
        public void run() {
            SeriesFollowingService.this.notifyOnStart(this.seriesToFollow);

            try {
                Series followedSeries = SeriesFollowingService.this.addSeries(this.seriesToFollow.tvdbIdAsInt());

                SeriesFollowingService.this.notifyOnFollowing(followedSeries);
            } catch (Exception e) {
                SeriesFollowingService.this.notifyOnFailure(this.seriesToFollow, e);
            }
        }
    }
}
