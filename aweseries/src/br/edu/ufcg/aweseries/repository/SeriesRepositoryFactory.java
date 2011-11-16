package br.edu.ufcg.aweseries.repository;

public interface SeriesRepositoryFactory {

    public SeriesRepository newSeriesDatabase();

    public SeriesRepository newSeriesCachedRepository();
}
