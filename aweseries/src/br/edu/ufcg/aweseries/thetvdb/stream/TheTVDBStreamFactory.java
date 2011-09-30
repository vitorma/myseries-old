package br.edu.ufcg.aweseries.thetvdb.stream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import br.edu.ufcg.aweseries.thetvdb.stream.url.UrlSupplier;


public class TheTVDBStreamFactory implements StreamFactory {

    private UrlSupplier urlSupplier;

    public TheTVDBStreamFactory(String apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("apiKey should not be null");
        }
        this.urlSupplier = new UrlSupplier(apiKey);
    }

    @Override
    public InputStream streamForBaseSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        String baseSeriesUrl = this.urlSupplier.getBaseSeriesUrl(seriesId);
        return buffered(streamFor(baseSeriesUrl));
    }

    @Override
    public InputStream streamForFullSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        String fullSeriesUrl = this.urlSupplier.getFullSeriesUrl(seriesId);
        return buffered(streamFor(fullSeriesUrl));
    }

    /**
     * @return an InputStream for the poster at <bannermirror>/resourcePaths
     */
    @Override
    public InputStream streamForSeriesPosterAt(String resourcePath) {
        this.checkIfItIsAValidUrlSuffix(resourcePath, "resourcePath");

        String seriesPosterUrl = this.urlSupplier.getSeriesPosterUrl(resourcePath);
        return streamFor(seriesPosterUrl);
    }

    private void checkIfItIsAValidUrlSuffix(String suffix, String parameterName) {
        if (suffix == null) {
            throw new IllegalArgumentException(parameterName + " should not be null");
        }
        if (suffix.trim().isEmpty()) {
            throw new IllegalArgumentException(parameterName + " should not be blank");
        }
    }

    private BufferedInputStream buffered(InputStream stream) {
        return new BufferedInputStream(stream);
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
