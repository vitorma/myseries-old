/*
 *   SeriesProvider.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import br.edu.ufcg.aweseries.repository.SeriesRepositoryFactory;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

/**
 * Supply series information to the system. It is a cache proxy for series
 * data. All followed series are cached.
 * The private constructor avoids instantiation of the SeriesProvider.
 * Most times, it should be gotten from Environment.seriesProvider().
 * 
 * @see newSeriesProvider()
 */
public class SeriesProvider {
    private final TheTVDB theTVDB;
    private final SeriesRepository seriesRepository;
    private final Set<FollowingSeriesListener> followingSeriesListeners;
    private final Set<UpdateListener> updateListeners;
    private long lastUpdate;

    public static SeriesProvider newInstance(TheTVDB theTVDB,
            SeriesRepositoryFactory seriesRepositoryFactory) {
        return new SeriesProvider(theTVDB, seriesRepositoryFactory);
    }

    private SeriesProvider(TheTVDB theTVDB, SeriesRepositoryFactory seriesRepositoryFactory) {
        if (theTVDB == null) {
            throw new IllegalArgumentException("theTVDB should not be null");
        }
        if (seriesRepositoryFactory == null) {
            throw new IllegalArgumentException("seriesRepositoryFactory should not be null");
        }

        this.theTVDB = theTVDB;
        this.seriesRepository = seriesRepositoryFactory.newSeriesCachedRepository();
        this.followingSeriesListeners = new HashSet<FollowingSeriesListener>();
        this.updateListeners = new HashSet<UpdateListener>();
    }

    public Collection<Series> followedSeries() {
        return this.seriesRepository.getAll();
    }

    public Series[] searchSeries(String seriesName) {
        final List<Series> searchResult = this.theTVDB.search(seriesName);

        if (searchResult == null) {
            throw new RuntimeException("no results found for criteria " + seriesName);
        }

        //TODO: Implement util.Arrays#toArray
        return searchResult.toArray(new Series[] {});
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
                this.seriesToUpdate.add(series.getId());
            }
        }

        @Override
        protected void onPreExecute() {
            Log.d("Update", "Update started...");
            SeriesProvider.this.notifyListenersOfUpdateStart();
        }

        @Override
        protected Void doInBackground(Void... params) {
            this.upToDateSeries = SeriesProvider.this.theTVDB.getAllSeries(this.seriesToUpdate);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (this.upToDateSeries == null) {
                SeriesProvider.this.notifyListenersOfUpdateFailure();
                Log.d("Update", "Update failed");
                return;
            }

            for (final Series theirSeries : upToDateSeries) {
                final Series ourSeries = SeriesProvider.this.getSeries(theirSeries.getId());
                ourSeries.mergeWith(theirSeries);
            }
            
            SeriesProvider.this.seriesRepository.updateAll(upToDateSeries);

            SeriesProvider.this.lastUpdate = (new Date()).getTime();
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

            this.followedSeries = SeriesProvider.this.theTVDB.getSeries(seriesToFollow.getId());
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
            recent.addAll(s.getSeasons().getLastAiredNotSeenEpisodes());
        }

        return recent;
    }

    public List<Episode> nextEpisodesToAir() {
        final List<Episode> upcoming = new ArrayList<Episode>();

        for (final Series s : this.followedSeries()) {
            upcoming.addAll(s.getSeasons().getNextEpisodesToAir());
        }

        return upcoming;
    }

    //TODO: Encapsulate it in Series or SeriesBuilder-------------------------------------------------------------------

    public Bitmap getPosterOf(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        if (!series.hasPoster()) {
            return this.genericPosterImage();
        }

        return series.getPoster().getImage();
    }

    private Bitmap genericPosterImage() {
        return BitmapFactory.decodeResource(App.environment().context().getResources(),
                R.drawable.small_poster_clapperboard);
    }

    //TODO: Remove it ASAP----------------------------------------------------------------------------------------------

    public void markSeasonAsSeen(Season season) {
        season.markAllAsSeen();
        this.seriesRepository.update(this.getSeries(season.getSeriesId()));
    }

    public void markSeasonAsNotSeen(Season season) {
        season.markAllAsNotSeen();
        this.seriesRepository.update(this.getSeries(season.getSeriesId()));
    }

    public void markEpisodeAsSeen(Episode episode) {
        episode.markAsSeen();
        this.seriesRepository.update(this.getSeries(episode.getSeriesId()));
    }

    public void markEpisodeAsNotSeen(Episode episode) {
        episode.markAsNotSeen();
        this.seriesRepository.update(this.getSeries(episode.getSeriesId()));
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
