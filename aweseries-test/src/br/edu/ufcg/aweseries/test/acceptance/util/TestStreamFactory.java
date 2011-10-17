package br.edu.ufcg.aweseries.test.acceptance.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public class TestStreamFactory implements StreamFactory {

    private Set<SampleSeries> allSampleSeries = SampleSeries.allSamples;

    @Override
    public InputStream streamForBaseSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        for (SampleSeries s : this.allSampleSeries) {
            if (s.id().equals(seriesId)) {
                return s.baseSeriesStream();
            }
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    @Override
    public InputStream streamForFullSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        for (SampleSeries s : this.allSampleSeries) {
            if (s.id().equals(seriesId)) {
                return s.fullSeriesStream();
            }
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    @Override
    public InputStream streamForSeriesSearch(String seriesName) {
        //TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream streamForSeriesPosterAt(String resourcePath) {
        this.checkIfItIsAValidUrlSuffix(resourcePath, "resourcePath");

        for (SampleSeries s : this.allSampleSeries) {
            if (s.posterResourcePath().equals(resourcePath)) {
                return s.posterStream();
            }
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    private void checkIfItIsAValidUrlSuffix(String suffix, String parameterName) {
        if (suffix == null) {
            throw new IllegalArgumentException(parameterName + " should not be null");
        }
        if (suffix.trim().equals("")) {
            throw new IllegalArgumentException(parameterName + " should not be blank");
        }
    }
}
