package br.edu.ufcg.aweseries.test.unit.thetvdb;

import static org.mockito.Mockito.mock;

import org.junit.Test;

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
}
