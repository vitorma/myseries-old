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

import java.util.Date;

import junit.framework.Assert;
import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;

import org.junit.Test;
import org.mockito.Mockito;

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

    /* Mock */

    private static EpisodeListener mockListener() {
        return Mockito.mock(EpisodeListener.class);
    }

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

    /* SeenMark */

    @Test
    public void markingAnEpisodeAsSeenAssignsTrueToItsSeenMarkField() {
        Episode episode = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .withSeenMark(false)
            .build();

        Assert.assertFalse(episode.wasSeen());

        episode.markAsSeen();

        Assert.assertTrue(episode.wasSeen());
    }

    @Test
    public void markingAnEpisodeAsNotSeenAssignsFalseToItsSeenMarkField() {
        Episode episode = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .withSeenMark(true)
            .build();

        Assert.assertTrue(episode.wasSeen());

        episode.markAsNotSeen();

        Assert.assertFalse(episode.wasSeen());
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
    public void mergingAnEpisodeWithAnotherHavingADifferentIdCausesIllegalArgumentException() {
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
            .withName(NAME)
            .withAirDate(AIR_DATE)
            .withOverview(OVERVIEW)
            .withDirectors(DIRECTORS)
            .withWriters(WRITERS)
            .withGuestStars(GUEST_STARS)
            .withImageFileName(IMAGE_FILE_NAME)
            .build();

        episode1.mergeWith(episode2);

        Assert.assertEquals(episode2.name(), episode1.name());
        Assert.assertEquals(episode2.airDate(), episode1.airDate());
        Assert.assertEquals(episode2.overview(), episode1.overview());
        Assert.assertEquals(episode2.directors(), episode1.directors());
        Assert.assertEquals(episode2.writers(), episode1.writers());
        Assert.assertEquals(episode2.guestStars(), episode1.guestStars());
        Assert.assertEquals(episode2.imageFileName(), episode1.imageFileName());
    }

    @Test
    public void mergingAnEpisodeWithAnotherDoesNotChangesTheValueOfItsSeenMarkField() {
        Episode episode1 = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .withSeenMark(true)
            .build();

        Episode episode2 = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .withSeenMark(false)
            .build();

        episode1.mergeWith(episode2);

        Assert.assertTrue(episode1.wasSeen());
    }

    //EpisodeListener---------------------------------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void registeringANullListenerCausesIllegalArgumentException() {
        Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build()
            .register(null);
    }

    @Test
    public void aSameListenerCannotBeRegisteredTwoTimes() {
        Episode episode = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();

        EpisodeListener listener = mockListener();

        Assert.assertTrue(episode.register(listener));
        Assert.assertFalse(episode.register(listener));
    }

    @Test
    public void equalListenersCanBeRegistered() {
        Episode episode = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();

        Assert.assertTrue(episode.register(mockListener()));
        Assert.assertTrue(episode.register(mockListener()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deregisteringANullListenerCausesIllegalArgumentException() {
        Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build()
            .deregister(null);
    }

    @Test
    public void unregisteredListenersCannotBeDeregistered() {
        Episode episode = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();

        Assert.assertFalse(episode.deregister(mockListener()));
    }

    @Test
    public void aRegisteredListenerCanBeDeregistered() {
        Episode episode = Episode.builder()
        .withId(ID1)
        .withSeriesId(SERIES_ID1)
        .withNumber(NUMBER1)
        .withSeasonNumber(SEASON_NUMBER1)
        .build();

        EpisodeListener listener = mockListener();

        Assert.assertTrue(episode.register(mockListener()));
        Assert.assertTrue(episode.register(listener));

        Assert.assertFalse(episode.deregister(mockListener()));
        Assert.assertTrue(episode.deregister(listener));
    }

    @Test
    public void listenersAreNotifiedOnceWhenANotSeenEpisodeIsMarkedAsSeen() {
        Episode episode = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .withSeenMark(false)
            .build();

        Assert.assertFalse(episode.wasSeen());

        EpisodeListener l1 = mockListener();
        EpisodeListener l2 = mockListener();

        Assert.assertTrue(episode.register(l1));
        Assert.assertTrue(episode.register(l2));

        for (int i = 1; i <= 1000; i++) {
            episode.markAsSeen();
            Assert.assertTrue(episode.wasSeen());
        }

        Mockito.verify(l1, Mockito.times(1)).onMarkAsSeen(episode);
        Mockito.verify(l2, Mockito.times(1)).onMarkAsSeen(episode);
    }

    @Test
    public void listenersAreNotifiedOnceWhenASeenEpisodeIsMarkedAsNotSeen() {
        Episode episode = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .withSeenMark(true)
            .build();

        Assert.assertTrue(episode.wasSeen());

        EpisodeListener l1 = mockListener();
        EpisodeListener l2 = mockListener();

        Assert.assertTrue(episode.register(l1));
        Assert.assertTrue(episode.register(l2));

        for (int i = 1; i <= 1000; i++) {
            episode.markAsNotSeen();
            Assert.assertFalse(episode.wasSeen());
        }

        Mockito.verify(l1, Mockito.times(1)).onMarkAsNotSeen(episode);
        Mockito.verify(l2, Mockito.times(1)).onMarkAsNotSeen(episode);
    }

    //Equals and hashCode-----------------------------------------------------------------------------------------------

    @Test
    public void testEquals() {
        Episode e1 = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();

        Episode e2 = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID2)
            .withNumber(NUMBER2)
            .withSeasonNumber(SEASON_NUMBER2)
            .build();

        Episode e3 = Episode.builder()
            .withId(ID1)
            .withSeriesId(SERIES_ID2)
            .withNumber(NUMBER2)
            .withSeasonNumber(SEASON_NUMBER2)
            .build();

        Episode e4 = Episode.builder()
            .withId(ID2)
            .withSeriesId(SERIES_ID2)
            .withNumber(NUMBER2)
            .withSeasonNumber(SEASON_NUMBER2)
            .build();

        //equals is consistent
        for (int i = 1; i <= 1000; i++) {

            //equals returns false for null objects
            Assert.assertFalse(e1.equals(null));

            //equals is reflexive
            Assert.assertTrue(e1.equals(e1));

            //equals is symmetric
            Assert.assertTrue(e1.equals(e2));
            Assert.assertTrue(e2.equals(e1));

            //equals is transitive
            Assert.assertTrue(e1.equals(e2));
            Assert.assertTrue(e2.equals(e3));
            Assert.assertTrue(e1.equals(e3));

            //episodes are equal if and only if they have the same id
            Assert.assertFalse(e2.equals(e4));
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
            .withId(ID1)
            .withSeriesId(SERIES_ID2)
            .withNumber(NUMBER2)
            .withSeasonNumber(SEASON_NUMBER2)
            .build();

        Episode e3 = Episode.builder()
            .withId(ID2)
            .withSeriesId(SERIES_ID2)
            .withNumber(NUMBER2)
            .withSeasonNumber(SEASON_NUMBER2)
            .build();

        //hashCode is consistent
        for (int i = 1; i <= 1000; i++) {

            //equal objects have the same hashCode
            Assert.assertTrue(e1.hashCode() == e2.hashCode());

            //non equal episodes have distinct hashCodes
            Assert.assertTrue(e1.hashCode() != e3.hashCode());
            Assert.assertTrue(e2.hashCode() != e3.hashCode());
        }
    }
}
