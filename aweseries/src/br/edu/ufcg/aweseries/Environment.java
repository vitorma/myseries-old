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
    public SeriesProvider seriesProvider() {
        if (this.seriesProvider == null) {
            this.seriesProvider = this.defaultSeriesProvider();
        }
        return this.seriesProvider;
    }

    /**
     * @return a default series provider for the production environment
     */
    private SeriesProvider defaultSeriesProvider() {
        // XXX: It is here because the user can't follow a series yet. Remove it ASAP
        SeriesProvider sp = SeriesProvider.newSeriesProvider();
        sp.loadExampleData = true;
        return sp;
    }

    /**
     * Set the environment's series provider to newSeriesProvider.
     * If null, a new default series provider will be instantiated.
     */
    public void setSeriesProvider(SeriesProvider newSeriesProvider) {
        this.seriesProvider = newSeriesProvider;
    }

    /**
     * @return the thetvdb interface for the app.
     */
    public TheTVDB theTVDB() {
        if (this.theTVDB == null) {
            this.theTVDB = this.defaultTheTVDB();
        }
        return this.theTVDB;
    }

    /**
     * @return a default thetvdb interface for the production environment
     */
    private TheTVDB defaultTheTVDB() {
        return new TheTVDB(this.apiKey);
    }

    /**
     * Set the environment's thetvdb interface to newTheTVDB.
     * If null, a new default thetvdb interface will be instantiated.
     */
    public void setTheTVDBTo(TheTVDB newTheTVDB) {
        this.theTVDB = newTheTVDB;
    }
}
