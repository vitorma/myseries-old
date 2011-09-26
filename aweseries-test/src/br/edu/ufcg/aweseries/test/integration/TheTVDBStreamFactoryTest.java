package br.edu.ufcg.aweseries.test.integration;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import junit.framework.TestCase;
import br.edu.ufcg.aweseries.thetvdb.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.TheTVDBStreamFactory;
import br.edu.ufcg.aweseries.thetvdb.UrlSupplier;

public class TheTVDBStreamFactoryTest extends TestCase {

    private final String chuckId = "80348";
    private final String chuckName = "Chuck";

    private final List<String> baseSeriesContent = Arrays.asList(
            "<id>" + chuckId + "</id>",
            "<SeriesName>" + chuckName + "</SeriesName>"
    );

    private final List<String> fullSeriesOnlyContent = Arrays.asList(
            "<Episode>", "</Episode>"
    );

    private final List<String> fullSeriesContent = new ArrayList<String>(baseSeriesContent);
    {
        fullSeriesContent.addAll(fullSeriesOnlyContent);
    }

    private StreamFactory factory;

    private final String apiKey = "6F2B5A871C96FB05";
    private UrlSupplier urlSupplier = new UrlSupplier(apiKey);

    public void setUp() {
        this.factory = new TheTVDBStreamFactory(urlSupplier);
    }

    // Base Series -------------------------------------------------------------
    public void testGettingNullBaseSeriesStreamReturnsNull() {
        assertThat(factory.streamForBaseSeries(null), nullValue());
    }

    public void testGettingChuckBaseSeriesReturnsChuckBaseData() throws IOException {
        InputStream chuckStream = factory.streamForBaseSeries(chuckId);

        String contentOfChuckStream = contentOf(chuckStream);

        for (String content : baseSeriesContent) {
            assertThat(contentOfChuckStream, containsString(content));
        }

        for (String content : fullSeriesOnlyContent) {
            assertThat(contentOfChuckStream, not(containsString(content)));
        }
    }

    // Full Series -------------------------------------------------------------
    public void testGettingNullFullSeriesStreamReturnsNull() {
        assertThat(factory.streamForFullSeries(null), nullValue());
    }

    public void testGettingChuckFullSeriesReturnsChuckFullData() {
        assertThat(factory.streamForFullSeries(chuckId), not(nullValue()));
    }

    private String contentOf(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("stream should not be null");
        }

        InputStreamReader reader = new InputStreamReader(stream);

        StringBuilder builder = new StringBuilder();

        char[] buffer = new char[1024];
        while (reader.read(buffer) != -1) {
            builder.append(buffer);
        }

        return builder.toString();
    }
}
