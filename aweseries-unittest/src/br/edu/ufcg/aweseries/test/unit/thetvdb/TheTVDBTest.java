package br.edu.ufcg.aweseries.test.unit.thetvdb;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;

import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.NonExistentSeriesException;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public class TheTVDBTest {

    // Constructor --------------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void testNullApiKey() {
        new TheTVDB((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStreamFactory() {
        new TheTVDB((StreamFactory) null);
    }

    // getSeries ----------------------------------------------------------------------------------

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

    // getFullSeries ------------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void testGettingNullFullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        theTVBD.getFullSeries(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGettingBlankFullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        theTVBD.getFullSeries("   \t \t ");
    }

    @Test(expected = NonExistentSeriesException.class)
    public void testGettingNonExistentFullSeries() {
        String nonExistentSeriesId = "0";

        StreamFactory streamFactoryMock = mock(StreamFactory.class);
        when(streamFactoryMock.streamForFullSeries(nonExistentSeriesId))
                .thenThrow(new RuntimeException(new FileNotFoundException()));

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        theTVBD.getFullSeries(nonExistentSeriesId);
    }
}
