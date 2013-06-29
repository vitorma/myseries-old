/*
 *   FollowSeriesService.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.application.follow;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.broadcast.BroadcastService;
import mobi.myseries.application.error.ErrorService;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;
import android.os.Handler;

public class FollowSeriesService implements Publisher<SeriesFollowingListener> {
    private final SeriesSource seriesSource;
    private final SeriesRepository seriesRepository;
    private final LocalizationProvider localizationProvider;
    private final ImageService imageService;
    private final ErrorService errorService;
    private final BroadcastService broadcastService;
    private final SeriesFollower seriesFollower;
    private final ListenerSet<SeriesFollowingListener> seriesFollowingListeners;

    private Handler handler;
    private final ExecutorService executor;

    public FollowSeriesService(SeriesSource seriesSource,
        SeriesRepository seriesRepository,
        LocalizationProvider localizationProvider,
        ImageService imageService,
        ErrorService errorService,
        BroadcastService broadcastService) {
        this(seriesSource, seriesRepository, localizationProvider, imageService, errorService,
            broadcastService, true);
    }

    public FollowSeriesService(SeriesSource seriesSource,
        SeriesRepository seriesRepository,
        LocalizationProvider localizationProvider,
        ImageService imageService,
        ErrorService errorService,
        BroadcastService broadcastService,
        boolean asyncFollowing) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageService, "imageService");
        Validate.isNonNull(errorService, "errorService");
        Validate.isNonNull(broadcastService, "broadcastService");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.localizationProvider = localizationProvider;
        this.imageService = imageService;
        this.errorService = errorService;
        this.broadcastService = broadcastService;
        this.seriesFollower = (asyncFollowing ? new AsynchronousFollower()
            : new SynchronousFollower());
        this.seriesFollowingListeners = new ListenerSet<SeriesFollowingListener>();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void follow(Series series) {
        Validate.isNonNull(series, "series");

        this.seriesFollower.follow(series);
    }

    public void stopFollowing(Series series) {
        this.seriesRepository.delete(series);

        this.imageService.removeAllImagesOf(series);

        this.notifyListenersOfUnfollowedSeries(series);
    }

    public void stopFollowingAll(Collection<Series> seriesCollection) {
        this.seriesRepository.deleteAll(seriesCollection);

        for (Series series : seriesCollection) {
            this.imageService.removeAllImagesOf(series);
        }

        this.notifyListenersOfUnfollowedSeries(seriesCollection);
    }

    public boolean follows(Series series) {
        Validate.isNonNull(series, "series");

        return this.seriesRepository.contains(series);
    }

    public void wipeFollowedSeries() {
        for (final Series s : this.seriesRepository.getAll()) {
            this.notifyListenersOfUnfollowedSeries(s);
        }

        this.seriesRepository.clear();
    }

    @Override
    public boolean register(SeriesFollowingListener listener) {
        return this.seriesFollowingListeners.register(listener);
    }

    @Override
    public boolean deregister(SeriesFollowingListener listener) {
        return this.seriesFollowingListeners.deregister(listener);
    }

    private void notifyListenersOfStartingToFollowSeries(Series seriesToFollow) {
        for (final SeriesFollowingListener listener : this.seriesFollowingListeners) {
            listener.onFollowingStart(seriesToFollow);
        }
    }

    private void notifyListenersOfFollowedSeries(Series followedSeries) {
        for (final SeriesFollowingListener listener : this.seriesFollowingListeners) {
            listener.onFollowing(followedSeries);
        }

        this.broadcastService.broadcastAddition();
    }

    private void notifyListenersOfFollowingError(Series series, Exception e) {
        for (final SeriesFollowingListener listener : this.seriesFollowingListeners) {
            listener.onFollowingFailure(series, e);
        }
    }

    private void notifyListenersOfUnfollowedSeries(Series unfollowedSeries) {
        for (final SeriesFollowingListener listener : this.seriesFollowingListeners) {
            listener.onStopFollowing(unfollowedSeries);
        }

        this.broadcastService.broadcastRemoval();
    }

    private void notifyListenersOfUnfollowedSeries(Collection<Series> allUnfollowedSeries) {
        for (final SeriesFollowingListener listener : this.seriesFollowingListeners) {
            listener.onStopFollowingAll(allUnfollowedSeries);
        }

        this.broadcastService.broadcastRemoval();
    }

    private abstract class SeriesFollower {
        public abstract void follow(Series series);

        private Series followedSeries;

        private boolean failed;

        protected void followSeries(Series seriesToFollow) throws ParsingFailedException,
            ConnectionFailedException,
            SeriesNotFoundException, ConnectionTimeoutException {
            this.failed = true;

            this.followedSeries =
                FollowSeriesService.this.seriesSource.fetchSeries(seriesToFollow.id(),
                    FollowSeriesService.this.localizationProvider
                        .language());
            FollowSeriesService.this.seriesRepository.insert(this.followedSeries);

            FollowSeriesService.this.imageService.downloadAndSavePosterOf(this.followedSeries);

            this.failed = false;
        }

        protected void beforeFollowingActions(final Series seriesToFollow) {
            FollowSeriesService.this.handler.post(new Runnable() {
                @Override
                public void run() {
                    FollowSeriesService.this
                        .notifyListenersOfStartingToFollowSeries(seriesToFollow);
                }
            });
        }

        protected void afterFollowingActions() {
            if (!this.failed) {
                FollowSeriesService.this.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FollowSeriesService.this
                            .notifyListenersOfFollowedSeries(SeriesFollower.this.followedSeries);
                    }
                });
            }
        }
    }

    private class AsynchronousFollower extends SeriesFollower {
        @Override
        public void follow(final Series series) {
            FollowSeriesService.this.executor.execute(
                new FollowSeriesTask(series, FollowSeriesService.this.errorService)
                );
        }
    }

    private class FollowSeriesTask implements Runnable {
        private final Series series;
        private AsyncTaskResult<Series> result;

        public FollowSeriesTask(Series series, ErrorService errorService) {
            this.series = series;
        }

        @Override
        public void run() {
            FollowSeriesService.this.seriesFollower.beforeFollowingActions(this.series);

            try {
                FollowSeriesService.this.seriesFollower.followSeries(this.series);
            } catch (Exception e) {
                this.result = new AsyncTaskResult<Series>(e);
            }

            this.result = new AsyncTaskResult<Series>(this.series);

            if (this.result.error() != null) {
                FollowSeriesService.this.notifyListenersOfFollowingError(this.series,
                    this.result.error());
            } else {
                FollowSeriesService.this.seriesFollower.afterFollowingActions();
            }

        }
    }

    private class SynchronousFollower extends SeriesFollower {
        @Override
        public void follow(final Series series) {
            try {
                this.followSeries(series);
            } catch (Exception e) {
                FollowSeriesService.this.notifyListenersOfFollowingError(series, e);
            }
            this.afterFollowingActions();
        }
    }

    public FollowSeriesService withHandler(Handler handler2) {
        this.handler = handler2;

        return this;
    }
}
