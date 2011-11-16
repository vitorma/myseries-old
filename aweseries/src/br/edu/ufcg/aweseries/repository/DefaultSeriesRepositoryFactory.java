package br.edu.ufcg.aweseries.repository;

import android.content.Context;

public class DefaultSeriesRepositoryFactory implements SeriesRepositoryFactory {

    private Context context;

    public DefaultSeriesRepositoryFactory(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context should not be null");
        }

        this.context = context;
    }

    @Override
    public SeriesRepository newSeriesDatabase() {
        return new SeriesDatabase(this.context);
    }

    @Override
    public SeriesRepository newSeriesCachedRepository() {
        return new SeriesCachedRepository(this.newSeriesDatabase());
    }
}
