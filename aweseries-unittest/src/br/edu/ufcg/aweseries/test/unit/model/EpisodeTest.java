/*
 *   EpisodeTest.java
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

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.EpisodeListener;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

public class EpisodeTest {
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int SERIES_ID1 = 1;
    private static final int SERIES_ID2 = 2;
    private static final int NUMBER1 = 1;
    private static final int NUMBER2 = 2;
    private static final int SEASON_NUMBER1 = 1;
    private static final int SEASON_NUMBER2 = 2;
    private static final String NAME = "name";
    private static final Date AIR_DATE = new Date();
    private static final String OVERVIEW = "overview";
    private static final String DIRECTORS = "directors";
    private static final String WRITERS = "writers";
    private static final String GUEST_STARS = "guest stars";
    private static final String IMAGE_FILE_NAME = "poster";

    //Mock--------------------------------------------------------------------------------------------------------------

    private static EpisodeListener mockListener() {
        return Mockito.mock(EpisodeListener.class);
    }

    //Building----------------------------------------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void buildingEpisodeWithoutIdCausesIllegalArgumentException() {
        Episode.builder()
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingEpisodeWithInvalidIdCausesIllegalArgumentException() {
        Episode.builder()
            .withId(Episode.INVALID_ID)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingEpisodeWithoutSeriesIdCausesIllegalArgumentException() {
        Episode.builder()
            .withId(ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingEpisodeWithInvalidSeriesIdCausesIllegalArgumentException() {
        Episode.builder()
            .withId(ID1)
            .withSeriesId(Series.INVALID_ID)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingEpisodeWithoutNumberCausesIllegalArgumentException() {
        Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingEpisodeWithInvalidNumberCausesIllegalArgumentException() {
        Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(Episode.INVALID_NUMBER)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingEpisodeWithoutSeasonNumberCausesIllegalArgumentException() {
        Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingEpisodeWithInvalidSeasonNumberCausesIllegalArgumentException() {
        Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(Season.INVALID_NUMBER)
            .build();
    }

    @Test
    public void itCanBuildAnEpisodeWithOnlyRequiredFields() {
        Episode episode = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();

        Assert.assertEquals(ID1, episode.id());
        Assert.assertEquals(SERIES_ID1, episode.seriesId());
        Assert.assertEquals(NUMBER1, episode.number());
        Assert.assertEquals(SEASON_NUMBER1, episode.seasonNumber());

        Assert.assertNull(episode.name());
        Assert.assertNull(episode.airDate());
        Assert.assertNull(episode.overview());
        Assert.assertNull(episode.directors());
        Assert.assertNull(episode.directors());
        Assert.assertNull(episode.writers());
        Assert.assertNull(episode.guestStars());
        Assert.assertNull(episode.imageFileName());

        Assert.assertFalse(episode.wasSeen());
    }

    @Test
    public void itCanBuildAnEpisodeWithAllFields() {
        Episode e1 = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .withName(NAME)
            .withAirDate(AIR_DATE)
            .withOverview(OVERVIEW)
            .withDirectors(DIRECTORS)
            .withWriters(WRITERS)
            .withGuestStars(GUEST_STARS)
            .withImageFileName(IMAGE_FILE_NAME)
            .withSeenMark(true)
            .build();

        Assert.assertEquals(ID1, e1.id());
        Assert.assertEquals(SERIES_ID1, e1.seriesId());
        Assert.assertEquals(NUMBER1, e1.number());
        Assert.assertEquals(SEASON_NUMBER1, e1.seasonNumber());

        Assert.assertEquals(NAME, e1.name());
        Assert.assertEquals(AIR_DATE, e1.airDate());
        Assert.assertEquals(OVERVIEW, e1.overview());
        Assert.assertEquals(DIRECTORS, e1.directors());
        Assert.assertEquals(WRITERS, e1.writers());
        Assert.assertEquals(GUEST_STARS, e1.guestStars());
        Assert.assertEquals(IMAGE_FILE_NAME, e1.imageFileName());

        Assert.assertEquals(true, e1.wasSeen());
    }

    //SeenMark----------------------------------------------------------------------------------------------------------

    //TODO Continue refactoring from here
	@Test
	public void testMarkAsSeen() {
		Episode e = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1)
				.withSeenMark(false).build();

		EpisodeListener l1 = mockListener();
		EpisodeListener l2 = mockListener();

		e.register(l1);
		e.register(l2);

		for (int i = 1; i <= 1000; i++) {
			e.markAsSeen();
			Assert.assertTrue(e.wasSeen());
		}

		Mockito.verify(l1, Mockito.times(1)).onMarkAsSeen(e);
		Mockito.verify(l2, Mockito.times(1)).onMarkAsSeen(e);

		Mockito.verify(l1, Mockito.times(0)).onMarkAsNotSeen(e);
		Mockito.verify(l2, Mockito.times(0)).onMarkAsNotSeen(e);

		Mockito.verify(l1, Mockito.times(0)).onMerge(e);
		Mockito.verify(l2, Mockito.times(0)).onMerge(e);
	}

	@Test
	public void testMarkAsNotSeen() {
		Episode e = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1)
				.withSeenMark(true).build();

		EpisodeListener l1 = mockListener();
		EpisodeListener l2 = mockListener();

		e.register(l1);
		e.register(l2);

		for (int i = 1; i <= 1000; i++) {
			e.markAsNotSeen();
			Assert.assertFalse(e.wasSeen());
		}

		Mockito.verify(l1, Mockito.times(0)).onMarkAsSeen(e);
		Mockito.verify(l2, Mockito.times(0)).onMarkAsSeen(e);

		Mockito.verify(l1, Mockito.times(1)).onMarkAsNotSeen(e);
		Mockito.verify(l2, Mockito.times(1)).onMarkAsNotSeen(e);

		Mockito.verify(l1, Mockito.times(0)).onMerge(e);
		Mockito.verify(l2, Mockito.times(0)).onMerge(e);
	}

	// Merge-------------------------------------------------------------------------------------------------------------

	@Test(expected = IllegalArgumentException.class)
	public void testMergeWithNull() {
		Episode.builder().withId(ID1).withSeriesId(SERIES_ID1).withNumber(
				NUMBER1).withSeasonNumber(SEASON_NUMBER1).build().mergeWith(
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMergeWithOtherHavingNotSameId() {
		Episode e1 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		Episode e2 = Episode.builder().withId(ID2).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		e1.mergeWith(e2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMergeWithOtherHavingNotSameSeriesId() {
		Episode e1 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		Episode e2 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID2)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		e1.mergeWith(e2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMergeWithOtherHavingNotSameNumber() {
		Episode e1 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		Episode e2 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER2).withSeasonNumber(SEASON_NUMBER1).build();

		e1.mergeWith(e2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMergeWithOtherHavingNotSameSeasonNumber() {
		Episode e1 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		Episode e2 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER2).build();

		e1.mergeWith(e2);
	}

	@Test
	public void testMergeWith() {
		Episode e1 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1)
				.withSeenMark(false).build();

		Episode e2 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).withName(
						NAME).withAirDate(AIR_DATE).withOverview(OVERVIEW)
				.withDirectors(DIRECTORS).withWriters(WRITERS).withGuestStars(
						GUEST_STARS).withImageFileName(IMAGE_FILE_NAME).withSeenMark(
						true).build();

		EpisodeListener l1 = mockListener();
		EpisodeListener l2 = mockListener();

		e1.register(l1);
		e1.register(l2);

		for (int i = 1; i <= 1000; i++) {
			e1.mergeWith(e2);

			Assert.assertEquals(NAME, e1.name());
			Assert.assertEquals(AIR_DATE, e1.airDate());
			Assert.assertEquals(OVERVIEW, e1.overview());
			Assert.assertEquals(DIRECTORS, e1.directors());
			Assert.assertEquals(WRITERS, e1.writers());
			Assert.assertEquals(GUEST_STARS, e1.guestStars());
			Assert.assertEquals(IMAGE_FILE_NAME, e1.imageFileName());
			Assert.assertEquals(false, e1.wasSeen());
		}

		Mockito.verify(l1, Mockito.times(0)).onMarkAsSeen(e1);
		Mockito.verify(l2, Mockito.times(0)).onMarkAsSeen(e1);

		Mockito.verify(l1, Mockito.times(0)).onMarkAsNotSeen(e1);
		Mockito.verify(l2, Mockito.times(0)).onMarkAsNotSeen(e1);

		Mockito.verify(l1, Mockito.times(1000)).onMerge(e1);
		Mockito.verify(l2, Mockito.times(1000)).onMerge(e1);
	}

	// EpisodeListener---------------------------------------------------------------------------------------------------

	@Test(expected = IllegalArgumentException.class)
	public void testRegisterNullListener() {
		Episode.builder().withId(ID1).withSeriesId(SERIES_ID1).withNumber(
				NUMBER1).withSeasonNumber(SEASON_NUMBER1).build()
				.register(null);
	}

	@Test
	public void testRegisterListener() {
		Episode e = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		EpisodeListener l1 = mockListener();
		EpisodeListener l2 = mockListener();

		Assert.assertTrue(e.register(l1));
		Assert.assertTrue(e.register(l2));

		for (int i = 1; i <= 1000; i++) {
			Assert.assertFalse(e.register(l1));
			Assert.assertFalse(e.register(l2));
			Assert.assertTrue(e.register(mockListener()));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeregisterNullListener() {
		Episode.builder().withId(ID1).withSeriesId(SERIES_ID1).withNumber(
				NUMBER1).withSeasonNumber(SEASON_NUMBER1).build().deregister(
				null);
	}

	@Test
	public void testDeregisterListener() {
		Episode e = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		EpisodeListener l1 = mockListener();
		EpisodeListener l2 = mockListener();

		e.register(l1);
		e.register(l2);

		Assert.assertFalse(e.deregister(mockListener()));
		Assert.assertTrue(e.deregister(l1));
		Assert.assertTrue(e.deregister(l2));

		for (int i = 1; i <= 1000; i++) {
			Assert.assertFalse(e.deregister(l1));
			Assert.assertFalse(e.deregister(l2));
		}
	}

	// Equals and
	// hashCode-----------------------------------------------------------------------------------------------

	@Test
	public void testEquals() {
		Episode e1 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		Episode e2 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID2)
				.withNumber(NUMBER2).withSeasonNumber(SEASON_NUMBER2).build();

		Episode e3 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID2)
				.withNumber(NUMBER2).withSeasonNumber(SEASON_NUMBER2).build();

		Episode e4 = Episode.builder().withId(ID2).withSeriesId(SERIES_ID2)
				.withNumber(NUMBER2).withSeasonNumber(SEASON_NUMBER2).build();

		// equals is consistent
		for (int i = 1; i <= 1000; i++) {

			// equals returns false for null objects
			Assert.assertFalse(e1.equals(null));

			// equals is reflexive
			Assert.assertEquals(e1, e1);

			// equals is symmetric
			Assert.assertEquals(e1, e2);
			Assert.assertEquals(e2, e1);

			// equals is transitive
			Assert.assertEquals(e1, e2);
			Assert.assertEquals(e2, e3);
			Assert.assertEquals(e1, e3);

			// episodes are equal if and only if they have the same id
			Assert.assertFalse(e2.equals(e4));
		}
	}

	@Test
	public void testHashCode() {
		Episode e1 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID1)
				.withNumber(NUMBER1).withSeasonNumber(SEASON_NUMBER1).build();

		Episode e2 = Episode.builder().withId(ID1).withSeriesId(SERIES_ID2)
				.withNumber(NUMBER2).withSeasonNumber(SEASON_NUMBER2).build();

		Episode e3 = Episode.builder().withId(ID2).withSeriesId(SERIES_ID2)
				.withNumber(NUMBER2).withSeasonNumber(SEASON_NUMBER2).build();

		// hashCode is consistent
		for (int i = 1; i <= 1000; i++) {

			// equal objects have the same hashCode
			Assert.assertEquals(e1.hashCode(), e2.hashCode());

			// non equal episodes have distinct hashCodes
			Assert.assertTrue(e1.hashCode() != e3.hashCode());
			Assert.assertTrue(e2.hashCode() != e3.hashCode());
		}
	}
}
