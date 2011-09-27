package br.edu.ufcg.aweseries.test.integration;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.thetvdb.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.TheTVDBStreamFactory;
import br.edu.ufcg.aweseries.thetvdb.UrlSupplier;

public class TheTVDBStreamFactoryTest extends TestCase {

    private final String chuckId = "80348";
    private final String chuckName = "Chuck";
    private final String chuckPoster = "posters/80348-1.jpg";

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
    public void testGettingNullBaseSeriesStreamThrowsException() {
        try {
            factory.streamForBaseSeries(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
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
    public void testGettingNullFullSeriesStreamThrowsException() {
        try {
            factory.streamForFullSeries(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingChuckFullSeriesReturnsChuckFullData() throws IOException {
        InputStream chuckStream = factory.streamForFullSeries(chuckId);

        String contentOfChuckStream = contentOf(chuckStream);

        for (String content : fullSeriesContent) {
            assertThat(contentOfChuckStream, containsString(content));
        }
    }

    // Full Series -------------------------------------------------------------
    public void testGettingNullSeriesPosterThrowsException() {
        try {
            factory.streamForSeriesPosterAt(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingSeriesPosterWithBlankPathThrowsException() {
        try {
            factory.streamForSeriesPosterAt("   \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        try {
            factory.streamForSeriesPosterAt("");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingSeriesPosterWithNonExistentResourcePathThrowException() {
        String nonExistentResourcePath = chuckPoster.substring(0,
                                                               chuckPoster.length() - 3);

        try {
            factory.streamForSeriesPosterAt(nonExistentResourcePath);
            fail("Should have thrown a FileNotFoundException");
        } catch (RuntimeException e) {
            assertThat(e.getCause(), instanceOf(FileNotFoundException.class));
        }
    }

    public void testGettingSeriesPosterReturnsAStreamToABitmapableImage() {
        InputStream posterStream = factory.streamForSeriesPosterAt(chuckPoster);
        assertThat(posterStream, notNullValue());

        assertThat(BitmapFactory.decodeStream(posterStream), notNullValue());
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
