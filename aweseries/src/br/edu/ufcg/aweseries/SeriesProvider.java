package br.edu.ufcg.aweseries;

import java.util.Comparator;
import java.util.TreeSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;
import br.edu.ufcg.aweseries.thetvdb.season.Season;
import br.edu.ufcg.aweseries.thetvdb.series.Series;

/**
 * Supply series information to the system. It is a cache proxy for series
 * data. All followed series are cached.
 * 
 * The private constructor avoids instantiation of the SeriesProvider.
 * Most times, it should be gotten from Environment.getSeriesProvider().
 * @see newSeriesProvider()
 */
public class SeriesProvider {

    private TreeSet<Series> followedSeries;

    /**
     * If you know what you are doing, use this method to instantiate a
     * SeriesProvider.
     * @see SeriesProvider()
     */
    public static SeriesProvider newSeriesProvider() {
        return new SeriesProvider();
    }

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
        // It is very ugly, but is here because 
        //     return (Series[]) this.followedSeries.toArray();
        // generates a ClassCastException (I don't know why).

        Series[] array = {};
        array = this.followedSeries.toArray(array);
        return array;
    }

    public void follow(Series series) {
        this.followedSeries.add(series);
    }

    /**
     * @return series data for the requested series id.
     * @param id series id
     */
    public Series getSeries(String id) {
        return this.theTVDB().getSeries(id);
    }

    public Season[] getSeasons(Series series) {
        if (series == null) {
            return new Season[] {};
        }
        return this.theTVDB().getSeasons(series.getId()).toArray();
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
}
