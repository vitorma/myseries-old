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
    
    private SeriesProvider defaultSeriesProvider() {
        return new SeriesProvider();
    }

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
