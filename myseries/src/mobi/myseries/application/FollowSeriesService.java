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

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Validate;
import android.os.AsyncTask;

public class FollowSeriesService {

    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private LocalizationProvider localizationProvider;
    private ImageProvider imageProvider;

    private SeriesFollower seriesFollower;
    private final ListenerSet<SeriesFollowingListener> seriesFollowingListeners;

    /**
     * Default constructor. Series following will be asynchronous.
     * @param seriesSource
     * @param seriesRepository
     * @param localizationProvider
     * @param imageProvider
     */
    public FollowSeriesService(SeriesSource seriesSource,
                               SeriesRepository seriesRepository,
                               LocalizationProvider localizationProvider,
                               ImageProvider imageProvider) {
        this(seriesSource, seriesRepository, localizationProvider, imageProvider, true);
    }

    /**
     * This constructor should be use used when you want to specify if the series following should be
     * synchronous or not. It will probably be used only for testing.
     */
    public FollowSeriesService(SeriesSource seriesSource,
                               SeriesRepository seriesRepository,
                               LocalizationProvider localizationProvider,
                               ImageProvider imageProvider,
                               boolean asyncFollowing) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(localizationProvider, "localizationProvider");
        Validate.isNonNull(imageProvider, "imageProvider");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.localizationProvider = localizationProvider;
        this.imageProvider = imageProvider;

        this.seriesFollower = (asyncFollowing ? new AsynchronousFollower()
                                              : new SynchronousFollower());

        this.seriesFollowingListeners = new ListenerSet<SeriesFollowingListener>();
    }

    public void follow(Series series) {
        Validate.isNonNull(series, "series");

        this.seriesFollower.follow(series);
    }


    public void stopFollowing(Series series) {
        Validate.isNonNull(series, "series");

        this.seriesRepository.delete(series);
        this.notifyListenersOfUnfollowedSeries(series);
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
            listener.onUnfollowing(unfollowedSeries);
        }
    }

    private abstract class SeriesFollower {
        public abstract void follow(Series series);

        private Series followedSeries;

        private boolean failed;

        protected void followSeries(Series seriesToFollow) {
            this.failed = true; // it will only be considered successful
                                // after actually being successful

            // TODO is there anything to do about any SeriesNotFoundException that may be thrown
            // here?
            try {
                this.followedSeries = seriesSource.fetchSeries(
                        seriesToFollow.id(),
                        localizationProvider.language());
            } catch (SeriesNotFoundException e) {
                //TODO: notify someone?
                return;
            } catch (ConnectionFailedException e) {
                //TODO: notify someone?
                return;
            } catch (ParsingFailedException e) {
                //TODO: notify someone?
                return;
            }

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
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    followSeries(series);
                    return null;
                }
    
                @Override
                protected void onPostExecute(Void result) {
                    afterFollowingActions();
                }
            }.execute();
        };
    }

    private class SynchronousFollower extends SeriesFollower {
        @Override
        public void follow(final Series series) {
            followSeries(series);
            afterFollowingActions();
        };
    }
}
