package br.edu.ufcg.aweseries.test.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

/**
 * A set of tests that every StreamFactory should pass in order to properly run with AweSeries.
 */
public abstract class StreamFactoryTest extends InstrumentationTestCase {
    
    /**
     * Hook for testing the same properties on other StreamFactories
     */
    protected abstract StreamFactory factory();

    // TODO: turn all these 3 variables into just one -> call everything through attr seriesSample
    private String testSeriesId;
    private String testSeriesName;
    private String testSeriesPoster;

    private final String nonExistentSeriesId = "0";

    private List<String> baseSeriesContent;
    private List<String> fullSeriesOnlyContent;
    private List<String> fullSeriesContent;

    @Override
    public void setUp() {
        this.testSeriesId = SampleSeries.CHUCK.series().getId();
        this.testSeriesName = SampleSeries.CHUCK.series().getName();
        this.testSeriesPoster = SampleSeries.CHUCK.series().getPoster();

        this.baseSeriesContent = Arrays.asList(
                "<id>" + testSeriesId + "</id>",
                "<SeriesName>" + testSeriesName + "</SeriesName>"
        );

        this.fullSeriesOnlyContent = Arrays.asList(
                "<Episode>", "</Episode>"
        );

        this.fullSeriesContent = new ArrayList<String>(baseSeriesContent);
        {
            fullSeriesContent.addAll(fullSeriesOnlyContent);
        }
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
        InputStream testSeriesStream = factory().streamForBaseSeries(testSeriesId);

        String contentOfTestSeriesStream = contentOf(testSeriesStream);

        for (String content : baseSeriesContent) {
            assertThat(contentOfTestSeriesStream, containsString(content));
        }

        for (String content : fullSeriesOnlyContent) {
            assertThat(contentOfTestSeriesStream, not(containsString(content)));
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
        InputStream testSeriesStream = factory().streamForFullSeries(testSeriesId);

        String contentOfTestSeriesStream = contentOf(testSeriesStream);

        for (String content : fullSeriesContent) {
            assertThat(contentOfTestSeriesStream, containsString(content));
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
