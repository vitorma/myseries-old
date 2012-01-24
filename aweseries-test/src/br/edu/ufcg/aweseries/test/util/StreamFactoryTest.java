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
		SampleSeries.injectInstrumentation(this.getInstrumentation());

		this.testSeriesId = SampleSeries.CHUCK.series().id();
		this.testSeriesName = SampleSeries.CHUCK.series().name();
		this.testSeriesPoster = SampleSeries.CHUCK.posterResourcePath();

		this.baseSeriesContent = Arrays.asList(
				"<id>" + this.testSeriesId + "</id>",
				"<SeriesName>" + this.testSeriesName + "</SeriesName>"
		);

		this.fullSeriesOnlyContent = Arrays.asList(
				"<Episode>", "</Episode>"
		);

		this.fullSeriesContent = new ArrayList<String>(this.baseSeriesContent);
		{
			this.fullSeriesContent.addAll(this.fullSeriesOnlyContent);
		}

		this.seriesSearchContent = Arrays.asList(
				"<Series>", "</Series>",
				"<seriesid>" + this.testSeriesId + "</seriesid>",
				"<language>", "</language>",
				"<SeriesName>" + this.testSeriesName + "</SeriesName>",
				"<FirstAired>", "</FirstAired>",
				"<id>" + this.testSeriesId + "</id>"
		);
	}

	// Full Series -------------------------------------------------------------
	public void testGettingNullFullSeriesStreamThrowsException() {
		try {
			this.factory().streamForFullSeries(null, "en");
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	public void testGettingFullSeriesWithBlankSeriesIdThrowsException() {
		try {
			this.factory().streamForFullSeries(BLANK_STRING, "en");
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	public void testGettingFullSeriesWithNonExistentSeriesIdThrowsException() {
		try {
			this.factory().streamForFullSeries(NON_EXISTENT_SERIES_ID, "en");
			fail("Should have thrown a FileNotFoundException");
		} catch (RuntimeException e) {
			assertThat(e.getCause(), instanceOf(FileNotFoundException.class));
		}
	}

	public void testGettingFullSeriesReturnsFullData() throws IOException {
		InputStream testSeriesStream = this.factory().streamForFullSeries(this.testSeriesId, "en");

		String contentOfTestSeriesStream = this.contentOf(testSeriesStream);

		for (String content : this.fullSeriesContent) {
			assertThat(contentOfTestSeriesStream, containsString(content));
		}
	}

	// Series Poster -----------------------------------------------------------
	public void testGettingNullSeriesPosterThrowsException() {
		try {
			this.factory().streamForSeriesPosterAt(null);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	public void testGettingSeriesPosterWithBlankPathThrowsException() {
		try {
			this.factory().streamForSeriesPosterAt(BLANK_STRING);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	public void testGettingSeriesPosterWithNonExistentResourcePathThrowsException() {
		try {
			this.factory().streamForSeriesPosterAt(NON_EXISTENT_POSTER_RESOURCE_PATH);
			fail("Should have thrown a FileNotFoundException");
		} catch (RuntimeException e) {
			assertThat(e.getCause(), instanceOf(FileNotFoundException.class));
		}
	}

	// FIXME
	public void failing_testGettingSeriesPosterReturnsAStreamToABitmapableImage() {
		InputStream posterStream = this.factory().streamForSeriesPosterAt(this.testSeriesPoster);
		assertThat(posterStream, notNullValue());

		assertThat(BitmapFactory.decodeStream(posterStream), notNullValue());
	}

	// Series Search -----------------------------------------------------------
	// XXX
	public void failing_testSearchingForNullSeriesThrowsAnException() {
		try {
			this.factory().streamForSeriesSearch(null, "en");
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	// XXX
	public void failing_testSearchingForBlankSeriesReturnsNoResults() {
		try {
			this.factory().streamForSeriesSearch(BLANK_STRING, "en");
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	// XXX
	public void failing_testSearchingForValidSeriesReturnsValidResultData() throws IOException {
		InputStream seriesSearchStream = this.factory().streamForSeriesSearch(this.testSeriesName, "en");

		String contentOfSeriesSearchStream = this.contentOf(seriesSearchStream);

		for (String content : this.seriesSearchContent) {
			assertThat(contentOfSeriesSearchStream, containsString(content));
		}
	}

	// Test tools --------------------------------------------------------------
	private String contentOf(InputStream stream) throws IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream should not be null");

		InputStreamReader reader = new InputStreamReader(stream);

		StringBuilder builder = new StringBuilder();

		char[] buffer = new char[1024];
		while (reader.read(buffer) != -1) {
			builder.append(buffer);
		}

		return builder.toString();
	}
}
