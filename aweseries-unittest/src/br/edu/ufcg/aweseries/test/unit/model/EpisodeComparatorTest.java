/*
 *   EpisodeComparator.java
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

import java.util.Comparator;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.EpisodeComparator;

public class EpisodeComparatorTest {

	//Episode mock------------------------------------------------------------------------------------------------------

	private Episode newEpisodeWith(int number) {
		Episode e = Mockito.mock(Episode.class);

		Mockito.when(e.number()).thenReturn(number);

		return e;
	}

	private Episode newEpisodeWith(int number, int seasonNumber, Date airdate) {
		Episode e = Mockito.mock(Episode.class);

		Mockito.when(e.number()).thenReturn(number);
		Mockito.when(e.seasonNumber()).thenReturn(seasonNumber);
		Mockito.when(e.airDate()).thenReturn(airdate);

		return e;
	}

	//ByNumber----------------------------------------------------------------------------------------------------------

	@Test(expected=IllegalArgumentException.class)
	public void testComparingByNumberWhenEpisode1IsNull() {
		Comparator<Episode> c = EpisodeComparator.byNumber();
		c.compare(null, this.newEpisodeWith(1));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testComparingByNumberWhenEpisode2IsNull() {
		Comparator<Episode> c = EpisodeComparator.byNumber();
		c.compare(this.newEpisodeWith(1), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testComparingByNumberWhenBothEpisodesAreNull() {
		Comparator<Episode> c = EpisodeComparator.byNumber();
		c.compare(null, null);
	}

	@Test
	public void testComparingByNumber() {
		Comparator<Episode> c = EpisodeComparator.byNumber();

		Episode e1 = this.newEpisodeWith(1);
		Episode e2 = this.newEpisodeWith(2);

		Assert.assertEquals(-1, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(1, c.compare(e2, e1));
	}

	//ByAirdateThenBySeasonThenByNumber---------------------------------------------------------------------------------

	@Test(expected=IllegalArgumentException.class)
	public void testComparingByAirdateThenBySeasonThenByNumberWhenEpisode1IsNull() {
		Comparator<Episode> c = EpisodeComparator.byAirdateThenBySeasonThenByNumber();
		c.compare(null, this.newEpisodeWith(1));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testComparingByAirdateThenBySeasonThenByNumberWhenEpisode2IsNull() {
		Comparator<Episode> c = EpisodeComparator.byAirdateThenBySeasonThenByNumber();
		c.compare(this.newEpisodeWith(1), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testComparingByAirdateThenBySeasonThenByNumberWhenBothEpisodesAreNull() {
		Comparator<Episode> c = EpisodeComparator.byAirdateThenBySeasonThenByNumber();
		c.compare(null, null);
	}

	@Test
	public void testByAirdateThenBySeasonThenByNumber() {
		Comparator<Episode> c = EpisodeComparator.byAirdateThenBySeasonThenByNumber();

		Episode e1 = this.newEpisodeWith(1, 1, new Date(1L));
		Episode e2 = this.newEpisodeWith(1, 1, new Date(2L));

		Assert.assertEquals(-1, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(1, c.compare(e2, e1));

		e1 = this.newEpisodeWith(1, 1, new Date(1L));
		e2 = this.newEpisodeWith(1, 2, new Date(1L));

		Assert.assertEquals(-1, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(1, c.compare(e2, e1));

		e1 = this.newEpisodeWith(1, 1, new Date(1L));
		e2 = this.newEpisodeWith(2, 1, new Date(1L));

		Assert.assertEquals(-1, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(1, c.compare(e2, e1));

		e1 = this.newEpisodeWith(1, 1, new Date(1L));
		e2 = this.newEpisodeWith(1, 1, new Date(1L));

		Assert.assertEquals(0, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(0, c.compare(e2, e1));
	}

	//ReversedByAirdateThenBySeasonThenByNumber-------------------------------------------------------------------------

	@Test(expected=IllegalArgumentException.class)
	public void testComparingReversedByAirdateThenBySeasonThenByNumberWhenEpisode1IsNull() {
		Comparator<Episode> c = EpisodeComparator.reversedByAirdateThenBySeasonThenByNumber();
		c.compare(null, this.newEpisodeWith(1));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testComparingReversedByAirdateThenBySeasonThenByNumberWhenEpisode2IsNull() {
		Comparator<Episode> c = EpisodeComparator.reversedByAirdateThenBySeasonThenByNumber();
		c.compare(this.newEpisodeWith(1), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testComparingReversedByAirdateThenBySeasonThenByNumberWhenBothEpisodesAreNull() {
		Comparator<Episode> c = EpisodeComparator.reversedByAirdateThenBySeasonThenByNumber();
		c.compare(null, null);
	}

	@Test
	public void testComparingReversedByAirdateThenBySeasonThenByNumber() {
		Comparator<Episode> c = EpisodeComparator.reversedByAirdateThenBySeasonThenByNumber();

		Episode e1 = this.newEpisodeWith(1, 1, new Date(1L));
		Episode e2 = this.newEpisodeWith(1, 1, new Date(2L));

		Assert.assertEquals(1, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(-1, c.compare(e2, e1));

		e1 = this.newEpisodeWith(1, 1, new Date(1L));
		e2 = this.newEpisodeWith(1, 2, new Date(1L));

		Assert.assertEquals(1, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(-1, c.compare(e2, e1));

		e1 = this.newEpisodeWith(1, 1, new Date(1L));
		e2 = this.newEpisodeWith(2, 1, new Date(1L));

		Assert.assertEquals(1, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(-1, c.compare(e2, e1));

		e1 = this.newEpisodeWith(1, 1, new Date(1L));
		e2 = this.newEpisodeWith(1, 1, new Date(1L));

		Assert.assertEquals(0, c.compare(e1, e2));
		Assert.assertEquals(0, c.compare(e1, e1));
		Assert.assertEquals(0, c.compare(e2, e1));
	}
}
