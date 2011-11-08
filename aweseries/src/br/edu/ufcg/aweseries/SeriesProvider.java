package br.edu.ufcg.aweseries;

import java.util.ArrayList;
import java.util.Collection;
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
        // TODO: use the right interface for this job
        /*
        this.notifyListenersAboutFollowedSeries(fullSeries);
        */
    }

    public void unfollow(Series series) {
        this.seriesRepository.delete(series);
        // TODO: use the right interface for this job
        /*
        this.notifyListenersAboutUnfollowedSeries(series);
        */
    }

    public boolean follows(Series series) {
        return this.seriesRepository.contains(series);
    }

    public void wipeFollowedSeries() {
        // TODO: use the right interface for this job
        /*
        for (final Series s : this.followedSeries()) {
            this.notifyListenersAboutUnfollowedSeries(s);
        }
        */

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
}
