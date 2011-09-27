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
            throw new IllegalArgumentException("seriesId should not be null");
        }

        String baseSeriesUrl = this.urlSupplier.getBaseSeriesUrl(seriesId);
        return streamFor(baseSeriesUrl);
    }

    @Override
    public InputStream streamForFullSeries(String seriesId) {
        if (seriesId == null) {
            throw new IllegalArgumentException("seriesId should not be null");
        }

        String fullSeriesUrl = this.urlSupplier.getFullSeriesUrl(seriesId);
        return streamFor(fullSeriesUrl);
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
