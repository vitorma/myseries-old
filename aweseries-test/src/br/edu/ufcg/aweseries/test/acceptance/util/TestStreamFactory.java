package br.edu.ufcg.aweseries.test.acceptance.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public class TestStreamFactory implements StreamFactory {

    private SampleSeries sampleSeries = SampleSeries.CHUCK;

    @Override
    public InputStream streamForBaseSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        if (seriesId.equals(this.sampleSeries.id())) {
            return this.sampleSeries.baseSeriesStream();
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    @Override
    public InputStream streamForFullSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        if (seriesId.equals(this.sampleSeries.id())) {
            return this.sampleSeries.fullSeriesStream();
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    @Override
    public InputStream streamForSeriesPosterAt(String resourcePath) {
        this.checkIfItIsAValidUrlSuffix(resourcePath, "resourcePath");

        if (resourcePath.equals(this.sampleSeries.posterResourcePath())) {
            return this.sampleSeries.posterStream();
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


