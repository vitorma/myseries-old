package br.edu.ufcg.aweseries.test.integration;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;
import br.edu.ufcg.aweseries.thetvdb.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.TheTVDBStreamFactory;
import br.edu.ufcg.aweseries.thetvdb.UrlSupplier;

public class TheTVDBStreamFactoryTest extends TestCase {

    private final String chuckId = "80348";
    private final String chuckName = "Chuck";

    private StreamFactory factory;

    private final String apiKey = "6F2B5A871C96FB05";
    private UrlSupplier urlSupplier = new UrlSupplier(apiKey);

    public void setUp() {
        this.factory = new TheTVDBStreamFactory(urlSupplier);
    }

    public void testGettingNullSeriesStreamReturnsNull() {
        assertThat(factory.streamForSeries(null), nullValue());
    }

    public void testGettingChuckSeriesReturnsAnInstance() {
        InputStream chuckStream = factory.streamForSeries(chuckId);
        assertThat(chuckStream, not(nullValue()));
    }

    public void testGettingChuckSeriesReturnsChuckData() throws IOException {
        InputStream chuckStream = factory.streamForSeries(chuckId);

        String contentOfChuckStream = contentOf(chuckStream);

        assertThat(contentOfChuckStream,
                   containsString("<id>" + chuckId + "</id>"));
        assertThat(contentOfChuckStream,
                   containsString("<SeriesName>" + chuckName + "</SeriesName>"));
    }

    private String contentOf(InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(stream);

        StringBuilder builder = new StringBuilder();

        char[] buffer = new char[1024];
        while (reader.read(buffer) != -1) {
            builder.append(buffer);
        }

        return builder.toString();
    }
}
