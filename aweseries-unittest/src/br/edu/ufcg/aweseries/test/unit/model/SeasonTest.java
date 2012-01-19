/*
 *   SeasonTest.java
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.SeasonListener;

public class SeasonTest {

	private Season season;
	private Episode episode2;
	private Episode episode1;
	private Episode episode3;
	private Episode episode4;

	//Auxiliar----------------------------------------------------------------------------------------------------------

	private Episode mockEpisode(int id, int seriesId, int number, int seasonNumber) {
		Episode episode = Mockito.mock(Episode.class);

		Mockito.when(episode.id()).thenReturn(id);
		Mockito.when(episode.seriesId()).thenReturn(seriesId);
		Mockito.when(episode.number()).thenReturn(number);
		Mockito.when(episode.seasonNumber()).thenReturn(seasonNumber);

		return episode;
	}

	private void markAsNotSeen(Episode... episodes) {
		for (Episode episode : episodes) {
			Mockito.when(episode.wasSeen()).thenReturn(false);
		}
	}

	private void markAsSeen(Episode... episodes) {
		for (Episode episode : episodes) {
			Mockito.when(episode.wasSeen()).thenReturn(true);
		}
	}

	private void callOnMarkAsSeenFor(Season season, Episode... episodes) {
		for (Episode episode : episodes) {
			season.onMarkAsSeen(episode);
		}
	}

	private void callOnMarkAsNotSeenFor(Season season, Episode... episodes) {
		for (Episode episode : episodes) {
			season.onMarkAsNotSeen(episode);
		}
	}

	public SeasonListener mockListener() {
		return Mockito.mock(SeasonListener.class);
	}

	//SetUp-------------------------------------------------------------------------------------------------------------

	@Before
	public void setUp() throws Exception {
		this.season = new Season(1, 1);

		this.episode1 = this.mockEpisode(1, 1, 1, 1);
		this.episode2 = this.mockEpisode(2, 1, 2, 1);
		this.episode3 = this.mockEpisode(3, 1, 3, 1);
		this.episode4 = this.mockEpisode(4, 1, 4, 1);

		this.season.include(this.episode1);
		this.season.include(this.episode2);
		this.season.include(this.episode3);
		this.season.include(this.episode4);
	}

	//Construction------------------------------------------------------------------------------------------------------

	@Test(expected=IllegalArgumentException.class)
	public void testConstructASeasonWithNegativeSeriesId() {
		new Season(-1, 0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructASeasonWithNegativeNumber() {
		new Season(0, -1);
	}

	@Test
	public void testConstructASeason() {
		Season s = new Season(0, 0);

		Assert.assertEquals(0, s.seriesId());
		Assert.assertEquals(0, s.number());
		Assert.assertTrue(s.episodes().isEmpty());
		Assert.assertEquals(0, s.numberOfEpisodes());
		Assert.assertTrue(s.wasSeen());
		Assert.assertEquals(0, s.numberOfSeenEpisodes());
		Assert.assertNull(s.nextEpisodeToSee());
	}

	//Queries-----------------------------------------------------------------------------------------------------------

	@Test(expected=IllegalArgumentException.class)
	public void testGettingEpisodesByANullSpecificationThrowIllegalArgumentException() {
		new Season(1,1).episodesBy(null);
	}

	//Inclusion---------------------------------------------------------------------------------------------------------

	@Test(expected=IllegalArgumentException.class)
	public final void testIncludeNullEpisode() {
		new Season(1, 1).include(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public final void testIncludeEpisodeWithAnotherSeriesId() {
		new Season(1, 1).include(this.mockEpisode(1, 2, 1, 1));
	}

	@Test(expected=IllegalArgumentException.class)
	public final void testIncludeEpisodeWithAnotherSeasonNumber() {
		new Season(1, 1).include(this.mockEpisode(1, 1, 1, 2));
	}

	@Test(expected=IllegalArgumentException.class)
	public final void testIncludeAlreadyExistentEpisode() {
		Season s = new Season(1, 1);
		Episode e = this.mockEpisode(1, 1, 1, 1);
		s.include(e);
		s.include(e);
	}

	@Test
	public final void testIncludeValidEpisodes() {
		Season s1 = new Season(1, 1);

		Episode e1 = this.mockEpisode(1, 1, 1, 1);
		Episode e2 = this.mockEpisode(2, 1, 2, 1);
		this.markAsNotSeen(e1, e2);

		SeasonListener l1 = this.mockListener();
		SeasonListener l2 = this.mockListener();

		s1.register(l1);
		s1.register(l2);

		s1.include(e2);

		Assert.assertTrue(s1.includes(e2));
		Assert.assertEquals(1, s1.numberOfEpisodes());
		Assert.assertFalse(s1.wasSeen());
		Assert.assertEquals(0, s1.numberOfSeenEpisodes());
		Assert.assertEquals(e2, s1.nextEpisodeToSee());
		Mockito.verify(l1, Mockito.times(1)).onChangeNextEpisodeToSee(s1);
		Mockito.verify(l2, Mockito.times(1)).onChangeNextEpisodeToSee(s1);

		s1.include(e1);

		Assert.assertTrue(s1.includes(e1));
		Assert.assertEquals(2, s1.numberOfEpisodes());
		Assert.assertFalse(s1.wasSeen());
		Assert.assertEquals(0, s1.numberOfSeenEpisodes());
		Assert.assertEquals(e1, s1.nextEpisodeToSee());
		Mockito.verify(l1, Mockito.times(2)).onChangeNextEpisodeToSee(s1);
		Mockito.verify(l2, Mockito.times(2)).onChangeNextEpisodeToSee(s1);

		Season s2 = new Season(1, 2);

		Episode e3 = this.mockEpisode(1, 1, 1, 2);
		Episode e4 = this.mockEpisode(2, 1, 2, 2);
		this.markAsSeen(e3, e4);

		SeasonListener l3 = this.mockListener();
		SeasonListener l4 = this.mockListener();

		s2.register(l3);
		s2.register(l4);

		s2.include(e3);

		Assert.assertTrue(s2.includes(e3));
		Assert.assertTrue(s2.wasSeen());
		Assert.assertNull(s2.nextEpisodeToSee());
		Mockito.verify(l3, Mockito.times(0)).onChangeNextEpisodeToSee(s2);
		Mockito.verify(l4, Mockito.times(0)).onChangeNextEpisodeToSee(s2);

		s2.include(e4);

		Assert.assertTrue(s2.includes(e3));
		Assert.assertTrue(s2.includes(e4));
		Assert.assertTrue(s2.wasSeen());
		Assert.assertNull(s2.nextEpisodeToSee());
		Mockito.verify(l3, Mockito.times(0)).onChangeNextEpisodeToSee(s2);
		Mockito.verify(l4, Mockito.times(0)).onChangeNextEpisodeToSee(s2);
	}

	//Mark--------------------------------------------------------------------------------------------------------------

	@Test
	public final void testMarkAsSeen() {
		Season s = new Season(1, 1);

		Episode e1 = this.mockEpisode(1, 1, 1, 1);
		Episode e2  = this.mockEpisode(2, 1, 2, 1);

		s.include(e1);
		s.include(e2);

		SeasonListener l1 = this.mockListener();
		SeasonListener l2 = this.mockListener();

		s.register(l1);
		s.register(l2);

		s.markAsSeen();
		this.markAsSeen(e1, e2);
		this.callOnMarkAsSeenFor(s, e1, e2);

		Mockito.verify(e1).markAsSeen();
		Mockito.verify(e2).markAsSeen();

		Mockito.verify(l1).onMarkAsSeen(s);
		Mockito.verify(l2).onMarkAsSeen(s);

		Assert.assertTrue(s.wasSeen());
		Assert.assertNull(s.nextEpisodeToSee());
	}

	@Test
	public final void testMarkAsNotSeen() {
		Season s = new Season(1, 1);

		Episode e1 = this.mockEpisode(1, 1, 1, 1);
		Episode e2  = this.mockEpisode(2, 1, 2, 1);
		this.markAsSeen(e1, e2);

		s.include(e1);
		s.include(e2);

		SeasonListener l1 = this.mockListener();
		SeasonListener l2 = this.mockListener();

		s.register(l1);
		s.register(l2);

		s.markAsNotSeen();
		this.markAsNotSeen(e1, e2);
		this.callOnMarkAsNotSeenFor(s, e1, e2);

		Mockito.verify(e1).markAsNotSeen();
		Mockito.verify(e2).markAsNotSeen();

		Mockito.verify(l1).onMarkAsNotSeen(s);
		Mockito.verify(l2).onMarkAsNotSeen(s);

		Assert.assertFalse(s.wasSeen());
		Assert.assertEquals(e1, s.nextEpisodeToSee());
	}

	//Merge-------------------------------------------------------------------------------------------------------------

	@Test(expected = IllegalArgumentException.class)
	public final void testMergeWithNull() {
		new Season(1, 1).mergeWith(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testMergeWithOtherHavingADifferentSeriesId() {
		new Season(1, 1).mergeWith(new Season(2, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testMergeWithOtherHavingADifferentNumber() {
		new Season(1, 1).mergeWith(new Season(1, 2));
	}

	@Test
	public final void testMergeWith() {
		Season s1 = new Season(1, 1);
		Season s2 = new Season(1, 1);

		Episode e1 = this.mockEpisode(1, 1, 1, 1);
		Episode e2 = this.mockEpisode(2, 1, 2, 1);

		s1.include(e1);
		s2.include(e2);

		SeasonListener l1 = this.mockListener();
		SeasonListener l2 = this.mockListener();

		s1.register(l1);
		s2.register(l2);

		s1.mergeWith(s2);
		Assert.assertTrue(s1.includes(e1));
		Assert.assertTrue(s1.includes(e2));
		Assert.assertTrue(s2.includes(e2));
		Assert.assertFalse(s2.includes(e1));

		Mockito.verify(l1).onMerge(s1);

		s2.mergeWith(s1);
		Assert.assertTrue(s1.includes(e1));
		Assert.assertTrue(s1.includes(e2));
		Assert.assertTrue(s2.includes(e2));
		Assert.assertTrue(s2.includes(e1));

		Mockito.verify(l2).onMerge(s2);
	}

	//EpisodeListener---------------------------------------------------------------------------------------------------

	@Test
	public final void testOnMarkAsSeen() {
		Season s = new Season(1, 1);

		Episode e1 = this.mockEpisode(1, 1, 1, 1);
		Episode e2 = this.mockEpisode(2, 1, 2, 1);
		this.markAsNotSeen(e1, e2);

		s.include(e1);
		s.include(e2);

		SeasonListener l1 = this.mockListener();
		SeasonListener l2 = this.mockListener();

		s.register(l1);
		s.register(l2);

		Assert.assertFalse(s.wasSeen());
		Assert.assertEquals(e1, s.nextEpisodeToSee());

		this.markAsSeen(e1);
		this.callOnMarkAsSeenFor(s, e1);

		Assert.assertFalse(s.wasSeen());
		Assert.assertEquals(e2, s.nextEpisodeToSee());

		Mockito.verify(l1, Mockito.times(0)).onMarkAsSeen(s);
		Mockito.verify(l2, Mockito.times(0)).onMarkAsSeen(s);
		Mockito.verify(l1, Mockito.times(1)).onChangeNextEpisodeToSee(s);
		Mockito.verify(l2, Mockito.times(1)).onChangeNextEpisodeToSee(s);

		this.markAsSeen(e2);
		this.callOnMarkAsSeenFor(s, e2);

		Assert.assertTrue(s.wasSeen());
		Assert.assertNull(s.nextEpisodeToSee());

		Mockito.verify(l1, Mockito.times(1)).onMarkAsSeen(s);
		Mockito.verify(l2, Mockito.times(1)).onMarkAsSeen(s);
		Mockito.verify(l1, Mockito.times(2)).onChangeNextEpisodeToSee(s);
		Mockito.verify(l2, Mockito.times(2)).onChangeNextEpisodeToSee(s);
	}

	@Test
	public final void testOnMarkAsNotSeen() {
		Season s = new Season(1, 1);

		Episode e1 = this.mockEpisode(1, 1, 1, 1);
		Episode e2 = this.mockEpisode(2, 1, 2, 1);
		this.markAsSeen(e1, e2);

		s.include(e1);
		s.include(e2);

		SeasonListener l1 = this.mockListener();
		SeasonListener l2 = this.mockListener();

		s.register(l1);
		s.register(l2);

		Assert.assertTrue(s.wasSeen());
		Assert.assertNull(s.nextEpisodeToSee());

		this.markAsNotSeen(e1);
		this.callOnMarkAsNotSeenFor(s, e1);

		Assert.assertFalse(s.wasSeen());
		Assert.assertEquals(e1, s.nextEpisodeToSee());

		Mockito.verify(l1, Mockito.times(1)).onMarkAsNotSeen(s);
		Mockito.verify(l2, Mockito.times(1)).onMarkAsNotSeen(s);
		Mockito.verify(l1, Mockito.times(1)).onChangeNextEpisodeToSee(s);
		Mockito.verify(l2, Mockito.times(1)).onChangeNextEpisodeToSee(s);

		this.markAsNotSeen(e2);
		this.callOnMarkAsNotSeenFor(s, e2);

		Assert.assertFalse(s.wasSeen());
		Assert.assertEquals(e1, s.nextEpisodeToSee());

		Mockito.verify(l1, Mockito.times(1)).onMarkAsNotSeen(s);
		Mockito.verify(l2, Mockito.times(1)).onMarkAsNotSeen(s);
		Mockito.verify(l1, Mockito.times(1)).onChangeNextEpisodeToSee(s);
		Mockito.verify(l2, Mockito.times(1)).onChangeNextEpisodeToSee(s);
	}

	//Equals and HashCode-----------------------------------------------------------------------------------------------

	@Test
	public void testEquals() {
		Season s1 = new Season(1,1);
		Season s2 = new Season(1,1);
		Season s3 = new Season(1,1);
		Season s4 = new Season(1,2);
		Season s5 = new Season(2,1);

		//equals is consistent
		for (int i=1; i<=1000; i++) {

			//equals returns false to null objects
			Assert.assertFalse(s1.equals(null));

			//equals is reflexive
			Assert.assertEquals(s1, s1);

			//equals is symmetric
			Assert.assertEquals(s1, s2);
			Assert.assertEquals(s2, s1);

			//equals is transitive
			Assert.assertEquals(s1, s2);
			Assert.assertEquals(s2, s3);
			Assert.assertEquals(s1, s3);

			//seasons are equal if and only if they have the same seriesId and the same number
			Assert.assertFalse(s1.equals(s4));
			Assert.assertFalse(s1.equals(s5));
		}
	}

	@Test
	public void testHashCode() {
		Season s1 = new Season(1,1);
		Season s2 = new Season(1,1);

		//hashCode is consistent
		for (int i=1; i<=1000; i++) {

			//equal objects have the same hashCode
			Assert.assertEquals(s1.hashCode(), s2.hashCode());
		}
	}
}
