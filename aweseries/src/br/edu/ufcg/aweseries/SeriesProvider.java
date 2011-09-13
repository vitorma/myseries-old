package br.edu.ufcg.aweseries;

import android.graphics.Bitmap;
import br.edu.ufcg.aweseries.thetvdb.Series;
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
    private final int chuckId = 80348;
    private final int tbbtId = 80379;
    private final int gotID = 121361;
    private final int houseID = 73255;
    private final int youngDraculaId = 68779;

    private final String apiKey = "6F2B5A871C96FB05";
    private final TheTVDB db = new TheTVDB(apiKey);
    
    /**
     * @see newSeriesProvider()
     */
    private SeriesProvider() {}

    /**
     * If you know what you are doing, use this method to instantiate a
     * SeriesProvider.
     * @see SeriesProvider()
     */
    public static SeriesProvider newSeriesProvider() {
        return new SeriesProvider();
    }

    /**
     * Returns an array with all followed series.
     * 
     * @return followed series.
     */
    public Series[] mySeries() {
        try {
            Series[] series = new Series[4];
            series[0] = db.getSeries(chuckId);
            series[1] = db.getSeries(gotID);
            series[2] = db.getSeries(houseID);
            series[3] = db.getSeries(tbbtId);
            //series[4] = db.getSeries(youngDraculaId);
            
            return series;
        } catch (Exception e) {
            return new Series[] { };
        }
    }

    /**
     * @return series data for the requested series id.
     * @param id series id
     */
    public Series getSeries(int id) {
        return this.db.getSeries(id);
    }

	public Bitmap getPoster(Series series) {
		return this.db.getSeriesPoster(series);
	}

}
