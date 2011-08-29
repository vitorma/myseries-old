package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.thetvdb.Series;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

/**
 * Supply series information to the system.
 * 
 * It is a cache proxy for series data. All followed series are cached.
 */
public class SeriesProvider {
    private final int chuckId = 80348;
    private final int tbbtId = 80379;
    private final int gotID = 121361;
    private final int houseID = 73255;

    private final String apiKey = "6F2B5A871C96FB05";
    private final TheTVDB db = new TheTVDB(apiKey);

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

}
