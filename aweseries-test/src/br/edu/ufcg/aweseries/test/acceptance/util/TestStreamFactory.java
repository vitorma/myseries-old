package br.edu.ufcg.aweseries.test.acceptance.util;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.edu.ufcg.aweseries.thetvdb.StreamFactory;

public class TestStreamFactory implements StreamFactory {

    @Override
    public InputStream streamForBaseSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        if (seriesId.equals(ChuckSeries.id)) {
            return new ByteArrayInputStream(ChuckSeries.baseSeries.getBytes());
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
    }

    @Override
    public InputStream streamForFullSeries(String seriesId) {
        this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

        if (seriesId.equals(ChuckSeries.id)) {
            return new ByteArrayInputStream(ChuckSeries.fullSeries.getBytes());
        }

        throw new RuntimeException(
                new FileNotFoundException("TestStream doesn't have data about that series")); 
        
    }

    @Override
    public InputStream streamForSeriesPosterAt(String resourcePath) {
        this.checkIfItIsAValidUrlSuffix(resourcePath, "resourcePath");

        // TODO Auto-generated method stub
        return null;
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


