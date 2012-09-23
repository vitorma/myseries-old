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

package mobi.myseries.application;

import java.util.Collection;

import mobi.myseries.application.image.ImageProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
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
    private ImageProvider imageProvider;
    private ErrorService errorService;
    private SeriesFollower seriesFollower;
    private final ListenerSet<SeriesFollowingListener> seriesFollowingListeners;

    public FollowSeriesService(SeriesSource seriesSource,
                               SeriesRepository seriesRepository,
                               LocalizationProvider localizationProvider,
                               ImageProvider imageProvider,
                               ErrorService errorService) {
        this(seriesSource, seriesRepository, localizationProvider, imageProvider, errorService, true);
    }

    public FollowSeriesService(SeriesSource seriesSource,
                               SeriesRepository seriesRepository,
                               LocalizationProvider localizationProvider,
                               ImageProvider imageProvider,
                               ErrorService errorService,
                               boolean asyncFollowing) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageProvider, "imageProvider");
        Validate.isNonNull(errorService, "errorService");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.localizationProvider = localizationProvider;
        this.imageProvider = imageProvider;
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

        //TODO (Cleber) Remove, asynchronously, the images related to the unfollowed series.

        this.notifyListenersOfUnfollowedSeries(series);
    }

    public void stopFollowingAll(Collection<Series> seriesCollection) {
        this.seriesRepository.deleteAll(seriesCollection);

        //TODO (Cleber) Remove, asynchronously, the images related to all the unfollowed series.

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

    private void notifyListenersOfFollowedSeries(Series followedSeries) {
        for (final SeriesFollowingListener listener : this.seriesFollowingListeners) {
            listener.onFollowing(followedSeries);
        }
    }

    private void notifyListenersOfUnfollowedSeries(Series unfollowedSeries) {
        for (final SeriesFollowingListener listener : this.seriesFollowingListeners) {
            listener.onStopFollowing(unfollowedSeries);
        }
    }

    private void notifyListenersOfUnfollowedSeries(Collection<Series> allUnfollowedSeries) {
        for (final SeriesFollowingListener listener : this.seriesFollowingListeners) {
            listener.onStopFollowingAll(allUnfollowedSeries);
        }
    }

    private abstract class SeriesFollower {
        public abstract void follow(Series series);

        private Series followedSeries;

        private boolean failed;

        protected void followSeries(Series seriesToFollow) throws ParsingFailedException, ConnectionFailedException, SeriesNotFoundException, ConnectionTimeoutException {
            this.failed = true;

            this.followedSeries = seriesSource.fetchSeries(seriesToFollow.id(), localizationProvider.language());
            seriesRepository.insert(this.followedSeries);

            this.failed = false;
        }

        protected void afterFollowingActions() {
            if (!failed) {
                notifyListenersOfFollowedSeries(this.followedSeries);
                imageProvider.downloadPosterOf(this.followedSeries); //TODO: move me elsewhere
            }
        }
    }

    private class AsynchronousFollower extends SeriesFollower {
        @Override
        public void follow(final Series series) {
            new FollowSeriesTask(series, errorService).execute();
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
        protected AsyncTaskResult<Series> doInBackground(Series... params) {
            try {
                seriesFollower.followSeries(series);
            } catch (SeriesNotFoundException e) {
                return new AsyncTaskResult<Series>(new FollowSeriesException(e, series));
            } catch (ConnectionFailedException e) {
                return new AsyncTaskResult<Series>(new FollowSeriesException(e, series));
            } catch (ParsingFailedException e) {
                return new AsyncTaskResult<Series>(new FollowSeriesException(e, series));
            } catch (ConnectionTimeoutException e) {
                return new AsyncTaskResult<Series>(new FollowSeriesException(e, series));
            }

            return new AsyncTaskResult<Series>(series);
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Series> result) {
            if(result.error() != null){
                errorService.notifyError(result.error());
            }

            seriesFollower.afterFollowingActions();
        }
    }

    private class SynchronousFollower extends SeriesFollower {
        @Override
        public void follow(final Series series) {
            try {
                followSeries(series);
            } catch (SeriesNotFoundException e) {
                errorService.notifyError(new FollowSeriesException(e, series));
            } catch (ConnectionFailedException e) {
                errorService.notifyError(new FollowSeriesException(e, series));
            } catch (ParsingFailedException e) {
                errorService.notifyError(new FollowSeriesException(e, series));
            } catch (ConnectionTimeoutException e) {
                errorService.notifyError(new FollowSeriesException(e, series));
            }

            afterFollowingActions();
        };
    }
}
