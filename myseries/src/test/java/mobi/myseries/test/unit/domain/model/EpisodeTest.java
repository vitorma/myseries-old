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

package mobi.myseries.test.unit.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.domain.model.Episode;

import org.junit.Test;

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
    private static final String IMAGE_FILE_NAME = "image file name";

    /* Build */

    @Test(expected = IllegalArgumentException.class)
    public void buildingAnEpisodeWithoutIdCausesIllegalArgumentException() {
        Episode.builder()
        .withSeriesId(SERIES_ID1)
        .withNumber(NUMBER1)
        .withSeasonNumber(SEASON_NUMBER1)
        .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingAnEpisodeWithInvalidIdCausesIllegalArgumentException() {
        Episode.builder()
        .withId(Invalid.EPISODE_ID)
        .withSeriesId(SERIES_ID1)
        .withNumber(NUMBER1)
        .withSeasonNumber(SEASON_NUMBER1)
        .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingAnEpisodeWithoutSeriesIdCausesIllegalArgumentException() {
        Episode.builder()
        .withId(ID1)
        .withNumber(NUMBER1)
        .withSeasonNumber(SEASON_NUMBER1)
        .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingAnEpisodeWithInvalidSeriesIdCausesIllegalArgumentException() {
        Episode.builder()
        .withId(ID1)
        .withSeriesId(Invalid.SERIES_ID)
        .withNumber(NUMBER1)
        .withSeasonNumber(SEASON_NUMBER1)
        .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingAnEpisodeWithoutNumberCausesIllegalArgumentException() {
        Episode.builder()
        .withId(ID1)
        .withSeriesId(SERIES_ID1)
        .withSeasonNumber(SEASON_NUMBER1)
        .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingAnEpisodeWithInvalidNumberCausesIllegalArgumentException() {
        Episode.builder()
        .withId(ID1)
        .withSeriesId(SERIES_ID1)
        .withNumber(Invalid.EPISODE_NUMBER)
        .withSeasonNumber(SEASON_NUMBER1)
        .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingAnEpisodeWithoutSeasonNumberCausesIllegalArgumentException() {
        Episode.builder()
        .withId(ID1)
        .withSeriesId(SERIES_ID1)
        .withNumber(NUMBER1)
        .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildingAnEpisodeWithInvalidSeasonNumberCausesIllegalArgumentException() {
        Episode.builder()
        .withId(ID1)
        .withSeriesId(SERIES_ID1)
        .withNumber(NUMBER1)
        .withSeasonNumber(Invalid.SEASON_NUMBER)
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

        assertEquals(ID1, episode.id());
        assertEquals(SERIES_ID1, episode.seriesId());
        assertEquals(NUMBER1, episode.number());
        assertEquals(SEASON_NUMBER1, episode.seasonNumber());

        assertEquals(episode.title(), "");
        assertNull(episode.airDate());
        assertEquals(episode.overview(), "");
        assertEquals(episode.directors(), "");
        assertEquals(episode.directors(), "");
        assertEquals(episode.writers(), "");
        assertEquals(episode.guestStars(), "");
        assertEquals(episode.screenUrl(), "");

        assertFalse(episode.watched());
    }

    @Test
    public void itCanBuildAnEpisodeWithAllFields() {
        Episode e1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .withTitle(NAME)
                .withAirDate(AIR_DATE)
                .withOverview(OVERVIEW)
                .withDirectors(DIRECTORS)
                .withWriters(WRITERS)
                .withGuestStars(GUEST_STARS)
                .withScreenUrl(IMAGE_FILE_NAME)
                .withWatchMark(true)
                .build();

        assertEquals(ID1, e1.id());
        assertEquals(SERIES_ID1, e1.seriesId());
        assertEquals(NUMBER1, e1.number());
        assertEquals(SEASON_NUMBER1, e1.seasonNumber());

        assertEquals(NAME, e1.title());
        assertEquals(AIR_DATE, e1.airDate());
        assertEquals(OVERVIEW, e1.overview());
        assertEquals(DIRECTORS, e1.directors());
        assertEquals(WRITERS, e1.writers());
        assertEquals(GUEST_STARS, e1.guestStars());
        assertEquals(IMAGE_FILE_NAME, e1.screenUrl());

        assertEquals(true, e1.watched());
    }

    /* SeenMark */

    @Test
    public void markingAnEpisodeAsSeenAssignsTrueToItsSeenMarkField() {
        Episode episode = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .withWatchMark(false)
                .build();

        assertFalse(episode.watched());

        episode.markAsWatched();

        assertTrue(episode.watched());
    }

    @Test
    public void markingAnEpisodeAsNotSeenAssignsFalseToItsSeenMarkField() {
        Episode episode = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .withWatchMark(true)
                .build();

        assertTrue(episode.watched());

        episode.markAsUnwatched();

        assertFalse(episode.watched());
    }

    /* Merge */

    @Test(expected = IllegalArgumentException.class)
    public void mergingAnEpisodeWithANullOtherCausesIllegalArgumentException() {
        Episode.builder()
        .withId(ID1)
        .withSeriesId(SERIES_ID1)
        .withNumber(NUMBER1)
        .withSeasonNumber(SEASON_NUMBER1)
        .build()
        .mergeWith(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingAnEpisodeWithAnotherHavingADifferentNumberCausesIllegalArgumentException() {
        Episode episode1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode episode2 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER2)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        episode1.mergeWith(episode2);
    }

    public void mergingAnEpisodeWithAnotherHavingADifferentId() {
        Episode episode1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode episode2 = Episode.builder()
                .withId(ID2)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        episode1.mergeWith(episode2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingAnEpisodeWithAnotherHavingADifferentSeriesIdCausesIllegalArgumentException() {
        Episode episode1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode episode2 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID2)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        episode1.mergeWith(episode2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingAnEpisodeWithAnotherHavingADifferentSeasonNumberCausesIllegalArgumentException() {
        Episode episode1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode episode2 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER2)
                .build();

        episode1.mergeWith(episode2);

        assertEquals(episode2.number(), episode1.number());
        assertEquals(episode2.title(), episode1.title());
        assertEquals(episode2.airDate(), episode1.airDate());
        assertEquals(episode2.overview(), episode1.overview());
        assertEquals(episode2.directors(), episode1.directors());
        assertEquals(episode2.writers(), episode1.writers());
        assertEquals(episode2.guestStars(), episode1.guestStars());
        assertEquals(episode2.screenUrl(), episode1.screenUrl());
    }

    @Test
    public void mergingAnEpisodeWithAnotherMakesItsOptionalFieldsEqualsToThoseOfTheOtherOne() {
        Episode episode1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode episode2 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .withTitle(NAME)
                .withAirDate(AIR_DATE)
                .withOverview(OVERVIEW)
                .withDirectors(DIRECTORS)
                .withWriters(WRITERS)
                .withGuestStars(GUEST_STARS)
                .withScreenUrl(IMAGE_FILE_NAME)
                .build();

        episode1.mergeWith(episode2);

        assertEquals(episode2.title(), episode1.title());
        assertEquals(episode2.airDate(), episode1.airDate());
        assertEquals(episode2.overview(), episode1.overview());
        assertEquals(episode2.directors(), episode1.directors());
        assertEquals(episode2.writers(), episode1.writers());
        assertEquals(episode2.guestStars(), episode1.guestStars());
        assertEquals(episode2.screenUrl(), episode1.screenUrl());
    }

    @Test
    public void mergingAnEpisodeWithAnotherDoesNotChangesTheValueOfItsSeenMarkField() {
        Episode episode1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .withWatchMark(true)
                .build();

        Episode episode2 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .withWatchMark(false)
                .build();

        episode1.mergeWith(episode2);

        assertTrue(episode1.watched());
    }

    /* isTheSameAs */

    @Test
    public void testIsTheSameAs() {
        Episode e1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode e2 = Episode.builder()
                .withId(ID2)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode e3 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode e4 = Episode.builder()
                .withId(ID2)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER2)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode e5 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER2)
                .build();

        Episode e6 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID2)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER2)
                .build();

        //isTheSameAs is consistent
        for (int i = 1; i <= 1000; i++) {

            //isTheSameAs returns false for null objects
            assertFalse(e1.isTheSameAs(null));

            //isTheSameAs is reflexive
            assertTrue(e1.isTheSameAs(e1));

            //isTheSameAs is symmetric
            assertTrue(e1.isTheSameAs(e2));
            assertTrue(e2.isTheSameAs(e1));

            //isTheSameAs is transitive
            assertTrue(e1.isTheSameAs(e2));
            assertTrue(e2.isTheSameAs(e3));
            assertTrue(e1.isTheSameAs(e3));

            //items are equal if and only if they have the same series id, season number and number
            assertFalse(e3.isTheSameAs(e4));
            assertFalse(e3.isTheSameAs(e5));
            assertFalse(e3.isTheSameAs(e6));
            assertFalse(e4.isTheSameAs(e5));
            assertFalse(e4.isTheSameAs(e6));
            assertFalse(e5.isTheSameAs(e6));
        }
    }

    /* Equals and hashCode */

    @Test
    public void testEquals() {
        Episode e1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode e2 = Episode.builder()
                .withId(ID2)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode e3 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode e4 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID2)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER2)
                .build();

        Episode e5 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID2)
                .withNumber(NUMBER2)
                .withSeasonNumber(SEASON_NUMBER2)
                .build();


        //equals is consistent
        for (int i = 1; i <= 1000; i++) {

            //equals returns false for null objects
            assertFalse(e1.equals(null));

            //equals is reflexive
            assertTrue(e1.equals(e1));

            //equals is symmetric
            assertTrue(e1.equals(e2));
            assertTrue(e2.equals(e1));

            //equals is transitive
            assertTrue(e1.equals(e2));
            assertTrue(e2.equals(e3));
            assertTrue(e1.equals(e3));

            //items are equal if and only if they have the same series id, season number and episode number
            assertFalse(e2.equals(e5));
            assertFalse(e5.equals(e2));
            assertFalse(e4.equals(e2));
            assertFalse(e4.equals(e5));
        }
    }

    @Test
    public void testHashCode() {
        Episode e1 = Episode.builder()
                .withId(ID1)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        Episode e2 = Episode.builder()
                .withId(ID2)
                .withSeriesId(SERIES_ID1)
                .withNumber(NUMBER1)
                .withSeasonNumber(SEASON_NUMBER1)
                .build();

        //hashCode is consistent
        for (int i = 1; i <= 1000; i++) {
            //equal objects have the same hashCode
            assertTrue(e1.equals(e2));
            assertTrue(e1.hashCode() == e2.hashCode());
        }
    }
}
