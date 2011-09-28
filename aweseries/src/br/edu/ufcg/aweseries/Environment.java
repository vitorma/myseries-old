package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

/**
 * Singleton repository. Stores all singleton objects. Allow dependency
 * injection.
 */
public class Environment {

    private SeriesProvider seriesProvider;
    private TheTVDB theTVDB;

    private final String apiKey = "6F2B5A871C96FB05";

    /**
     * If you know what you are doing, use this method to instantiate an
     * Environment.
     * @see Environment()
     * @see App.environment()
     */
    public static Environment newEnvironment() {
        return new Environment();
    }
    /**
     * @see newSeriesProvider()
     */
    private Environment() {}

    /**
     * @return the SeriesProvider for the app.
     */
    public SeriesProvider getSeriesProvider() {
        if (this.seriesProvider == null) {
            this.seriesProvider = this.defaultSeriesProvider();
        }
        return this.seriesProvider;
    }

    /**
     * @return a default series provider for the production environment
     */
    private SeriesProvider defaultSeriesProvider() {
        final String chuckId = "80348";
        final String tbbtId = "80379";
        final String gotID = "121361";
        final String houseID = "73255";
        final String youngDraculaId = "80248";

        SeriesProvider sp = SeriesProvider.newSeriesProvider();

        sp.follow(sp.getSeries(chuckId));
        sp.follow(sp.getSeries(tbbtId));
        sp.follow(sp.getSeries(gotID));
        sp.follow(sp.getSeries(houseID));
        sp.follow(sp.getSeries(youngDraculaId));

        return sp;
    }

    /**
     * Set the environment's series provider to newSeriesProvider.
     * If null, a new default series provider will be instantiated.
     */
    public void setSeriesProvider(SeriesProvider newSeriesProvider) {
        this.seriesProvider = newSeriesProvider;
    }

    public TheTVDB theTVDB() {
        if (this.theTVDB == null) {
            this.theTVDB = this.defaultTheTVDB();
        }
        return this.theTVDB;
    }

    private TheTVDB defaultTheTVDB() {
        return new TheTVDB(this.apiKey);
    }

    public void setTheTVDBTo(TheTVDB newTheTVDB) {
        this.theTVDB = newTheTVDB;
    }
}
