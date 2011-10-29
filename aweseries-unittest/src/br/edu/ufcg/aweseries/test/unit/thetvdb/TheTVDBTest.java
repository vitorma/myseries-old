package br.edu.ufcg.aweseries.test.unit.thetvdb;

import static org.mockito.Mockito.mock;
import junit.framework.Assert;

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

    @Test
    public void testGettingNullFullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        Assert.assertNull("TheTVDB should return a null Series for a null seriesId",
                theTVBD.getSeries(null));
    }

    @Test
    public void testGettingBlankFullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        Assert.assertNull("TheTVDB should return a null Series for a blank seriesId",
                theTVBD.getSeries("   \t \t "));
    }
}
