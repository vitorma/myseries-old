/*
 *   SeriesProvider.java
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


package br.edu.ufcg.aweseries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.repository.SeriesRepository;

/**
 * Supply series information to the system.
 *
 * The private constructor avoids instantiation of the SeriesProvider.
 * Most times, it should be gotten from Environment.seriesProvider().
 *
 * @see newSeriesProvider()
 */
public class SeriesProvider {
    private final SeriesSource seriesSource;
    private final SeriesRepository seriesRepository;

    private final Set<FollowingSeriesListener> followingSeriesListeners;
    private final Set<UpdateListener> updateListeners;

    public static SeriesProvider newInstance(SeriesSource seriesSource,
                                             SeriesRepository seriesRepository) {
        return new SeriesProvider(seriesSource, seriesRepository);
    }

    private SeriesProvider(SeriesSource seriesSource, SeriesRepository seriesRepository) {
        if (seriesSource == null) {
            throw new IllegalArgumentException("seriesSource should not be null");
        }
        if (seriesRepository == null) {
            throw new IllegalArgumentException("seriesRepository should not be null");
        }

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.followingSeriesListeners = new HashSet<FollowingSeriesListener>();
        this.updateListeners = new HashSet<UpdateListener>();
    }

    public Collection<Series> followedSeries() {
        return this.seriesRepository.getAll();
    }

    public Series[] searchSeries(String seriesName) {
        List<Series> result = this.seriesSource.searchFor(seriesName, App.environment().localization().language());

        //TODO This check should not throw an exception. Move it to the appropriate presenter, where a toast with the
        //     message below should be shown.
        if (result.isEmpty()) {
            throw new RuntimeException(
                    App.environment().context().getString(R.string.no_results_found_for_criteria) + seriesName);
        }

        return result.toArray(new Series[]{}); //TODO Return a List<Series>
    }

    public void updateData() {
        new UpdateSeriesTask().execute();
    }

    private class UpdateSeriesTask extends AsyncTask<Void, Void, Void> {
        private List<String> seriesToUpdate;
        private List<Series> upToDateSeries;

        public UpdateSeriesTask() {
            this.seriesToUpdate = new ArrayList<String>();

            for (final Series series : SeriesProvider.this.seriesRepository.getAll()) {
                this.seriesToUpdate.add(series.id());
            }
        }

        @Override
        protected void onPreExecute() {
            Log.d("Update", "Update started...");
            SeriesProvider.this.notifyListenersOfUpdateStart();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                this.upToDateSeries = SeriesProvider.this.seriesSource.fetchAllSeries(
                        this.seriesToUpdate,
                        App.environment().localization().language());
            } catch (SeriesNotFoundException e) {
                // TODO: find a better way to tell that a problem happened when fetching the series
                this.upToDateSeries = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (this.upToDateSeries == null) {
                SeriesProvider.this.notifyListenersOfUpdateFailure();
                Log.d("Update", "Update failed");
                return;
            }

            List<Series> allOurSeries = new ArrayList<Series>();

            for (final Series theirSeries : upToDateSeries) {
                final Series ourSeries = SeriesProvider.this.getSeries(theirSeries.id());
                ourSeries.mergeWith(theirSeries);
                allOurSeries.add(ourSeries);
            }

            SeriesProvider.this.seriesRepository.updateAll(allOurSeries);

            SeriesProvider.this.notifyListenersOfUpdateSuccess();
            Log.d("Update", "Update successful.");

        }
    }

    public void follow(Series series) {
        new FollowSeriesTask().execute(series);
    }

    private class FollowSeriesTask extends AsyncTask<Series, Void, Void> {
        private Series followedSeries;

        @Override
        protected Void doInBackground(Series... params) {
            final Series seriesToFollow = params[0];

            // TODO is there anything to do about any SeriesNotFoundException that may be thrown
            // here?
            this.followedSeries = SeriesProvider.this.seriesSource.fetchSeries(seriesToFollow.id(), App
                    .environment().localization().language());
            SeriesProvider.this.seriesRepository.insert(this.followedSeries);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SeriesProvider.this.notifyListenersOfFollowedSeries(this.followedSeries);
        }
    };

    public void unfollow(Series series) {
        this.seriesRepository.delete(series);
        this.notifyListenersOfUnfollowedSeries(series);
    }

    public boolean follows(Series series) {
        return this.seriesRepository.contains(series);
    }

    public void wipeFollowedSeries() {
        for (final Series s : this.followedSeries()) {
            this.notifyListenersOfUnfollowedSeries(s);
        }

        this.seriesRepository.clear();
    }

    private void notifyListenersOfFollowedSeries(Series followedSeries) {
        for (final FollowingSeriesListener listener : this.followingSeriesListeners) {
            listener.onFollowing(followedSeries);
        }
    }

    private void notifyListenersOfUnfollowedSeries(Series unfollowedSeries) {
        for (final FollowingSeriesListener listener : this.followingSeriesListeners) {
            listener.onUnfollowing(unfollowedSeries);
        }
    }

    public void addFollowingSeriesListener(FollowingSeriesListener listener) {
        this.followingSeriesListeners.add(listener);
    }

    public Series getSeries(String seriesId) {
        return this.seriesRepository.get(seriesId);
    }

    public List<Episode> recentNotSeenEpisodes() {
        final List<Episode> recent = new ArrayList<Episode>();

        for (final Series s : this.followedSeries()) {
            recent.addAll(s.seasons().getLastAiredNotSeenEpisodes());
        }

        return recent;
    }

    public List<Episode> nextEpisodesToAir() {
        final List<Episode> upcoming = new ArrayList<Episode>();

        for (final Series s : this.followedSeries()) {
            upcoming.addAll(s.seasons().getNextEpisodesToAir());
        }

        return upcoming;
    }

    //TODO: Move this method to an utilitary class----------------------------------------------------------------------

    public Bitmap getPosterOf(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        if (!series.hasPoster()) {
            return this.genericPosterImage();
        }

        return series.poster().getImage();
    }

    private Bitmap genericPosterImage() {
        return BitmapFactory.decodeResource(App.environment().context().getResources(),
                R.drawable.small_poster_clapperboard);
    }

    //TODO: Remove it ASAP----------------------------------------------------------------------------------------------

    public void markSeasonAsSeen(Season season) {
        season.markAllAsSeen();
        this.seriesRepository.update(this.getSeries(String.valueOf(season.seriesId())));
    }

    public void markSeasonAsNotSeen(Season season) {
        season.markAllAsNotSeen();
        this.seriesRepository.update(this.getSeries(String.valueOf(season.seriesId())));
    }

    public void markEpisodeAsSeen(Episode episode) {
        episode.markAsSeen();
        this.seriesRepository.update(this.getSeries(String.valueOf(episode.seriesId())));
    }

    public void markEpisodeAsNotSeen(Episode episode) {
        episode.markAsNotSeen();
        this.seriesRepository.update(this.getSeries(String.valueOf(episode.seriesId())));
    }

    public boolean addListener(UpdateListener listener) {
        return this.updateListeners.add(listener);
    }

    public boolean removeListener(UpdateListener listener) {
        return this.updateListeners.remove(listener);
    }

    public void notifyListenersOfUpdateStart() {
        for (final UpdateListener listener : this.updateListeners) {
            listener.onUpdateStart();
        }
    }

    public void notifyListenersOfUpdateSuccess() {
        for (final UpdateListener listener : this.updateListeners) {
            listener.onUpdateSuccess();
        }
    }

    public void notifyListenersOfUpdateFailure() {
        for (final UpdateListener listener : this.updateListeners) {
            listener.onUpdateFailure();
        }
    }
}
