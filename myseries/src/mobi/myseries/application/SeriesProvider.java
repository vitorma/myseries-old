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

import mobi.myseries.R;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SeriesProvider {
    private final SeriesSource seriesSource;
    private final SeriesRepository seriesRepository;

    private final Set<FollowingSeriesListener> followingSeriesListeners;
    private final Set<UpdateListener> updateListeners;

    public static SeriesProvider newInstance(SeriesSource seriesSource, SeriesRepository seriesRepository) {
        return new SeriesProvider(seriesSource, seriesRepository);
    }

    private SeriesProvider(SeriesSource seriesSource, SeriesRepository seriesRepository) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(seriesRepository, "seriesRepository");

        this.seriesSource = seriesSource;
        this.seriesRepository = seriesRepository;
        this.followingSeriesListeners = new HashSet<FollowingSeriesListener>();
        this.updateListeners = new HashSet<UpdateListener>();
    }

    public Collection<Series> followedSeries() {
        return this.seriesRepository.getAll();
    }

    public List<Series> searchSeries(String seriesName) {
        List<Series> result = null;

        try {
            result = this.seriesSource.searchFor(seriesName, App.environment().localization().language());
        } catch (InvalidSearchCriteriaException e) {
            throw new RuntimeException(context().getString(R.string.invalid_search_criteria));
        } catch (ConnectionFailedException e) {
            throw new RuntimeException(context().getString(R.string.connection_failed_message));
        } catch (ParsingFailedException e) {
            throw new RuntimeException(context().getString(R.string.parsing_failed));
        }

        if (result.isEmpty())
            throw new RuntimeException(
                    App.environment().context().getString(R.string.no_results_found_for_criteria) + " " + seriesName);

        return result;
    }

    public void updateData() {
        new UpdateSeriesTask().execute();
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
                this.upToDateSeries = SeriesProvider.this.seriesSource.fetchAllSeries(
                        this.seriesToUpdate, App.environment().localization().language());
            } catch (SeriesNotFoundException e) {
                // TODO: find a better way to tell that a problem happened when fetching the series
                this.upToDateSeries = null;
            } catch (ConnectionFailedException e) {
                // TODO: find a better way to tell that a problem happened when fetching the series
                this.upToDateSeries = null;
            } catch (ParsingFailedException e) {
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

            for (Series theirSeries : this.upToDateSeries) {
                Series ourSeries = SeriesProvider.this.getSeries(theirSeries.id());
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
            try {

                this.followedSeries = SeriesProvider.this.seriesSource.fetchSeries(
                        seriesToFollow.id(), App.environment().localization().language());

            } catch (SeriesNotFoundException e) {
                //TODO: notify someone?

                return null;
            } catch (ConnectionFailedException e) {
                //TODO: notify someone?

                return null;
            } catch (ParsingFailedException e) {
                //TODO: notify someone?

                return null;
            }

            SeriesProvider.this.seriesRepository.insert(this.followedSeries);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            SeriesProvider.this.notifyListenersOfFollowedSeries(this.followedSeries);
            App.environment().imageProvider().downloadPosterOf(this.followedSeries); //TODO: move me elsewhere
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

    public Series getSeries(int seriesId) {
        return this.seriesRepository.get(seriesId);
    }

    //Recent and upcoming episodes--------------------------------------------------------------------------------------

    private Specification<Episode> recentEpisodesSpecification() {
        return AirdateSpecification.before(Dates.today()).and(SeenMarkSpecification.asNotSeen());
    }

    public List<Episode> recentEpisodes() {
        final List<Episode> recent = new ArrayList<Episode>();

        for (final Series s : this.followedSeries()) {
            recent.addAll(s.seasons().episodesBy(this.recentEpisodesSpecification()));
        }

        return recent;
    }

    private Specification<Episode> upcomingEpisodesSpecification() {
        return AirdateSpecification.before(Dates.today()).not().and(SeenMarkSpecification.asNotSeen());
    }

    public List<Episode> upcomingEpisodes() {
        final List<Episode> upcoming = new ArrayList<Episode>();

        for (final Series s : this.followedSeries()) {
            upcoming.addAll(s.seasons().episodesBy(this.upcomingEpisodesSpecification()));
        }

        return upcoming;
    }

    //TODO: Remove it ASAP----------------------------------------------------------------------------------------------

    public void markSeasonAsSeen(Season season) {
        season.markAsSeen();
        this.seriesRepository.update(this.getSeries(season.seriesId()));
    }

    public void markSeasonAsNotSeen(Season season) {
        season.markAsNotSeen();
        this.seriesRepository.update(this.getSeries(season.seriesId()));
    }

    public void markEpisodeAsSeen(Episode episode) {
        episode.markAsSeen();
        this.seriesRepository.update(this.getSeries(episode.seriesId()));
    }

    public void markEpisodeAsNotSeen(Episode episode) {
        episode.markAsNotSeen();
        this.seriesRepository.update(this.getSeries(episode.seriesId()));
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

    private Context context() {
        return App.environment().context();
    }
}