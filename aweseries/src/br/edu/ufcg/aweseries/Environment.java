package br.edu.ufcg.aweseries;

import android.content.Context;
import br.edu.ufcg.aweseries.repository.SeriesDatabase;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

/**
 * Singleton repository. Stores all singleton objects. Allow dependency
 * injection.
 */
public class Environment {

    private Context context;

    private SeriesProvider seriesProvider;
    private TheTVDB theTVDB;
    private SeriesDatabase localSeriesRepository;

    private final String apiKey = "6F2B5A871C96FB05";

    /**
     * If you know what you are doing, use this method to instantiate an
     * Environment.
     * @see Environment()
     * @see App.environment()
     */
    public static Environment newEnvironment(Context context) {
        return new Environment(context);
    }

    /**
     * @see newSeriesProvider()
     */
    private Environment(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context should not be null");
        }
        this.context = context;
    }

    /**
     * @return the application context.
     */
    public Context context() {
        return this.context;
    }

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
        return SeriesProvider.newSeriesProvider();
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

    /**
     * @return the local repository of series.
     */
    public SeriesDatabase localSeriesRepository() {
        if (this.localSeriesRepository == null) {
            this.localSeriesRepository = this.defaultLocalSeriesRepository();
        }
        return this.localSeriesRepository;
    }

    /**
     * @return a default thetvdb interface for the production environment
     */
    private SeriesDatabase defaultLocalSeriesRepository() {
        return new SeriesDatabase(this.context());
    }

    /**
     * Set the environment's series repository to newSeriesRepository.
     * If null, a new default series repository will be instantiated.
     */
    public void setLocalSeriesRepositoryTo(SeriesDatabase newSeriesRepository) {
        this.localSeriesRepository = newSeriesRepository;
    }
}
