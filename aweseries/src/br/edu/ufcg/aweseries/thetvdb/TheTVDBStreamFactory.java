package br.edu.ufcg.aweseries.thetvdb;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class TheTVDBStreamFactory implements StreamFactory {

    private UrlSupplier urlSupplier;

    public TheTVDBStreamFactory(UrlSupplier urlSupplier) {
        this.urlSupplier = urlSupplier;
    }

    @Override
    public InputStream streamForBaseSeries(String seriesId) {
        if (seriesId == null) {
            return null;
        }

        String seriesUrl = this.urlSupplier.getBaseSeriesUrl(seriesId);

        return streamFor(seriesUrl);
    }

    @Override
    public InputStream streamForFullSeries(String seriesId) {
        return null;
    }

    private InputStream streamFor(String url) {
        try {
            return new URL(url).openConnection().getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
