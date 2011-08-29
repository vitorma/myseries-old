package br.edu.ufcg.aweseries;

/**
 * Singleton repository. Stores all singleton objects. Allow dependency
 * injection.
 */
public class Environment {
    private static Environment instance;
    public static Environment instance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }

    private SeriesProvider seriesProvider;

    /**
     * @return a default series provider for the production environment
     */
    private SeriesProvider defaultSeriesProvider() {
        return SeriesProvider.newSeriesProvider();
    }

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
     * Set the environment's series provider to newSeriesProvider.
     * If null, a new default series provider will be instantiated.
     */
    public void setSeriesProvider(SeriesProvider newSeriesProvider) {
        this.seriesProvider = newSeriesProvider;
    }
}
