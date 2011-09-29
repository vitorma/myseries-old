package br.edu.ufcg.aweseries.test.acceptance.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import br.edu.ufcg.aweseries.test.util.ChuckSeries;
import br.edu.ufcg.aweseries.thetvdb.StreamFactory;

public class TestStreamFactory implements StreamFactory {

    @Override
    public InputStream streamForBaseSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        if (seriesId.equals(ChuckSeries.id)) {
            return ChuckSeries.baseSeriesStream();
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    @Override
    public InputStream streamForFullSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        if (seriesId.equals(ChuckSeries.id)) {
            return ChuckSeries.fullSeriesStream();
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    @Override
    public InputStream streamForSeriesPosterAt(String resourcePath) {
        this.checkIfItIsAValidUrlSuffix(resourcePath, "resourcePath");

        if (resourcePath.equals(ChuckSeries.posterResourcePath)) {
            return ChuckSeries.posterStream();
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    private void checkIfItIsAValidUrlSuffix(String suffix, String parameterName) {
        if (suffix == null) {
            throw new IllegalArgumentException(parameterName + " should not be null");
        }
        if (suffix.trim().isEmpty()) {
            throw new IllegalArgumentException(parameterName + " should not be blank");
        }
    }
}


