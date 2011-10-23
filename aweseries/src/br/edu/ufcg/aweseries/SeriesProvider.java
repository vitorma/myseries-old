package br.edu.ufcg.aweseries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import br.edu.ufcg.aweseries.data.DatabaseHelper;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;
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

    private HashSet<SeriesProviderListener> listeners;

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
        return this.sortSeriesByName(this.localSeriesRepository().getAllSeries());
    }

    private List<Series> sortSeriesByName(List<Series> series) {
        ArrayList<Series> sorted = new ArrayList<Series>(series);
        Comparator<Series> comparator = new Comparator<Series>() {
            @Override
            public int compare(Series s1, Series s2) {
                return s1.getName().compareTo(s2.getName());
            }
        };
        Collections.sort(sorted, comparator);
        return sorted;
    }

    public void follow(Series series) {
        Series fullSeries = this.theTVDB().getFullSeries(series.getId());
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

        return this.localSeriesRepository().getSeries(series.getId()) != null;
    }

    public void wipeFollowedSeries() {
        for (Series s : this.localSeriesRepository().getAllSeries()) {
            this.notifyListenersAboutUnfollowedSeries(s);
        }

        this.localSeriesRepository().deleteAllSeries();
    }

    public Series[] searchSeries(String seriesName) {
        List<Series> searchResult = this.theTVDB().search(seriesName);

        if (searchResult == null) {
            throw new RuntimeException("no results found for criteria " + seriesName);
        }

        //TODO: Implement util.Arrays#toArray
        return searchResult.toArray(new Series[] {});
    }

    public Series getSeries(String seriesId) {
        Series series = this.getSeriesFromLocalRepository(seriesId);

        if (series == null) {
            series = this.getSeriesFromExternalServer(seriesId);
        }

        if (series == null) {
            final String message = "series not found: id = " + seriesId;
            Log.d("SeriesProvider", message);
            throw new NonExistentSeriesException(message);
        }

        return series;
    }

    private Series getSeriesFromExternalServer(String seriesId) {
        Log.d("SeriesProvider", "getting series with id " + seriesId + " from external server");
        return this.theTVDB().getFullSeries(seriesId);
    }

    private Series getSeriesFromLocalRepository(String seriesId) {
        Log.d("SeriesProvider", "getting series with id " + seriesId + " from local repository");
        return this.localSeriesRepository().getSeries(seriesId);
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

    public void addListener(SeriesProviderListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListenersAboutUnfollowedSeries(Series series) {
        for (SeriesProviderListener listener : this.listeners) {
            listener.onUnfollowing(series);
        }
    }

    private void notifyListenersAboutFollowedSeries(Series series) {
        for (SeriesProviderListener listener : this.listeners) {
            listener.onFollowing(series);
        }
    }
    
    private void notifyListenersAboutEpisodeMarkedAsViewed(Episode episode) {
        for (SeriesProviderListener listener : this.listeners) {
            listener.onEpisodeMarkedAsViewed(episode);
        }
    }

    private void notifyListenersAboutEpisodeMarkedAsNotViewed(Episode episode) {
        for (SeriesProviderListener listener : this.listeners) {
            listener.onEpisodeMarkedAsNotViewed(episode);
        }
    }
    
    public void markEpisodeAsViewed(Episode episode) {
        Episode ep = this.getEpisode(episode.getId());
        ep.markAsViewed();
        localSeriesRepository().update(ep);
        this.notifyListenersAboutEpisodeMarkedAsNotViewed(ep);
    }

    public void markEpisodeAsNotViewed(Episode episode) {
        Episode ep = this.getEpisode(episode.getId());
        ep.markAsNotViewed();
        localSeriesRepository().update(ep);
        this.notifyListenersAboutEpisodeMarkedAsNotViewed(ep);
    }
    
}
