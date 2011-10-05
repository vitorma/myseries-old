package br.edu.ufcg.aweseries;

import java.util.Comparator;
import java.util.TreeSet;

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
 * Most times, it should be gotten from Environment.getSeriesProvider().
 * @see newSeriesProvider()
 */
public class SeriesProvider {

    @Deprecated
    private TreeSet<Series> followedSeries;
    private DatabaseHelper dababaseHelper;

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
    private SeriesProvider() {
        Comparator<Series> nameComparator = new Comparator<Series>() {
            @Override
            public int compare(Series object1, Series object2) {
                return object1.getName().compareTo(object2.getName());
            }
        };

        this.followedSeries = new TreeSet<Series>(nameComparator);
        this.dababaseHelper = new DatabaseHelper(App.getContext());
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
        array = this.dababaseHelper.getAllSeries().toArray(array);
        return array;
    }

    public void wipeFollowedSeries() {
        this.followedSeries.clear();
    }

    public void follow(Series series) {
        if (series == null) {
            return;
        }
        this.dababaseHelper.insert(series);
    }

    /**
     * @return series data for the requested series id.
     * @param id series id
     */
    public Series getSeries(String id) {
        //Temporary implementation
        try {
            Series s = this.dababaseHelper.getSeries(id);
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

    public Bitmap getSmallPoster(Series series) {
        Bitmap poster = this.theTVDB().getSeriesPoster(series);

        if (poster == null) {
            return genericSmallPosterImage();
        }

        return smallPosterFrom(poster);
    }

    private Bitmap genericSmallPosterImage() {
        Bitmap genericPosterImage = BitmapFactory.decodeResource(
                App.getContext().getResources(),
                R.drawable.small_poster_clapperboard);

        return smallPosterFrom(genericPosterImage);
    }

    private Bitmap smallPosterFrom(Bitmap standardPoster) {
        return Bitmap.createScaledBitmap(standardPoster, 51, 75, true);
    }

    public Episode getEpisode(String episodeId) {
        return this.dababaseHelper.getEpisode(episodeId);
    }
}
