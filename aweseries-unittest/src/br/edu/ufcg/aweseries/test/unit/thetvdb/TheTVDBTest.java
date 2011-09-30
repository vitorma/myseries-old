package br.edu.ufcg.aweseries.test.unit.thetvdb;

import java.io.FileNotFoundException;

import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.NonExistentSeriesException;
import br.edu.ufcg.aweseries.thetvdb.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

import static org.mockito.Mockito.*;

public class TheTVDBTest {

    // Constructor -------------------------------------------------------------
    @Test(expected = IllegalArgumentException.class)
    public void testNullApiKey() {
        new TheTVDB((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStreamFactory() {
        new TheTVDB((StreamFactory) null);
    }

    // getSeries ---------------------------------------------------------------
    @Test(expected = IllegalArgumentException.class)
    public void testGettingNullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        theTVBD.getSeries(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGettingBlankSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        theTVBD.getSeries("   \t \t ");
    }

    @Test(expected = NonExistentSeriesException.class)
    public void testGettingNonExistentSeries() {
        String nonExistentSeriesId = "0";

        StreamFactory streamFactoryMock = mock(StreamFactory.class);
        when(streamFactoryMock.streamForBaseSeries(nonExistentSeriesId))
                .thenThrow(new RuntimeException(new FileNotFoundException()));

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        theTVBD.getSeries(nonExistentSeriesId);
    }
}
