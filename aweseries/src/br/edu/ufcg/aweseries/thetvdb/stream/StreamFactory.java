package br.edu.ufcg.aweseries.thetvdb.stream;

import java.io.InputStream;

public interface StreamFactory {

    public InputStream streamForBaseSeries(String seriesId);

    public InputStream streamForFullSeries(String seriesId);

    public InputStream streamForSeriesPosterAt(String resourcePath);

    public InputStream streamForSeriesSearch(String seriesName);
}
