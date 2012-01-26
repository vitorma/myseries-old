/*
 *   TheTVDBDoubleTest.java
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

package br.edu.ufcg.aweseries.test;

import static br.edu.ufcg.aweseries.test.matchers.SeriesMatchers.hasId;
import static br.edu.ufcg.aweseries.test.matchers.SeriesMatchers.namedAs;
import static br.edu.ufcg.aweseries.test.matchers.SeriesMatchers.overviewedAs;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.series_source.SeriesNotFoundException;

public class TheTVDBDoubleTest extends TestCase {

	private static final String LANGUAGE_EN = "en";
	private static final String LANGUAGE_PT = "pt";
	private static final String LANGUAGE_ES = "es";
	private static final String UNAVAILABLE_LANGUAGE = "zz";

	private TheTVDBDouble theTVDB;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		// our code
		this.theTVDB = new TheTVDBDouble();
	}

	@Override
	public void tearDown() throws Exception {
		// our code
		super.tearDown();
	}

	// Create Series
	public void testCreateSeriesWithName() {
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name");

		// Fetch
		Series fetched = this.theTVDB.fetchSeries(123, LANGUAGE_EN);
		assertThat(fetched, hasId(123));
		assertThat(fetched, namedAs("Given Name"));

		// Search
		Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_EN);
		assertThat(results, hasItem(namedAs("Given Name")));
	}

	public void testCreateSeriesWithNullLanguageThrowsException() {
		try {
			this.theTVDB.createSeries(null);
			fail("Should have thrown an IllegalArgumentException for null language");
		} catch (IllegalArgumentException e) {}
	}

	public void testCreateSeriesWithUnavailableLanguageThrowsException() {
		try {
			this.theTVDB.createSeries(UNAVAILABLE_LANGUAGE);
			fail("Should have thrown an IllegalArgumentException for unavailable language");
		} catch (IllegalArgumentException e) {}
	}

	public void testCreateSeriesWithNullAttributesThrowsException() {
		try {
			this.theTVDB.createSeries(LANGUAGE_EN, (String) null);
			fail("Should have thrown an IllegalArgumentException for null attribute");
		} catch (IllegalArgumentException e) {}
	}

	// Search
	public void testWhenNoResultsAreFoundReturnsAnEmptyCollection() {
		Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_EN);

		assertTrue(results.isEmpty());
	}

	public void testEnglishUsualSearchReturnsTheSameResultOnlyOnce() {
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");

		Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_EN);

		assertThat(results, hasItem(namedAs("Given Name")));
		assertThat(results.size(), equalTo(1));
	}

	public void testNotSimilarResultsAreNotReturned() {
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Another Series");

		Collection<Series> results = this.theTVDB.searchFor("Another", LANGUAGE_EN);

		assertThat(results, hasItem(namedAs("Another Series")));
		assertThat(results, not(hasItem(namedAs("Given Name"))));
	}

	public void testWhatShouldHappenInAUsualLocalizedSearch() {
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Another Series");
		this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");
		this.theTVDB.createSeries(LANGUAGE_ES, "name : Given lo Malo");

		Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_PT);

		assertThat(results, hasItem(namedAs("Given Name")));
		assertThat(results, hasItem(namedAs("Cavalo Given nao se Olha os Dentes")));
		assertThat(results, not(hasItem(namedAs("Another Series"))));
		assertThat(results, not(hasItem(namedAs("Given lo Malo"))));
	}

	// Search Localization
	public void testLocalizedSearchReturnsLocalizedResults() {
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
		this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");

		Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_PT);

		assertThat(results, hasItem(namedAs("Cavalo Given nao se Olha os Dentes")));
	}

	public void testLocalizedSearchReturnsOnlyLocalizedResultsForTheGivenLocale() {
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
		this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");
		this.theTVDB.createSeries(LANGUAGE_ES, "name : Given lo Malo");

		Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_PT);

		assertThat(results, not(hasItem(namedAs("Given lo Malo"))));
	}

	public void testEnglishSearchOnlyReturnsResultsInEnglish() {
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
		this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");

		Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_EN);

		assertThat(results, not(hasItem(namedAs("Cavalo Given nao se Olha os Dentes"))));
	}

	public void testLocalizedSearchAlsoReturnsResultsInEnglish() {
		this.theTVDB.createSeries(LANGUAGE_EN, "name : Given Name");
		this.theTVDB.createSeries(LANGUAGE_PT, "name : Cavalo Given nao se Olha os Dentes");

		Collection<Series> results = this.theTVDB.searchFor("Given", LANGUAGE_PT);

		assertThat(results, hasItem(namedAs("Given Name")));
	}

	// Search Arguments Validation
	public void testSearchForNullNameThrowsException() {
		try {
			this.theTVDB.searchFor(null, LANGUAGE_EN);
			fail("Should have thrown an IllegalArgumentException for searching for null name");
		} catch (IllegalArgumentException e) {}
	}

	public void testSearchForNullLanguageThrowsException() {
		try {
			this.theTVDB.searchFor("Series Name", null);
			fail("Should have thrown an IllegalArgumentException for searching for null language");
		} catch (IllegalArgumentException e) {}
	}

	public void testSearchForUnavailableLanguageThrowsException() {
		try {
			this.theTVDB.searchFor("Series Name", UNAVAILABLE_LANGUAGE);
			fail("Should have thrown an IllegalArgumentException for unavailable language");
		} catch (IllegalArgumentException e) {}
	}

	// Fetch Series
	public void testFetchNonExistentSeriesThrowsException() {
		try {
			this.theTVDB.fetchSeries(123, LANGUAGE_EN);
			fail("Should have thrown an SeriesNotFoundException for nonexistent series");
		} catch (SeriesNotFoundException e) {}
	}

	public void testFetchExistentSeriesInEnglish() {
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");

		Series fetched = this.theTVDB.fetchSeries(123, LANGUAGE_EN);

		assertThat(fetched, hasId(123));
		assertThat(fetched, namedAs("Given Name"));
		assertThat(fetched, overviewedAs("An example of series"));
	}

	public void testFetchExistentSeriesFromDifferentLocale() {
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");
		this.theTVDB.createSeries(LANGUAGE_PT, "id : 123", "name : Given Name", "overview : Um exemplo de serie");

		Series fetched = this.theTVDB.fetchSeries(123, LANGUAGE_PT);

		assertThat(fetched, hasId(123));
		assertThat(fetched, namedAs("Given Name"));
		assertThat(fetched, overviewedAs("Um exemplo de serie"));
	}

	public void testFetchExistentSeriesFromLocaleWhereItDoesNotExistReturnsEnglishVersion() {
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");
		this.theTVDB.createSeries(LANGUAGE_PT, "id : 123", "name : Given Name", "overview : Um exemplo de serie");

		Series fetched = this.theTVDB.fetchSeries(123, LANGUAGE_ES);

		assertThat(fetched, hasId(123));
		assertThat(fetched, namedAs("Given Name"));
		assertThat(fetched, overviewedAs("An example of series"));
	}

	// Fetch Series Arguments Validation
	public void testFetchNullLanguageThrowsException() {
		try {
			this.theTVDB.fetchSeries(123, null);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	public void testFetchUnavailableLanguageThrowsException() {
		try {
			this.theTVDB.fetchSeries(123, UNAVAILABLE_LANGUAGE);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	// Fetch All Series
	public void testFetchAllWithNoSeriesIdsReturnsAnEmptyList() {
		assertTrue(this.theTVDB.fetchAllSeries(new ArrayList<Integer>(), LANGUAGE_EN).isEmpty());
	}

	public void testFetchAllWithNonExistentSeriesThrowsException() {
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name");
		try {
			this.theTVDB.fetchAllSeries(Arrays.asList(123, 321), LANGUAGE_EN);
			fail("Should have thrown an SeriesNotFoundException for nonexistent series");
		} catch (SeriesNotFoundException e) {}
	}

	public void testFetchAllWithExistentSeriesInEnglish() {
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 321", "name : Another Name", "overview : Another example of series");

		Collection<Series> allFetchedSeries = this.theTVDB.fetchAllSeries(Arrays.asList(123, 321), LANGUAGE_EN);
		assertThat(allFetchedSeries.size(), equalTo(2));

		assertThat(allFetchedSeries, hasItem(both(hasId(123))
				.and(namedAs("Given Name"))
				.and(overviewedAs("An example of series"))));

		assertThat(allFetchedSeries, hasItem(both(hasId(321))
				.and(namedAs("Another Name"))
				.and(overviewedAs("Another example of series"))));
	}

	public void testFetchAllWithExistentSeriesFromDifferentLocale() {
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");
		this.theTVDB.createSeries(LANGUAGE_PT, "id : 123", "name : Given Name", "overview : Um exemplo de serie");

		Collection<Series> allFetchedSeries = this.theTVDB.fetchAllSeries(Arrays.asList(123), LANGUAGE_PT);
		assertThat(allFetchedSeries.size(), equalTo(1));

		assertThat(allFetchedSeries, hasItem(both(hasId(123))
				.and(namedAs("Given Name"))
				.and(overviewedAs("Um exemplo de serie"))));
	}

	public void testFetchAllWithExistentSeriesFromLocaleWhereItDoesNotExistReturnsEnglishVersion() {
		this.theTVDB.createSeries(LANGUAGE_EN, "id : 123", "name : Given Name", "overview : An example of series");
		this.theTVDB.createSeries(LANGUAGE_PT, "id : 123", "name : Given Name", "overview : Um exemplo de serie");

		Collection<Series> allFetchedSeries = this.theTVDB.fetchAllSeries(Arrays.asList(123), LANGUAGE_ES);
		assertThat(allFetchedSeries.size(), equalTo(1));

		assertThat(allFetchedSeries, hasItem(both(hasId(123))
				.and(namedAs("Given Name"))
				.and(overviewedAs("An example of series"))));
	}

	// Fetch All Series Arguments Validation
	public void testFetchAllWithNullListOfSeriesThrowsException() {
		try {
			this.theTVDB.fetchAllSeries(null, LANGUAGE_EN);
			fail("Should have thrown an IllegalArgumentException for null list");
		} catch (IllegalArgumentException e) {}
	}

	public void testFetchAllWithNullLanguageThrowsException() {
		try {
			this.theTVDB.fetchAllSeries(Arrays.asList(123), null);
			fail("Should have thrown an IllegalArgumentException for null language abbreviation");
		} catch (IllegalArgumentException e) {}
	}

	public void testFetchAllUnavailableLanguageThrowsException() {
		try {
			this.theTVDB.fetchAllSeries(Arrays.asList(123), UNAVAILABLE_LANGUAGE);
			fail("Should have thrown an IllegalArgumentException for unavailable language");
		} catch (IllegalArgumentException e) {}
	}

	public void testFetchAllWithNullSeriesIdThrowsException() {
		try {
			this.theTVDB.fetchAllSeries(Arrays.asList((Integer) null), LANGUAGE_EN);
			fail("Should have thrown an IllegalArgumentException for null series id");
		} catch (IllegalArgumentException e) {}
	}

	// TODO
	// Deal with Episodes
}
