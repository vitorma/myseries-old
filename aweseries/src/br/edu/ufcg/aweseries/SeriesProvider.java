package br.edu.ufcg.aweseries;

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
 * 
 * The private constructor avoids instantiation of the SeriesProvider.
 * Most times, it should be gotten from Environment.seriesProvider().
 * @see newSeriesProvider()
 */
public class SeriesProvider {

    /**
     * If you know what you are doing, use this method to instantiate a
     * SeriesProvider.
     * @see SeriesProvider()
     */
    public static SeriesProvider newSeriesProvider() {
        return new SeriesProvider();
    }

    // XXX: It is here because the user can't follow a series yet. Remove it ASAP
    public boolean loadExampleData = false;
    
    /**
     * @see newSeriesProvider()
     */
    private SeriesProvider() {}

    private DatabaseHelper localSeriesRepository() {
        return App.environment().localSeriesRepository();
    }

    private TheTVDB theTVDB() {
        return App.environment().theTVDB();
    }

    /**
     * Returns an array with all followed series.
     * 
     * @return followed series.
     */
    public Series[] mySeries() {
        // XXX: It is here because the user can't follow a series yet. Remove it ASAP
        if (this.loadExampleData) {
            Log.d("SeriesProvider", "Start loading example data");
            final String chuckId = "80348";
            final String tbbtId = "80379";
            final String gotID = "121361";
            final String houseID = "73255";
            final String youngDraculaId = "80248";

            String[] seriesIds = new String[]{chuckId, tbbtId, gotID, houseID, youngDraculaId};
            for (String seriesId : seriesIds) {
                this.follow(this.getSeries(seriesId));
            }

            this.loadExampleData = false;
        }

        //TODO: Implement util.Arrays#toArray
        return this.localSeriesRepository().getAllSeries().toArray(new Series[] {});
    }

    public void follow(Series series) {
        this.localSeriesRepository().insert(series);
    }

    public void wipeFollowedSeries() {
        this.localSeriesRepository().deleteAllSeries();
    }

    public Series getSeries(String seriesId) {
        Series series = this.getSeriesFromLocalRepository(seriesId);

        if (series == null) {
            series = this.getSeriesFromExternalServer(seriesId);
        }

        if (series == null) {
            Log.d("SeriesProvider", "series not found: id = " + seriesId);
            throw new NonExistentSeriesException();
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
     * generic poster
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
        return BitmapFactory.decodeResource(
                App.environment().context().getResources(),
                R.drawable.small_poster_clapperboard);
    }

    public Episode getEpisode(String episodeId) {
        return this.localSeriesRepository().getEpisode(episodeId);
    }
}
