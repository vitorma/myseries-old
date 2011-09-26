package br.edu.ufcg.aweseries.thetvdb;

import java.io.InputStream;

public interface StreamFactory {

    public InputStream streamForBaseSeries(String seriesId);

    public InputStream streamForFullSeries(String seriesId);
}
