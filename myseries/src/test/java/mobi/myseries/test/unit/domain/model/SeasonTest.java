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

package mobi.myseries.test.unit.domain.model;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.gui.shared.EpisodeWatchMarkSpecification;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Specification;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class SeasonTest {
    private static final EpisodeWatchMarkSpecification WATCHED_SPEC = new EpisodeWatchMarkSpecification(true);

    /* Mock */

    private static Episode mockEpisode(Long id, int seriesId, int number, int seasonNumber) {
        Episode episode = Mockito.mock(Episode.class);

        Mockito.when(episode.id()).thenReturn(id);
        Mockito.when(episode.seriesId()).thenReturn(seriesId);
        Mockito.when(episode.number()).thenReturn(number);
        Mockito.when(episode.seasonNumber()).thenReturn(seasonNumber);

        return episode;
    }

    private static void markAsWatched(Episode... episodes) {
        for (Episode e : episodes) {
            Mockito.when(e.watched()).thenReturn(true);
            Mockito.when(e.unwatched()).thenReturn(false);
        }
    }

    private static void markAsUnwatched(Episode... episodes) {
        for (Episode e : episodes) {
            Mockito.when(e.watched()).thenReturn(false);
            Mockito.when(e.unwatched()).thenReturn(true);
        }
    }

    /* Construction */

    @Test
    public void constructingASeasonWithNegativeSeriesIdDoesNotCauseException() {
        new Season(-1, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructingASeasonWithNegativeNumberCausesIllegalArgumentException() {
        new Season(0, -1);
    }

    @Test
    public void theConstructedSeasonKeepsTheSameGivenSeriesIdAndNumber() {
        Season season1 = new Season(0, 0);

        Assert.assertEquals(0, season1.seriesId());
        Assert.assertEquals(0, season1.number());

        Season season2 = new Season(1, 2);

        Assert.assertEquals(1, season2.seriesId());
        Assert.assertEquals(2, season2.number());

        Season season3 = new Season(2, 1);

        Assert.assertEquals(2, season3.seriesId());
        Assert.assertEquals(1, season3.number());
    }

    @Test
    public void theConstructedSeasonIncludesNoEpisodes() {
        Season season = new Season(1, 1);

        Assert.assertTrue(season.episodes().isEmpty());
        Assert.assertEquals(0, season.numberOfEpisodes());
    }

    @Test
    public void theConstructedSeasonIncludesNoSeenEpisodesButItWasSeen() {
        Season season = new Season(1, 1);

        Assert.assertEquals(0, season.numberOfEpisodes());

        Assert.assertEquals(season.numberOfEpisodes(), season.numberOfEpisodes(WATCHED_SPEC));
        Assert.assertNull(season.nextEpisodeToWatch());
    }

    /* Search */

    @Test
    public void anEpisodeIsAlreadyIncludedIfItHasTheSameNumberAsOneOfTheEpisodesOfTheSeason() {
        Season season = new Season(1, 1).include(mockEpisode(1L, 1, 1, 1));

        Assert.assertTrue(season.includes(mockEpisode(1L, 1, 1, 1)));
        Assert.assertTrue(season.includes(mockEpisode(2L, 1, 1, 1)));  // even if their ids are different
        Assert.assertFalse(season.includes(mockEpisode(1L, 1, 2, 1)));
    }

    @Test
    public void searchingForAnEpisodeByItsNumberReturnsNullIfThereIsNoEpisodesWithSuchNumberInTheSeason() {
        Assert.assertNull(new Season(1, 1).episode(1));
    }

    @Test
    public void searchingForAnEpisodeByItsNumberReturnsTheEpisodeThatHasSuchNumber() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2 = mockEpisode(2L, 1, 2, 1);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertEquals(episode1, season.episode(1));
        Assert.assertEquals(episode2, season.episode(2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void searchingForEpisodesByANullSpecificationCausesIllegalArgumentException() {
        new Season(1, 1).episodesBy(null);
    }

    @Test
    public void searchingForEpisodesByAGivenSpecificationReturnsAllEpisodesThatSatisfyIt() {
        Specification<Episode> specification = new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode episode) {
                return episode.watched();
            }
        };

        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2 = mockEpisode(2L, 1, 2, 1);
        Episode episode3 = mockEpisode(3L, 1, 3, 1);
        Episode episode4 = mockEpisode(4L, 1, 4, 1);

        markAsWatched(episode1, episode2);
        markAsUnwatched(episode3, episode4);

        Season season = new Season(1, 1)
        .include(episode1)
        .include(episode2)
        .include(episode3)
        .include(episode4);

        Assert.assertEquals(2, season.episodesBy(specification).size());
        Assert.assertTrue(season.episodesBy(specification).contains(episode1));
        Assert.assertTrue(season.episodesBy(specification).contains(episode2));

        Assert.assertEquals(2, season.episodesBy(specification.not()).size());
        Assert.assertTrue(season.episodesBy(specification.not()).contains(episode3));
        Assert.assertTrue(season.episodesBy(specification.not()).contains(episode4));

        Assert.assertTrue(season.episodesBy(specification.and(specification.not())).isEmpty());
    }

    /* Inclusion */

    @Test
    public void includingIsLenient() {
        try {
            Season s1 = new Season(1, 1);

            //for null episodes
            s1.include(null);
            Assert.assertFalse(s1.includes(null));

            //for episodes with another seriesId
            Episode e0 = mockEpisode(1L, 2, 1, 1);
            s1.include(e0);
            Assert.assertFalse(s1.includes(e0));

            //for episodes with another seasonNumber
            Episode e1 = mockEpisode(1L, 1, 1, 2);
            s1.include(e1);
            Assert.assertFalse(s1.includes(e1));

            //for already included episodes
            Episode e2 = mockEpisode(1L, 1, 1, 1);
            s1.include(e2);
            Assert.assertTrue(s1.includes(e2));
            Assert.assertEquals(1, s1.numberOfEpisodes());
            s1.include(e2);
            Assert.assertTrue(s1.includes(e2));
            Assert.assertEquals(1, s1.numberOfEpisodes());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void aValidAndNotAlreadyIncludedEpisodeCanBeIncluded() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2 = mockEpisode(2L, 1, 2, 1);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertEquals(2, season.numberOfEpisodes());
        Assert.assertTrue(season.includes(episode1));
        Assert.assertTrue(season.includes(episode2));
    }

    /* SeenMark */

    @Test
    public void markingASeasonAsSeenMarksAllItsEpisodesAsSeen() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2  = mockEpisode(2L, 1, 2, 1);

        markAsUnwatched(episode1, episode2);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertEquals(episode1, season.nextEpisodeToWatch());
        Assert.assertEquals(0, season.numberOfEpisodes(WATCHED_SPEC));
        for (Episode e : season.episodes()) {
            Assert.assertFalse(e.watched());
        }

        season.markAsWatched();
        markAsWatched(episode1, episode2);

        Assert.assertNull(season.nextEpisodeToWatch());
        Assert.assertEquals(2, season.numberOfEpisodes(WATCHED_SPEC));
        for (Episode e : season.episodes()) {
            Assert.assertTrue(e.watched());
        }
    }

    @Test
    public void markingASeasonAsNotSeenMarksAllItsEpisodesAsNotSeen() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2  = mockEpisode(2L, 1, 2, 1);

        markAsWatched(episode1, episode2);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertEquals(2, season.numberOfEpisodes(WATCHED_SPEC));
        for (Episode e : season.episodes()) {
            Assert.assertTrue(e.watched());
        }
        Assert.assertNull(season.nextEpisodeToWatch());

        season.markAsUnwatched();
        markAsUnwatched(episode1, episode2);

        Assert.assertEquals(0, season.numberOfEpisodes(WATCHED_SPEC));
        for (Episode e : season.episodes()) {
            Assert.assertFalse(e.watched());
        }
        Assert.assertEquals(episode1, season.nextEpisodeToWatch());
    }

    @Test
    public void onMarkAnEpisodeAsSeenTheNumberOfSeenEpisodesIsIncreased() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2  = mockEpisode(2L, 1, 2, 1);

        markAsUnwatched(episode1, episode2);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertEquals(0, season.numberOfEpisodes(WATCHED_SPEC));

        markAsWatched(episode1);

        Assert.assertEquals(1, season.numberOfEpisodes(WATCHED_SPEC));

        markAsWatched(episode2);

        Assert.assertEquals(2, season.numberOfEpisodes(WATCHED_SPEC));
    }

    @Test
    public void onMarkTheLastNotSeenEpisodeAsSeenTheSeasonIsMarkedAsSeen() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2  = mockEpisode(2L, 1, 2, 1);

        markAsUnwatched(episode1, episode2);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertNotEquals(season.numberOfEpisodes(), season.numberOfEpisodes(WATCHED_SPEC));

        markAsWatched(episode1);

        Assert.assertNotEquals(season.numberOfEpisodes(), season.numberOfEpisodes(WATCHED_SPEC));

        markAsWatched(episode2);

        Assert.assertEquals(season.numberOfEpisodes(), season.numberOfEpisodes(WATCHED_SPEC));
    }

    @Test
    public void onMarkTheNextEpisodeToSeeAsSeenThisFieldIsUpdated() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2  = mockEpisode(2L, 1, 2, 1);

        markAsUnwatched(episode1, episode2);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertEquals(episode1, season.nextEpisodeToWatch());

        markAsWatched(episode1);

        Assert.assertEquals(episode2, season.nextEpisodeToWatch());

        markAsWatched(episode2);

        Assert.assertNull(season.nextEpisodeToWatch());
    }

    @Test
    public void onMarkAnEpisodeAsNotSeenTheNumberOfSeenEpisodesIsDecreased() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2  = mockEpisode(2L, 1, 2, 1);

        markAsWatched(episode1, episode2);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertEquals(2, season.numberOfEpisodes(WATCHED_SPEC));

        markAsUnwatched(episode1);

        Assert.assertEquals(1, season.numberOfEpisodes(WATCHED_SPEC));

        markAsUnwatched(episode2);

        Assert.assertEquals(0, season.numberOfEpisodes(WATCHED_SPEC));
    }

    @Test
    public void onMarkAnEpisodeAsNotSeenIfItShouldBeTheNextEpisodeToSeeThenItWillBe() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2  = mockEpisode(2L, 1, 2, 1);

        markAsWatched(episode1, episode2);

        Season season = new Season(1, 1).include(episode1).include(episode2);

        Assert.assertNull(season.nextEpisodeToWatch());

        markAsUnwatched(episode2);

        Assert.assertEquals(episode2, season.nextEpisodeToWatch());

        markAsUnwatched(episode1);

        Assert.assertEquals(episode1, season.nextEpisodeToWatch());
    }

    /* Merge */

    @Test(expected = IllegalArgumentException.class)
    public void mergingASeasonWithANullOtherCausesIllegalArgumentException() {
        new Season(1, 1).mergeWith(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingASeasonWithAnotherHavingADifferentSeriesIdCausesIllegalArgumentException() {
        new Season(1, 1).mergeWith(new Season(2, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void mergingASeasonWithAnotherHavingADifferentNumberCausesIllegalArgumentException() {
        new Season(1, 1).mergeWith(new Season(1, 2));
    }

    @Test
    public void mergingASeasonWithAnotherMergesAllItsEpisodesThatAreIncludedInTheOtherOne() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2 = mockEpisode(2L, 1, 2, 1);
        Episode episode3 = mockEpisode(1L, 1, 1, 1);
        Episode episode4 = mockEpisode(2L, 1, 2, 1);

        Season season1 = new Season(1, 1).include(episode1).include(episode2);
        Season season2 = new Season(1, 1).include(episode3).include(episode4);

        season1.mergeWith(season2);

        Mockito.verify(episode1, Mockito.times(1)).mergeWith(episode3);
        Mockito.verify(episode2, Mockito.times(1)).mergeWith(episode4);

        season2.mergeWith(season1);

        Mockito.verify(episode3, Mockito.times(1)).mergeWith(episode1);
        Mockito.verify(episode4, Mockito.times(1)).mergeWith(episode2);
    }

    @Test
    public void mergingASeasonWithAnotherIncludesInItAllEpisodesIncludedInTheOtherOne() {
        Episode episode1 = mockEpisode(1L, 1, 1, 1);
        Episode episode2 = mockEpisode(2L, 1, 2, 1);

        Season season1 = new Season(1, 1).include(episode1);
        Season season2 = new Season(1, 1).include(episode2);

        Assert.assertTrue(season1.mergeWith(season2).includes(episode2));
    }

    /* Equals and HashCode */

    @Test
    public void testEquals() {
        Season s1 = new Season(1,1);
        Season s2 = new Season(1,1);
        Season s3 = new Season(1,1);
        Season s4 = new Season(1,2);
        Season s5 = new Season(2,1);

        //equals is consistent
        for (int i=1; i<=1000; i++) {

            //equals returns false for null objects
            Assert.assertFalse(s1.equals(null));

            //equals is reflexive
            Assert.assertTrue(s1.equals(s1));

            //equals is symmetric
            Assert.assertTrue(s1.equals(s2));
            Assert.assertTrue(s2.equals(s1));

            //equals is transitive
            Assert.assertTrue(s1.equals(s2));
            Assert.assertTrue(s2.equals(s3));
            Assert.assertTrue(s1.equals(s3));

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
            Assert.assertTrue(s1.hashCode() == s2.hashCode());
        }
    }
}
