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
import br.edu.ufcg.aweseries.test.util.ChuckSeries;
import br.edu.ufcg.aweseries.thetvdb.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.TheTVDBStreamFactory;

public class TheTVDBStreamFactoryTest extends TestCase {

    private final String testSeriesId = ChuckSeries.id;
    private final String testSeriesName = ChuckSeries.name;
    private final String testSeriesPoster = ChuckSeries.posterResourcePath;

    private final String nonExistentSeriesId = "0";

    private final List<String> baseSeriesContent = Arrays.asList(
            "<id>" + testSeriesId + "</id>",
            "<SeriesName>" + testSeriesName + "</SeriesName>"
    );

    private final List<String> fullSeriesOnlyContent = Arrays.asList(
            "<Episode>", "</Episode>"
    );

    private final List<String> fullSeriesContent = new ArrayList<String>(baseSeriesContent);
    {
        fullSeriesContent.addAll(fullSeriesOnlyContent);
    }

    private final String apiKey = "6F2B5A871C96FB05";

    private StreamFactory factory = new TheTVDBStreamFactory(apiKey);

    /**
     * Hook for testing the same properties on other StreamFactories
     */
    protected StreamFactory factory() {
        return this.factory;
    }

    // Base Series -------------------------------------------------------------
    public void testGettingNullBaseSeriesStreamThrowsException() {
        try {
            factory().streamForBaseSeries(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingBaseSeriesWithBlankSeriesIdThrowsException() {
        try {
            factory().streamForBaseSeries("   \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        try {
            factory().streamForBaseSeries("");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingBaseSeriesWithNonExistentSeriesIdThrowsException() {
        try {
            factory().streamForBaseSeries(nonExistentSeriesId);
            fail("Should have thrown a FileNotFoundException");
        } catch (RuntimeException e) {
            assertThat(e.getCause(), instanceOf(FileNotFoundException.class));
        }
    }

    public void testGettingBaseSeriesReturnsBaseData() throws IOException {
        InputStream chuckStream = factory().streamForBaseSeries(testSeriesId);

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
            factory().streamForFullSeries(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingFullSeriesWithBlankSeriesIdThrowsException() {
        try {
            factory().streamForFullSeries("   \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        try {
            factory().streamForFullSeries("");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingFullSeriesWithNonExistentSeriesIdThrowsException() {
        try {
            factory().streamForFullSeries(nonExistentSeriesId);
            fail("Should have thrown a FileNotFoundException");
        } catch (RuntimeException e) {
            assertThat(e.getCause(), instanceOf(FileNotFoundException.class));
        }
    }

    public void testGettingFullSeriesReturnsFullData() throws IOException {
        InputStream chuckStream = factory().streamForFullSeries(testSeriesId);

        String contentOfChuckStream = contentOf(chuckStream);

        for (String content : fullSeriesContent) {
            assertThat(contentOfChuckStream, containsString(content));
        }
    }

    // Full Series -------------------------------------------------------------
    public void testGettingNullSeriesPosterThrowsException() {
        try {
            factory().streamForSeriesPosterAt(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingSeriesPosterWithBlankPathThrowsException() {
        try {
            factory().streamForSeriesPosterAt("   \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        try {
            factory().streamForSeriesPosterAt("");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingSeriesPosterWithNonExistentResourcePathThrowsException() {
        String nonExistentResourcePath = testSeriesPoster.substring(0,
                                                               testSeriesPoster.length() - 3);

        try {
            factory().streamForSeriesPosterAt(nonExistentResourcePath);
            fail("Should have thrown a FileNotFoundException");
        } catch (RuntimeException e) {
            assertThat(e.getCause(), instanceOf(FileNotFoundException.class));
        }
    }

    public void testGettingSeriesPosterReturnsAStreamToABitmapableImage() {
        InputStream posterStream = factory().streamForSeriesPosterAt(testSeriesPoster);
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
