package br.edu.ufcg.aweseries;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.data.DatabaseHelper;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
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
            final String chuckId = "80348";
            final String tbbtId = "80379";
            final String gotID = "121361";
            final String houseID = "73255";
            final String youngDraculaId = "80248";
            
            this.follow(this.getSeries(chuckId));
            this.follow(this.getSeries(tbbtId));
            this.follow(this.getSeries(gotID));
            this.follow(this.getSeries(houseID));
            this.follow(this.getSeries(youngDraculaId));

            this.loadExampleData = false;
        }

        // It is very ugly, but is here because 
        //     return (Series[]) this.followedSeries.toArray();
        // generates a ClassCastException (I don't know why).

        Series[] array = {};
        array = this.localSeriesRepository().getAllSeries().toArray(array);
        return array;
    }

    public void wipeFollowedSeries() {
        this.localSeriesRepository().deleteAllSeries();
    }

    public void follow(Series series) {
        if (series == null) {
            return;
        }
        this.localSeriesRepository().insert(series);
    }

    /**
     * @return series data for the requested series id.
     * @param id series id
     */
    public Series getSeries(String id) {
        //Temporary implementation
        try {
            Series s = this.localSeriesRepository().getSeries(id);
            return s;
        } catch (Exception e) {
            return this.theTVDB().getFullSeries(id);
        }
    }

    @Deprecated
    public Season[] getSeasons(Series series) {
        if (series == null) {
            return new Season[] {};
        }
        return series.getSeasons().toArray();
    }

    /**
     * @return a 102px x 150px Bitmap of the series' poster, or a clapperboard to be used as a
     * generic poster
     */
    public Bitmap getPosterOf(Series series) {
        Bitmap poster = this.theTVDB().getSeriesPoster(series);

        if (poster == null) {
            return genericPosterImage();
        }

        return Bitmap.createScaledBitmap(poster, 102, 150, true);
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
