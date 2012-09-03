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

package mobi.myseries.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.Validate;
import android.os.AsyncTask;
import android.util.Log;

public class SeriesProvider {
    private final SeriesSource seriesSource;
    private final SeriesRepository seriesRepository;

    private final Set<UpdateListener> updateListeners;
    private UpdateSeriesTask updateSeriesTask;

    public static SeriesProvider newInstance(SeriesSource seriesSource,
            SeriesRepository seriesRepository) {
        return new SeriesProvider(seriesSource, seriesRepository);
    }

    private SeriesProvider(SeriesSource seriesSource, SeriesRepository seriesRepository) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.updateListeners = new HashSet<UpdateListener>();
    }

    public Collection<Series> followedSeries() {
        return this.seriesRepository.getAll();
    }

    private void killUpdateInProgress() {
        if (this.updateSeriesTask != null && !this.updateSeriesTask.isCancelled()) {
            this.updateSeriesTask.cancel(true);
            
            Log.d("SeriesProvider", "Update cancelled");
        }

        this.updateSeriesTask = null;
    }

    public synchronized void updateData() {
        this.killUpdateInProgress();

        this.updateSeriesTask = new UpdateSeriesTask();
        this.updateSeriesTask.execute();
    }

    private class UpdateSeriesTask extends AsyncTask<Void, Void, Void> {
        private int[] seriesToUpdate;
        private List<Series> upToDateSeries;

        public UpdateSeriesTask() {
            Collection<Series> followedSeries = SeriesProvider.this.followedSeries();

            this.seriesToUpdate = new int[followedSeries.size()];

            int i = 0;
            for (Series series : followedSeries) {
                this.seriesToUpdate[i] = series.id();
                i++;
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
                this.upToDateSeries =
                        SeriesProvider.this.seriesSource.fetchAllSeries(this.seriesToUpdate, App
                                .environment().localization().language());
            } catch (SeriesNotFoundException e) {
                e.printStackTrace();
                // TODO: find a better way to tell that a problem happened when fetching the series
                this.upToDateSeries = null;
            } catch (ConnectionFailedException e) {
                e.printStackTrace();
                // TODO: find a better way to tell that a problem happened when fetching the series
                this.upToDateSeries = null;
            } catch (ParsingFailedException e) {
                e.printStackTrace();
                // TODO: find a better way to tell that a problem happened when fetching the series
                this.upToDateSeries = null;
            }

            return null;
        }
        
        @Override
        protected void onCancelled() {
            
        }

        @Override
        protected void onPostExecute(Void result) {
            if (this.upToDateSeries == null) {
                SeriesProvider.this.notifyListenersOfUpdateFailure();
                Log.d("Update", "Update failed");
                return;
            }

            List<Series> allOurSeries = new ArrayList<Series>();

            for (Series theirSeries : this.upToDateSeries) {
                Series ourSeries = SeriesProvider.this.getSeries(theirSeries.id());

                if (ourSeries == null) { // it happens when the user stops following
                                         // the series during the update
                    continue;
                }

                ourSeries.mergeWith(theirSeries);
                App.environment().imageProvider().downloadPosterOf(ourSeries);
                allOurSeries.add(ourSeries);
            }

            SeriesProvider.this.seriesRepository.updateAll(allOurSeries);

            SeriesProvider.this.notifyListenersOfUpdateSuccess();
            Log.d("Update", "Update successful.");
            SeriesProvider.this.updateSeriesTask = null;
        }
    }

    public Series getSeries(int seriesId) {
        return this.seriesRepository.get(seriesId);
    }

    //SeenMark----------------------------------------------------------------------------------------------------------

    public void markSeasonAsSeen(Season season) {
        season.markAsSeen();
        this.seriesRepository.updateAllEpisodes(season.episodes());
    }

    public void markSeasonAsNotSeen(Season season) {
        season.markAsNotSeen();
        this.seriesRepository.updateAllEpisodes(season.episodes());
    }

    public void markEpisodeAsSeen(Episode episode) {
        episode.markAsSeen();
        this.seriesRepository.update(episode);
    }

    public void markEpisodeAsNotSeen(Episode episode) {
        episode.markAsNotSeen();
        this.seriesRepository.update(episode);
    }

    //Update------------------------------------------------------------------------------------------------------------

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
