/*
 *   SeriesTest.java
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


package br.edu.ufcg.aweseries.test.unit.model;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.edu.ufcg.aweseries.model.Series;

public class SeriesTest {
	private Series series1;
	private Series series2;
	private Series series3;
	private Series series4;

	@Before
	public final void setUp() {
		this.series1 = new Series.Builder().withId(1)
		.withName("series 1")
		.withStatus("status 1")
		.withAirDay("airs day 1")
		.withAirTime("airs time 1")
		.withAirDate("first aired 1")
		.withRuntime("runtime 1")
		.withNetwork("network 1")
		.withOverview("overview 1")
		.withGenres("genres 1")
		.withActors("actors 1")
		.build();

		this.series2 = new Series.Builder().withId(1)
				.withName("series 2")
				.withStatus("status 2")
				.withAirDay("airs day 2")
				.withAirTime("airs time 2")
				.withAirDate("first aired 2")
				.withRuntime("runtime 2")
				.withNetwork("network 2")
				.withOverview("overview 2")
				.withGenres("genres 2")
				.withActors("actors 2")
				.build();
		
		this.series3 = new Series.Builder().withId(1)
				.withName("series 1")
				.withStatus("status 1")
				.withAirDay("airs day 1")
				.withAirTime("airs time 1")
				.withAirDate("first aired 1")
				.withRuntime("runtime 1")
				.withNetwork("network 1")
				.withOverview("overview 1")
				.withGenres("genres 1")
				.withActors("actors 1")
				.build();
		
		this.series4 = new Series.Builder().withId(4)
				.withName("series 4")
				.withStatus("status 4")
				.withAirDay("airs day 4")
				.withAirTime("airs time 4")
				.withAirDate("first aired 4")
				.withRuntime("runtime 4")
				.withNetwork("network 4")
				.withOverview("overview 4")
				.withGenres("genres 4")
				.withActors("actors 4")
				.build();
	}

//	@Test(expected = IllegalArgumentException.class)
//	public final void testSeriesWithNullName() {
//		new Series.Builder().withId(1).withName(null);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public final void testSeriesWithBlankName() {
//		new Series.Builder().withId(1).withName("  ");
//	}

	@Test
	public final void testSeries() {
		Assert.assertNotNull(this.series1.id());
		Assert.assertNotNull(this.series1.name());
	}

	@Test
	public final void testGetId() {
		Assert.assertEquals(1, this.series1.id());
	}

	@Test
	public final void testGetName() {
		Assert.assertEquals("series 1", this.series1.name());
	}

	@Test
	public final void testGetStatus() {
		Assert.assertEquals("status 1", this.series1.status());
	}

	@Test
	public final void testGetAirsDay() {
		Assert.assertEquals("airs day 1", this.series1.airDay());
	}

	@Test
	public final void testGetAirsTime() {
		Assert.assertEquals("airs time 1", this.series1.airTime());
	}

	@Test
	public final void testGetFirstAired() {
		Assert.assertEquals("first aired 1", this.series1.airDate());
	}

	@Test
	public final void testGetRuntime() {
		Assert.assertEquals("runtime 1", this.series1.runtime());
	}

	@Test
	public final void testGetNetwork() {
		Assert.assertEquals("network 1", this.series1.network());
	}

	@Test
	public final void testGetOverview() {
		Assert.assertEquals("overview 1", this.series1.overview());
	}

	@Test
	public final void testGetGenres() {
		Assert.assertEquals("genres 1", this.series1.genres());
	}

	@Test
	public final void testGetActors() {
		Assert.assertEquals("actors 1", this.series1.actors());
	}

	// TODO This test should be migrated to work with the builder
	@Ignore
	@Test
	public final void testGetPoster() {
		Assert.assertNotNull(this.series1.poster());
	}

	@Test
	public final void testGetSeasons() {
		Assert.assertNotNull(this.series1.seasons());
	}

	@Test
	public final void testEqualsObject() {
		Assert.assertTrue("series1 and series2 have the same id",
				this.series1.equals(this.series2));
		Assert.assertTrue("series2 and series3 have the same id",
				this.series2.equals(this.series3));
		Assert.assertFalse("series3 and series4 does not have the same id",
				this.series3.equals(this.series4));

		for (int i = 1; i <= 1000; i++) {
			Assert.assertTrue("equals should be consistently reflexive",
					this.series1.equals(this.series1));
			Assert.assertTrue("equals should be consistently symmetric",
					this.series2.equals(this.series1));
			Assert.assertTrue("equals should be consistently transitive",
					this.series1.equals(this.series3));
			Assert.assertFalse(
					"equals should consistently return false if the expected object is null",
					this.series1.equals(null));
		}
	}

	@Test
	public final void testHashCode() {
		final int hashCode = this.series1.hashCode();
		for (int i = 1; i <= 1000; i++) {
			Assert.assertEquals(
					"hashCode should consistently return the same value for the same object",
					hashCode, this.series1.hashCode());
			Assert.assertEquals(
					"hashCode should consistently return the same value for equal objects",
					this.series1.hashCode(), this.series2.hashCode());
		}
	}

//	@Test
//	public final void testToString() {
//		Assert.assertEquals(this.series1.name(), this.series1.toString());
//		Assert.assertEquals(this.series2.name(), this.series2.toString());
//		Assert.assertEquals(this.series3.name(), this.series3.toString());
//		Assert.assertEquals(this.series4.name(), this.series4.toString());
//	}
}
