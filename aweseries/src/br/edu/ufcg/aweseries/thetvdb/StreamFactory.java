package br.edu.ufcg.aweseries.thetvdb;

import java.io.InputStream;

public interface StreamFactory {

    public InputStream streamForSeries(String seriesId);
}
