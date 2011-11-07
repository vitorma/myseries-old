package br.edu.ufcg.aweseries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.repository.Repository;
import br.edu.ufcg.aweseries.repository.SeriesCache;
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
    private static final TheTVDB theTVDB = App.environment().theTVDB();

    private Repository<Series> seriesRepository;
    private HashSet<SeriesProviderListener> listeners; //TODO Remove this attribute ASAP

    /**
     * If you know what you are doing, use this method to instantiate a
     * SeriesProvider.
     * 
     * @see SeriesProvider()
     */
    public static SeriesProvider newSeriesProvider() {
        return new SeriesProvider();
    }

    private SeriesProvider() {
        this.seriesRepository = new SeriesCache();
        this.listeners = new HashSet<SeriesProviderListener>();
    }
    
    public Collection<Series> followedSeries() {
        return this.seriesRepository.getAll();
    }
    
    public Series[] searchSeries(String seriesName) {
        final List<Series> searchResult = theTVDB.search(seriesName);
        
        if (searchResult == null) {
            throw new RuntimeException("no results found for criteria " + seriesName);
        }
        
        //TODO: Implement util.Arrays#toArray
        return searchResult.toArray(new Series[] {});
    }

    public void follow(Series series) {
        final Series fullSeries = theTVDB.getSeries(series.getId());
        this.seriesRepository.insert(fullSeries);
        this.notifyListenersAboutFollowedSeries(fullSeries);
    }

    public void unfollow(Series series) {
        this.seriesRepository.delete(series);
        this.notifyListenersAboutUnfollowedSeries(series);
    }

    public boolean follows(Series series) {
        return this.seriesRepository.contains(series);
    }

    public void wipeFollowedSeries() {
        for (final Series s : this.followedSeries()) {
            this.notifyListenersAboutUnfollowedSeries(s);
        }

        this.seriesRepository.clear();
    }
    
    public Series getSeries(String seriesId) {
        return this.seriesRepository.get(seriesId);
    }
    
    public List<Episode> recentNotSeenEpisodes() {
        List<Episode> recent = new ArrayList<Episode>();
        
        for (Series s : this.followedSeries()) {
            recent.addAll(s.getSeasons().getLastAiredNotSeenEpisodes());
        }
        
        return recent;
    }
    
    public List<Episode> nextEpisodesToAir() {
        List<Episode> upcoming = new ArrayList<Episode>();
        
        for (Series s : this.followedSeries()) {
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
        this.notifyListenersAboutSeasonMarkedAsSeen(season);
    }
    
    public void markSeasonAsNotSeen(Season season) {
        season.markAllAsNotSeen();
        this.seriesRepository.update(this.getSeries(season.getSeriesId()));
        this.notifyListenersAboutSeasonMarkedAsNotSeen(season);
    }
    
    public void markEpisodeAsSeen(Episode episode) {
        episode.markAsSeen();
        this.seriesRepository.update(this.getSeries(episode.getSeriesId()));
        this.notifyListenersAboutEpisodeMarkedAsSeen(episode);
    }
    
    public void markEpisodeAsNotSeen(Episode episode) {
        episode.markAsNotSeen();
        this.seriesRepository.update(this.getSeries(episode.getSeriesId()));
        this.notifyListenersAboutEpisodeMarkedAsNotSeen(episode);
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
}
