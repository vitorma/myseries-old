package br.edu.ufcg.aweseries.thetvdb.stream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import br.edu.ufcg.aweseries.thetvdb.stream.url.UrlSupplier;
import br.edu.ufcg.aweseries.util.Strings;

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

    @Override
    public InputStream streamForSeriesPosterAt(String resourcePath) {
        this.checkIfItIsAValidUrlSuffix(resourcePath, "resourcePath");

        String seriesPosterUrl = this.urlSupplier.getSeriesPosterUrl(resourcePath);
        return streamFor(seriesPosterUrl);
    }

    @Override
    public InputStream streamForSeriesSearch(String seriesName) {
        this.checkIfItIsAValidUrlSuffix(seriesName, "seriesName");

        String seriesSearchUrl = this.urlSupplier.getSeriesSearchUrl(seriesName);
        return buffered(streamFor(seriesSearchUrl));
    }

    private void checkIfItIsAValidUrlSuffix(String suffix, String parameterName) {
        if (suffix == null) {
            throw new IllegalArgumentException(parameterName + " should not be null");
        }
        if (Strings.isBlank(suffix)) {
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
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
