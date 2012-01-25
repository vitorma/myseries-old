/*
 *   DefaultSeriesFactoryTest.java
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.TestCase;
import br.edu.ufcg.aweseries.model.Series;

public class DefaultSeriesFactoryTest extends TestCase {

	private DefaultSeriesFactory factory;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		this.factory = new DefaultSeriesFactory();
	}

	public void testNonExistentAttributeKeyThrowsIllegalArgumentException() {
		try {
			this.factory.createSeries("game : Sudoku");
			fail("Should have thrown an exception for nonexistent attribute key");
		} catch (IllegalArgumentException e) {}
	}

	public void testNotParameterizedCreationReturnsDefaultData() {
		Series defaultSeries = this.factory.createSeries();

		// Series ID should be randomized - assertThat(defaultSeries.getId(), equalTo("0"));
		assertThat(defaultSeries.name(), equalTo("Default Series"));
		assertThat(defaultSeries.status(), equalTo("Continuing"));
		assertThat(defaultSeries.airsDay(), equalTo("Monday"));
		assertThat(defaultSeries.airsTime(), equalTo("8:00 PM"));
		assertThat(defaultSeries.firstAired(), equalTo("1996-01-01"));
		assertThat(defaultSeries.runtime(), equalTo("60"));
		assertThat(defaultSeries.network(), equalTo("BBC"));
		assertThat(defaultSeries.overview(), equalTo("A default series that has been created"));
		assertThat(defaultSeries.genres(), equalTo("Action"));
		assertThat(defaultSeries.actors(), equalTo("Wile E. Coyote, Road Runner"));
		assertThat(defaultSeries.poster(), nullValue());
	}

	public void testSeriesIdIsRandomizedSoDifferentSeriesAreCreatedEachTime() {
		Series firstSeries = this.factory.createSeries();
		Series secondSeries = this.factory.createSeries();

		assertThat(firstSeries, not(equalTo(secondSeries)));
		assertThat(firstSeries.id(), not(equalTo(secondSeries.id())));
	}

	public void testWhenEqualSeriesIdsAreChoosenThenEqualSeriesAreCreated() {
		Series firstSeries = this.factory.createSeries("id : 0");
		Series secondSeries = this.factory.createSeries("id : 0");

		assertThat(firstSeries, equalTo(secondSeries));
		assertThat(firstSeries.id(), equalTo(secondSeries.id()));
	}

	public void testDefaultSeriesWithCustomId() {
		Series defaultSeries = this.factory.createSeries("id : 123");

		assertEquals(defaultSeries.id(), 123);
	}

	public void testDefaultSeriesWithCustomIdAndName() {
		Series defaultSeries = this.factory.createSeries("id : 123", "name : Series Name");

		assertEquals(defaultSeries.id(), 123);
		assertThat(defaultSeries.name(), equalTo("Series Name"));
	}

	public void testAllParameters() {
		int id = 123;
		String name = "Not Default Series";
		String status = "Ended";
		String airsOn = "";
		String airsAt = "";
		String firstAired = "1997-12-21";
		String runtime = "30";
		String network = "ABC";
		String overview = "A not default series to be used in this test.";
		String genres = "Drama";
		String actors = "Who Is He, Who Is She";

		Series defaultSeries = this.factory.createSeries("id : " + id,
				"name : " + name,
				"status : " + status,
				"airsOn : " + airsOn,
				"airsAt : " + airsAt,
				"firstAired : " + firstAired,
				"runtime : " + runtime,
				"network : " + network,
				"overview : " + overview,
				"genres : " + genres,
				"actors : " + actors);

		assertEquals(defaultSeries.id(), id);
		assertThat(defaultSeries.name(), equalTo(name));
		assertThat(defaultSeries.status(), equalTo(status));
		assertThat(defaultSeries.airsDay(), equalTo(airsOn));
		assertThat(defaultSeries.airsTime(), equalTo(airsAt));
		assertThat(defaultSeries.firstAired(), equalTo(firstAired));
		assertThat(defaultSeries.runtime(), equalTo(runtime));
		assertThat(defaultSeries.network(), equalTo(network));
		assertThat(defaultSeries.overview(), equalTo(overview));
		assertThat(defaultSeries.genres(), equalTo(genres));
		assertThat(defaultSeries.actors(), equalTo(actors));
		assertThat(defaultSeries.poster(), nullValue());
	}
}
