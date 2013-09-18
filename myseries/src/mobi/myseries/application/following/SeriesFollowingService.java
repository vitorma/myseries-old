package mobi.myseries.application.following;

import java.util.ArrayList;
import java.util.Collection;

import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;
import mobi.myseries.application.broadcast.BroadcastAction;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.util.SparseBooleanArray;

public class SeriesFollowingService extends ApplicationService<SeriesFollowingListener> {
    private ImageService mImageService;
    private SparseBooleanArray mSeriesBeingFollowed;
    private SparseBooleanArray mSeriesBeingUnfollowed;

    public SeriesFollowingService(Environment context, ImageService imageService) {
        super(context);

        Validate.isNonNull(imageService, "imageService");

        mImageService = imageService;
        mSeriesBeingFollowed = new SparseBooleanArray();
        mSeriesBeingUnfollowed = new SparseBooleanArray();
    }

    /* Interface */

    public void follow(SearchResult seriesToFollow) {
        Validate.isNonNull(seriesToFollow, "seriesToFollow");

        run(new FollowSeriesTask(seriesToFollow));
    }

    public void unfollow(Series seriesToUnfollow) {
        Validate.isNonNull(seriesToUnfollow, "seriesToUnfollow");

        run(new UnfollowSeriesTask(seriesToUnfollow));
    }

    public void unfollowAll(Collection<Series> allSeriesToUnfollow) {
        Validate.isNonNull(allSeriesToUnfollow, "allSeries");

        run(new UnfollowAllSeriesTask(allSeriesToUnfollow));
    }

    public boolean follows(Series series) {
        Validate.isNonNull(series, "series");

        return environment().seriesRepository().contains(series);
    }

    public Series getFollowedSeries(int seriesId) {
        return environment().seriesRepository().get(seriesId);
    }

    public Collection<Series> getAllFollowedSeries(int[] seriesIds) {
        Collection<Series> allFollowedSeries = new ArrayList<Series>();

        for (int i : seriesIds) {
            allFollowedSeries.add(this.getFollowedSeries(i));
        }

        return allFollowedSeries;
    }

    public Collection<Series> getAllFollowedSeries() {
        return environment().seriesRepository().getAll();
    }

    public boolean isTryingToFollowSeries(int seriesId) {
        return mSeriesBeingFollowed.get(seriesId, false);
    }

    public boolean isTryingToUnfollowSeries(int seriesId) {
        return mSeriesBeingUnfollowed.get(seriesId, false);
    }

    /* Notifications */

    private void notifyOnStartToFollow(final SearchResult seriesToFollow) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                mSeriesBeingFollowed.put(seriesToFollow.tvdbIdAsInt(), true);

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onStartToFollow(seriesToFollow);
                }
            }
        });
    }

    private void notifyOnSuccessToFollow(final Series followedSeries) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                mSeriesBeingFollowed.delete(followedSeries.id());

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onSuccessToFollow(followedSeries);
                }

                broadcast(BroadcastAction.ADDITION);
            }
        });
    }

    private void notifyOnFailToFollow(final SearchResult seriesToFollow, final Exception exception) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                mSeriesBeingFollowed.delete(seriesToFollow.tvdbIdAsInt());

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onFailToFollow(seriesToFollow, exception);
                }
            }
        });
    }

    private void notifyOnStartToUnfollow(final Series seriesToUnfollow) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                mSeriesBeingUnfollowed.put(seriesToUnfollow.id(), true);

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onStartToUnfollow(seriesToUnfollow);
                }
            }
        });
    }

    private void notifyOnSucessToUnfollow(final Series unfollowedSeries) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                mSeriesBeingUnfollowed.delete(unfollowedSeries.id());

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onSuccessToUnfollow(unfollowedSeries);
                }

                broadcast(BroadcastAction.REMOVAL);
            }
        });
    }

    private void notifyOnFailToUnfollow(final Series seriesToUnfollow, final Exception exception) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                mSeriesBeingUnfollowed.delete(seriesToUnfollow.id());

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onFailToUnfollow(seriesToUnfollow, exception);
                }
            }
        });
    }

    private void notifyOnStartToUnfollowAll(final Collection<Series> allSeriesToUnfollow) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (Series s : allSeriesToUnfollow) {
                    mSeriesBeingUnfollowed.put(s.id(), true);
                }

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onStartToUnfollowAll(allSeriesToUnfollow);
                }
            }
        });
    }

    private void notifyOnSuccessToUnfollowAll(final Collection<Series> allUnfollowedSeries) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (Series s : allUnfollowedSeries) {
                    mSeriesBeingUnfollowed.delete(s.id());
                }

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onSuccessToUnfollowAll(allUnfollowedSeries);
                }

                broadcast(BroadcastAction.REMOVAL);
            }
        });
    }

    private void notifyOnFailToUnfollowAll(final Collection<Series> allSeriesToUnfollow, final Exception exception) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (Series s : allSeriesToUnfollow) {
                    mSeriesBeingUnfollowed.delete(s.id());
                }

                for (SeriesFollowingListener listener : listeners()) {
                    listener.onFailToUnfollowAll(allSeriesToUnfollow, exception);
                }
            }
        });
    }

    /* Tasks */

    private class FollowSeriesTask implements Runnable {
        private final SearchResult mSeriesToFollow;

        public FollowSeriesTask(SearchResult seriesToFollow) {
            mSeriesToFollow = seriesToFollow;
        }

        @Override
        public void run() {
            notifyOnStartToFollow(mSeriesToFollow);

            try {
                Series followedSeries = environment().traktApi().fetchSeries(mSeriesToFollow.tvdbIdAsInt());

                environment().seriesRepository().insert(followedSeries);
                mImageService.downloadAndSavePosterOf(followedSeries);

                notifyOnSuccessToFollow(followedSeries);
            } catch (Exception e) {
                notifyOnFailToFollow(mSeriesToFollow, e);
            }
        }
    }

    private class UnfollowSeriesTask implements Runnable {
        private Series mSeriesToUnfollow;

        public UnfollowSeriesTask(Series seriesToUnfollow) {
            mSeriesToUnfollow = seriesToUnfollow;
        }

        @Override
        public void run() {
            notifyOnStartToUnfollow(mSeriesToUnfollow);

            try {
                environment().seriesRepository().delete(mSeriesToUnfollow);
                mImageService.removeAllImagesOf(mSeriesToUnfollow);

                notifyOnSucessToUnfollow(mSeriesToUnfollow);
            } catch (Exception e) {
                notifyOnFailToUnfollow(mSeriesToUnfollow, e);
            }
        }
    }

    private class UnfollowAllSeriesTask implements Runnable {
        private Collection<Series> mAllSeriesToUnfollow;

        public UnfollowAllSeriesTask(Collection<Series> allSeriesToUnfollow) {
            mAllSeriesToUnfollow = allSeriesToUnfollow;
        }

        @Override
        public void run() {
            notifyOnStartToUnfollowAll(mAllSeriesToUnfollow);

            try {
                environment().seriesRepository().deleteAll(mAllSeriesToUnfollow);

                for (Series s : mAllSeriesToUnfollow) {
                    mImageService.removeAllImagesOf(s);
                }

                notifyOnSuccessToUnfollowAll(mAllSeriesToUnfollow);
            } catch (Exception e) {
                notifyOnFailToUnfollowAll(mAllSeriesToUnfollow, e);
            }
        }
    }
}
