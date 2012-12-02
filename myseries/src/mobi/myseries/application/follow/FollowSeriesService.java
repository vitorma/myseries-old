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
import mobi.myseries.shared.Validate;
import android.os.AsyncTask;

public class FollowSeriesService {
    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private LocalizationProvider localizationProvider;
    private ImageService imageService;
    private ErrorService errorService;
    private BroadcastService broadcastService;
    private SeriesFollower seriesFollower;
    private final ListenerSet<SeriesFollowingListener> seriesFollowingListeners;

    public FollowSeriesService(SeriesSource seriesSource,
            SeriesRepository seriesRepository,
            LocalizationProvider localizationProvider,
            ImageService imageService,
            ErrorService errorService,
            BroadcastService broadcastService) {
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
        this.seriesFollower = new AsynchronousFollower();
        this.seriesFollowingListeners = new ListenerSet<SeriesFollowingListener>();
    }

    public FollowSeriesService(SeriesSource seriesSource,
                               SeriesRepository seriesRepository,
                               LocalizationProvider localizationProvider,
                               ImageService imageService,
                               ErrorService errorService) {
        this(seriesSource, seriesRepository, localizationProvider, imageService, errorService, true);
    }

    public FollowSeriesService(SeriesSource seriesSource,
                               SeriesRepository seriesRepository,
                               LocalizationProvider localizationProvider,
                               ImageService imageService,
                               ErrorService errorService,
                               boolean asyncFollowing) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageService, "imageService");
        Validate.isNonNull(errorService, "errorService");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.localizationProvider = localizationProvider;
        this.imageService = imageService;
        this.errorService = errorService;
        this.seriesFollower = (asyncFollowing ? new AsynchronousFollower() : new SynchronousFollower());
        this.seriesFollowingListeners = new ListenerSet<SeriesFollowingListener>();
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

    public void registerSeriesFollowingListener(SeriesFollowingListener listener) {
        this.seriesFollowingListeners.register(listener);
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

        this.broadcastService.broadcastAddiction();
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

        protected void followSeries(Series seriesToFollow) throws ParsingFailedException, ConnectionFailedException,
                SeriesNotFoundException, ConnectionTimeoutException {
            this.failed = true;

            this.followedSeries =
                    FollowSeriesService.this.seriesSource.fetchSeries(seriesToFollow.id(), FollowSeriesService.this.localizationProvider
                            .language());
            FollowSeriesService.this.seriesRepository.insert(this.followedSeries);

            FollowSeriesService.this.imageService.downloadAndSavePosterOf(this.followedSeries);

            this.failed = false;
        }

        protected void beforeFollowingActions(Series seriesToFollow) {
            FollowSeriesService.this.notifyListenersOfStartingToFollowSeries(seriesToFollow);
        }

        protected void afterFollowingActions() {
            if (!this.failed) {
                FollowSeriesService.this.notifyListenersOfFollowedSeries(this.followedSeries);
            }
        }
    }

    private class AsynchronousFollower extends SeriesFollower {
        @Override
        public void follow(final Series series) {
            new FollowSeriesTask(series, FollowSeriesService.this.errorService).execute();
        };
    }

    private class FollowSeriesTask extends AsyncTask<Series, Void, AsyncTaskResult<Series>> {
        private Series series;
        private ErrorService errorService;

        public FollowSeriesTask(Series series, ErrorService errorService) {
            this.series = series;
            this.errorService = errorService;
        }

        @Override
        protected void onPreExecute() {
            FollowSeriesService.this.seriesFollower.beforeFollowingActions(this.series);
        }

        @Override
        protected AsyncTaskResult<Series> doInBackground(Series... params) {
            try {
                FollowSeriesService.this.seriesFollower.followSeries(this.series);
            } catch (Exception e) {
                return new AsyncTaskResult<Series>(e);
            }
            return new AsyncTaskResult<Series>(this.series);
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Series> result) {
            if (result.error() != null) {
                FollowSeriesService.this.notifyListenersOfFollowingError(this.series, result.error());
            }

            FollowSeriesService.this.seriesFollower.afterFollowingActions();
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
        };
    }
}
