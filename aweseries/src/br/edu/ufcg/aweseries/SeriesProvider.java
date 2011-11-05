package br.edu.ufcg.aweseries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.repository.DatabaseHelper;
import br.edu.ufcg.aweseries.thetvdb.NonExistentSeriesException;
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
    private Series currentSeries;
    private List<Series> mySeries;

    private final HashSet<SeriesProviderListener> listeners;

    /**
     * If you know what you are doing, use this method to instantiate a
     * SeriesProvider.
     * 
     * @see SeriesProvider()
     */
    public static SeriesProvider newSeriesProvider() {
        return new SeriesProvider();
    }

    /**
     * @see newSeriesProvider()
     */
    private SeriesProvider() {
        this.listeners = new HashSet<SeriesProviderListener>();
    }

    private DatabaseHelper localSeriesRepository() {
        return App.environment().localSeriesRepository();
    }

    private TheTVDB theTVDB() {
        return App.environment().theTVDB();
    }

    public List<Series> mySeries() {
        if (this.mySeries == null) {
            this.mySeries = this.localSeriesRepository().getAllSeries();
        }
        return this.mySeries;
    }

    public void follow(Series series) {
        final Series fullSeries = this.theTVDB().getSeries(series.getId());
        this.localSeriesRepository().insert(fullSeries);
        this.notifyListenersAboutFollowedSeries(fullSeries);
    }

    public void unfollow(Series series) {
        this.localSeriesRepository().delete(series);
        this.notifyListenersAboutUnfollowedSeries(series);
    }

    public boolean follows(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        return this.mySeries().contains(series);
    }

    public void wipeFollowedSeries() {
        for (final Series s : this.localSeriesRepository().getAllSeries()) {
            this.notifyListenersAboutUnfollowedSeries(s);
        }

        this.localSeriesRepository().deleteAllSeries();
    }

    public Series[] searchSeries(String seriesName) {
        final List<Series> searchResult = this.theTVDB().search(seriesName);

        if (searchResult == null) {
            throw new RuntimeException("no results found for criteria " + seriesName);
        }

        //TODO: Implement util.Arrays#toArray
        return searchResult.toArray(new Series[] {});
    }

    public Series getSeries(String seriesId) {

        Series series = this.getSeriesFromCache(seriesId);

        if (series == null) {
            series = this.getSeriesFromLocalRepository(seriesId);
        }

        if (series == null) {
            series = this.getSeriesFromExternalServer(seriesId);
        }

        if (series == null) {
            final String message = "series not found: id = " + seriesId;
            Log.d("SeriesProvider", message);
            throw new NonExistentSeriesException(message);
        }

        this.currentSeries = series;
        return series;
    }

    private Series getSeriesFromExternalServer(String seriesId) {
        Log.d("SeriesProvider", "getting series with id " + seriesId + " from external server");
        return this.theTVDB().getSeries(seriesId);
    }

    private Series getSeriesFromLocalRepository(String seriesId) {
        Log.d("SeriesProvider", "getting series with id " + seriesId + " from local repository");
        return this.localSeriesRepository().getSeries(seriesId);
    }

    private Series getSeriesFromCache(String seriesId) {
        Log.d("SeriesProvider", "getting series with id " + seriesId + " from cache");
        if (this.currentSeries != null && this.currentSeries.getId().equals(seriesId)) {
            return this.currentSeries;
        }

        return null;
    }

    /**
     * @return a 102px x 150px Bitmap of the series' poster, or a clapperboard to be used as a
     *         generic poster
     */
    public Bitmap getPosterOf(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        if (!series.hasPoster()) {
            return this.genericPosterImage();
        }

        return series.getPoster().getImage();
    }

    /**
     * @return a 102px x 150px image of a clapperboard to be used as a generic poster
     */
    private Bitmap genericPosterImage() {
        return BitmapFactory.decodeResource(App.environment().context().getResources(),
                R.drawable.small_poster_clapperboard);
    }

    public Episode getEpisode(String episodeId) {
        return this.localSeriesRepository().getEpisode(episodeId);
    }

    public List<Episode> recentNotSeenEpisodes() {
        List<Episode> recent = new ArrayList<Episode>();

        for (Series s : this.mySeries()) {
            recent.addAll(s.getSeasons().getLastAiredNotSeenEpisodes());
        }

        return recent;
    }

    public List<Episode> upcoming() {
        List<Episode> upcoming = new ArrayList<Episode>();

        for (Series s : this.mySeries()) {
            upcoming.addAll(s.getSeasons().getNextEpisodesToAir());
        }

        return upcoming;
    }

    public void addListener(SeriesProviderListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListenersAboutUnfollowedSeries(Series series) {
        for (final SeriesProviderListener listener : this.listeners) {
            listener.onUnfollowing(series);
        }
    }

    private void notifyListenersAboutFollowedSeries(Series series) {
        for (final SeriesProviderListener listener : this.listeners) {
            listener.onFollowing(series);
        }
    }

    private void notifyListenersAboutEpisodeMarkedAsSeen(Episode episode) {
        for (final SeriesProviderListener listener : this.listeners) {
            listener.onMarkedAsSeen(episode);
        }
    }

    private void notifyListenersAboutEpisodeMarkedAsNotSeen(Episode episode) {
        for (final SeriesProviderListener listener : this.listeners) {
            listener.onMarkedAsNotSeen(episode);
        }
    }

    private void notifyListenersAboutSeasonMarkedAsSeen(Season season) {
        for (final SeriesProviderListener listener : this.listeners) {
            listener.onMarkedAsSeen(season);
        }
    }

    private void notifyListenersAboutSeasonMarkedAsNotSeen(Season season) {
        for (final SeriesProviderListener listener : this.listeners) {
            listener.onMarkedAsNotSeen(season);
        }
    }

    public void markSeasonAsSeen(Season season) {
        season.markAllAsSeen();
        this.localSeriesRepository().updateAll(season.getEpisodes());
        this.notifyListenersAboutSeasonMarkedAsSeen(season);
    }

    public void markSeasonAsNotSeen(Season season) {
        season.markAllAsNotSeen();
        this.localSeriesRepository().updateAll(season.getEpisodes());
        this.notifyListenersAboutSeasonMarkedAsNotSeen(season);
    }

    public void markEpisodeAsSeen(Episode episode) {
        episode.markAsSeen();
        this.localSeriesRepository().update(episode);
        this.notifyListenersAboutEpisodeMarkedAsSeen(episode);
    }

    public void markEpisodeAsNotSeen(Episode episode) {
        episode.markAsNotSeen();
        this.localSeriesRepository().update(episode);
        this.notifyListenersAboutEpisodeMarkedAsNotSeen(episode);
    }
}
