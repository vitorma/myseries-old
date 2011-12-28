/*
 *   StreamFactoryTest.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */


package br.edu.ufcg.aweseries.test.util;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
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

    private static final String NON_EXISTENT_POSTER_RESOURCE_PATH = "nonExistent";
    private static final String NON_EXISTENT_SERIES_ID = "0";

    private static final String BLANK_STRING = "   \t  \n \t  ";

    /**
     * Hook for testing the same properties on other StreamFactories
     */
    protected abstract StreamFactory factory();

    private String testSeriesId;
    private String testSeriesName;
    private String testSeriesPoster;

    private List<String> baseSeriesContent;
    private List<String> fullSeriesOnlyContent;
    private List<String> fullSeriesContent;
    private List<String> seriesSearchContent;

    @Override
    public void setUp() {
        SampleSeries.injectInstrumentation(getInstrumentation());

        this.testSeriesId = SampleSeries.CHUCK.series().getId();
        this.testSeriesName = SampleSeries.CHUCK.series().getName();
        this.testSeriesPoster = SampleSeries.CHUCK.posterResourcePath();

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

        this.seriesSearchContent = Arrays.asList(
                "<Series>", "</Series>",
                "<seriesid>" + testSeriesId + "</seriesid>",
                "<language>", "</language>",
                "<SeriesName>" + testSeriesName + "</SeriesName>",
                "<FirstAired>", "</FirstAired>",
                "<id>" + testSeriesId + "</id>"
        );
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
            factory().streamForBaseSeries(BLANK_STRING);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingBaseSeriesWithNonExistentSeriesIdThrowsException() {
        try {
            factory().streamForBaseSeries(NON_EXISTENT_SERIES_ID);
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
            factory().streamForFullSeries(BLANK_STRING);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingFullSeriesWithNonExistentSeriesIdThrowsException() {
        try {
            factory().streamForFullSeries(NON_EXISTENT_SERIES_ID);
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

    // Series Poster -----------------------------------------------------------
    public void testGettingNullSeriesPosterThrowsException() {
        try {
            factory().streamForSeriesPosterAt(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingSeriesPosterWithBlankPathThrowsException() {
        try {
            factory().streamForSeriesPosterAt(BLANK_STRING);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingSeriesPosterWithNonExistentResourcePathThrowsException() {
        try {
            factory().streamForSeriesPosterAt(NON_EXISTENT_POSTER_RESOURCE_PATH);
            fail("Should have thrown a FileNotFoundException");
        } catch (RuntimeException e) {
            assertThat(e.getCause(), instanceOf(FileNotFoundException.class));
        }
    }

    // FIXME
    public void failing_testGettingSeriesPosterReturnsAStreamToABitmapableImage() {
        InputStream posterStream = factory().streamForSeriesPosterAt(testSeriesPoster);
        assertThat(posterStream, notNullValue());

        assertThat(BitmapFactory.decodeStream(posterStream), notNullValue());
    }

    // Series Search -----------------------------------------------------------
    // XXX
    public void failing_testSearchingForNullSeriesThrowsAnException() {
        try {
            this.factory().streamForSeriesSearch(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    // XXX
    public void failing_testSearchingForBlankSeriesReturnsNoResults() {
        try {
            this.factory().streamForSeriesSearch(BLANK_STRING);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    // XXX
    public void failing_testSearchingForValidSeriesReturnsValidResultData() throws IOException {
        InputStream seriesSearchStream = factory().streamForSeriesSearch(testSeriesName);

        String contentOfSeriesSearchStream = contentOf(seriesSearchStream);

        for (String content : this.seriesSearchContent) {
            assertThat(contentOfSeriesSearchStream, containsString(content));
        }
    }

    // Test tools --------------------------------------------------------------
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
