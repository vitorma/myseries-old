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
import org.junit.Test;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.AbstractSpecification;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.SeasonListener;
import br.edu.ufcg.aweseries.model.Specification;

public class SeasonTest {

    //Mock--------------------------------------------------------------------------------------------------------------

    private static Episode mockEpisode(int id, int seriesId, int number, int seasonNumber) {
        Episode episode = Mockito.mock(Episode.class);

        Mockito.when(episode.id()).thenReturn(id);
        Mockito.when(episode.seriesId()).thenReturn(seriesId);
        Mockito.when(episode.number()).thenReturn(number);
        Mockito.when(episode.seasonNumber()).thenReturn(seasonNumber);

        return episode;
    }

    private static void markAsSeen(Episode... episodes) {
        for (Episode e : episodes) {
            Mockito.when(e.wasSeen()).thenReturn(true);
        }
    }

    private static void markAsNotSeen(Episode... episodes) {
        for (Episode e : episodes) {
            Mockito.when(e.wasSeen()).thenReturn(false);
        }
    }

    private static void callOnMarkAsSeenFor(Season season, Episode... episodes) {
        for (Episode e : episodes) {
            season.onMarkAsSeen(e);
        }
    }

    private static void callOnMarkAsNotSeenFor(Season season, Episode... episodes) {
        for (Episode e : episodes) {
            season.onMarkAsNotSeen(e);
        }
    }

    public static SeasonListener mockListener() {
        return Mockito.mock(SeasonListener.class);
    }

    //Construction------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void constructingASeasonWithNegativeSeriesIdCausesIllegalArgumentException() {
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

        Assert.assertEquals(0, season.numberOfSeenEpisodes());

        Assert.assertTrue(season.wasSeen());
        Assert.assertNull(season.nextEpisodeToSee());
    }

    //Search------------------------------------------------------------------------------------------------------------

    @Test
    public void anEpisodeIsAlreadyIncludedIfItHasTheSameNumberAsOneOfTheEpisodesOfTheSeason() {
        Season season = new Season(1, 1).including(mockEpisode(1, 1, 1, 1));

        Assert.assertTrue(season.includes(mockEpisode(1, 1, 1, 1)));
        Assert.assertTrue(season.includes(mockEpisode(2, 1, 1, 1)));
        Assert.assertFalse(season.includes(mockEpisode(1, 1, 2, 1)));
    }

    @Test
    public void searchingForAnEpisodeByItsNumberReturnsNullIfThereIsNoEpisodesWithSuchNumberInTheSeason() {
        Assert.assertNull(new Season(1, 1).episode(1));
    }

    @Test
    public void searchingForAnEpisodeByItsNumberReturnsTheEpisodeThatHasSuchNumber() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        Season season = new Season(1, 1).including(episode1).including(episode2);

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
                return episode.wasSeen();
            }
        };

        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(3, 1, 3, 1);
        Episode episode4 = mockEpisode(4, 1, 4, 1);

        markAsSeen(episode1, episode2);
        markAsNotSeen(episode3, episode4);

        Season season = new Season(1, 1)
            .including(episode1)
            .including(episode2)
            .including(episode3)
            .including(episode4);

        Assert.assertEquals(2, season.episodesBy(specification).size());
        Assert.assertTrue(season.episodesBy(specification).contains(episode1));
        Assert.assertTrue(season.episodesBy(specification).contains(episode2));

        Assert.assertEquals(2, season.episodesBy(specification.not()).size());
        Assert.assertTrue(season.episodesBy(specification.not()).contains(episode3));
        Assert.assertTrue(season.episodesBy(specification.not()).contains(episode4));

        Assert.assertTrue(season.episodesBy(specification.and(specification.not())).isEmpty());
    }

    //Inclusion---------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void includingANullEpisodeCausesIllegalArgumentException() {
        new Season(1, 1).including(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void includingAnEpisodeWithAnotherSeriesIdCausesIllegalArgumentException() {
        new Season(1, 1).including(mockEpisode(1, 2, 1, 1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void includingAnEpisodeWithAnotherSeasonNumberCausesIllegalArgumentException() {
        new Season(1, 1).including(mockEpisode(1, 1, 1, 2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void includingAnAlreadyIncludedEpisodeCausesIllegalArgumentException() {
        new Season(1, 1).including(mockEpisode(1, 1, 1, 1)).including(mockEpisode(1, 1, 1, 1));
    }

    @Test
    public void aValidAndNotAlreadyIncludedEpisodeCanBeIncluded() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertEquals(2, season.numberOfEpisodes());
        Assert.assertTrue(season.includes(episode1));
        Assert.assertTrue(season.includes(episode2));
    }

    //SeenMark----------------------------------------------------------------------------------------------------------

    @Test
    public void markingASeasonAsSeenMarksAllItsEpisodesAsSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertFalse(season.wasSeen());
        Assert.assertEquals(episode1, season.nextEpisodeToSee());
        Assert.assertEquals(0, season.numberOfSeenEpisodes());
        for (Episode e : season.episodes()) {
            Assert.assertFalse(e.wasSeen());
        }

        season.markAsSeen();
        markAsSeen(episode1, episode2);
        callOnMarkAsSeenFor(season, episode1, episode2);

        Assert.assertTrue(season.wasSeen());
        Assert.assertNull(season.nextEpisodeToSee());
        Assert.assertEquals(2, season.numberOfSeenEpisodes());
        for (Episode e : season.episodes()) {
            Assert.assertTrue(e.wasSeen());
        }
    }

    @Test
    public void markingASeasonAsNotSeenMarksAllItsEpisodesAsNotSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertTrue(season.wasSeen());
        Assert.assertNull(season.nextEpisodeToSee());
        Assert.assertEquals(2, season.numberOfSeenEpisodes());
        for (Episode e : season.episodes()) {
            Assert.assertTrue(e.wasSeen());
        }

        season.markAsNotSeen();
        markAsNotSeen(episode1, episode2);
        callOnMarkAsNotSeenFor(season, episode1, episode2);

        Assert.assertFalse(season.wasSeen());
        Assert.assertEquals(episode1, season.nextEpisodeToSee());
        Assert.assertEquals(0, season.numberOfSeenEpisodes());
        for (Episode e : season.episodes()) {
            Assert.assertFalse(e.wasSeen());
        }
    }

    @Test
    public void onMarkAnEpisodeAsSeenTheNumberOfSeenEpisodesIsIncreased() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertEquals(0, season.numberOfSeenEpisodes());

        markAsSeen(episode1);
        callOnMarkAsSeenFor(season, episode1);

        Assert.assertEquals(1, season.numberOfSeenEpisodes());

        markAsSeen(episode2);
        callOnMarkAsSeenFor(season, episode2);

        Assert.assertEquals(2, season.numberOfSeenEpisodes());
    }

    @Test
    public void onMarkTheLastNotSeenEpisodeAsSeenTheSeasonIsMarkedAsSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertFalse(season.wasSeen());

        markAsSeen(episode1);
        callOnMarkAsSeenFor(season, episode1);

        Assert.assertFalse(season.wasSeen());

        markAsSeen(episode2);
        callOnMarkAsSeenFor(season, episode2);

        Assert.assertTrue(season.wasSeen());
    }

    @Test
    public void onMarkTheNextEpisodeToSeeAsSeenThisFieldIsUpdated() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertEquals(episode1, season.nextEpisodeToSee());

        markAsSeen(episode1);
        callOnMarkAsSeenFor(season, episode1);

        Assert.assertEquals(episode2, season.nextEpisodeToSee());

        markAsSeen(episode2);
        callOnMarkAsSeenFor(season, episode2);

        Assert.assertNull(season.nextEpisodeToSee());
    }

    @Test
    public void onMarkAnEpisodeAsNotSeenTheNumberOfSeenEpisodesIsDecreased() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertEquals(2, season.numberOfSeenEpisodes());

        markAsNotSeen(episode1);
        callOnMarkAsNotSeenFor(season, episode1);

        Assert.assertEquals(1, season.numberOfSeenEpisodes());

        markAsNotSeen(episode2);
        callOnMarkAsNotSeenFor(season, episode2);

        Assert.assertEquals(0, season.numberOfSeenEpisodes());
    }

    @Test
    public void onMarkAnEpisodeAsNotSeenIfItShouldBeTheNextEpisodeToSeeThenItWillBe() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertNull(season.nextEpisodeToSee());

        markAsNotSeen(episode2);
        callOnMarkAsNotSeenFor(season, episode2);

        Assert.assertEquals(episode2, season.nextEpisodeToSee());

        markAsNotSeen(episode1);
        callOnMarkAsNotSeenFor(season, episode1);

        Assert.assertEquals(episode1, season.nextEpisodeToSee());
    }

    //Merge-------------------------------------------------------------------------------------------------------------

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
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);
        Episode episode3 = mockEpisode(1, 1, 1, 1);
        Episode episode4 = mockEpisode(2, 1, 2, 1);

        Season season1 = new Season(1, 1).including(episode1).including(episode2);
        Season season2 = new Season(1, 1).including(episode3).including(episode4);

        season1.mergeWith(season2);

        Mockito.verify(episode1, Mockito.times(1)).mergeWith(episode3);
        Mockito.verify(episode2, Mockito.times(1)).mergeWith(episode4);

        season2.mergeWith(season1);

        Mockito.verify(episode3, Mockito.times(1)).mergeWith(episode1);
        Mockito.verify(episode4, Mockito.times(1)).mergeWith(episode2);
    }

    @Test
    public void mergingASeasonWithAnotherIncludesInItAllEpisodesIncludedInTheOtherOne() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        Season season1 = new Season(1, 1).including(episode1);
        Season season2 = new Season(1, 1).including(episode2);

        Assert.assertTrue(season1.mergeWith(season2).includes(episode2));
        Assert.assertTrue(season2.mergeWith(season1).includes(episode1));
    }

    //SeasonListener----------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void registeringANullListenerCausesIllegalArgumentException() {
        new Season(1, 1).register(null);
    }

    @Test
    public void aSameListenerCannotBeRegisteredTwoTimes() {
        Season season = new Season(1, 1);
        SeasonListener listener = mockListener();

        Assert.assertTrue(season.register(listener));
        Assert.assertFalse(season.register(listener));
    }

    @Test
    public void equalListenersCanBeRegistered() {
        Season season = new Season(1, 1);

        Assert.assertTrue(season.register(mockListener()));
        Assert.assertTrue(season.register(mockListener()));
    }

    @Test(expected=IllegalArgumentException.class)
    public void deregisteringANullListenerCausesIllegalArgumentException() {
        new Season(1, 1).deregister(null);
    }

    @Test
    public void anUnregisteredListenerCannotBeDeregistered() {
        Season season = new Season(1, 1);

        Assert.assertFalse(season.deregister(mockListener()));
}

    @Test
    public void aRegisteredListenerCanBeDeregistered() {
        Season season = new Season(1, 1);
        SeasonListener listener = mockListener();

        Assert.assertTrue(season.register(mockListener()));
        Assert.assertTrue(season.register(listener));

        Assert.assertFalse(season.deregister(mockListener()));
        Assert.assertTrue(season.deregister(listener));
    }

    @Test
    public void listenersAreNotifiedWhenAnInclusionCausesTheChangeOfTheTheNumberOfSeenEpisodes() {
        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Season season = new Season(1, 1);

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsSeen(episode1, episode2);

        Assert.assertEquals(1, season.including(episode1).numberOfSeenEpisodes());
        Mockito.verify(listener1, Mockito.times(1)).onChangeNumberOfSeenEpisodes(season);
        Mockito.verify(listener2, Mockito.times(1)).onChangeNumberOfSeenEpisodes(season);

        Assert.assertEquals(2, season.including(episode2).numberOfSeenEpisodes());
        Mockito.verify(listener1, Mockito.times(2)).onChangeNumberOfSeenEpisodes(season);
        Mockito.verify(listener2, Mockito.times(2)).onChangeNumberOfSeenEpisodes(season);
    }

    @Test
    public void listenersAreNotifiedWhenAnInclusionCausesTheChangeOfTheNextEpisodeToSee() {
        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Season season = new Season(1, 1);

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Assert.assertEquals(episode2, season.including(episode2).nextEpisodeToSee());
        Mockito.verify(listener1, Mockito.times(1)).onChangeNextEpisodeToSee(season);
        Mockito.verify(listener2, Mockito.times(1)).onChangeNextEpisodeToSee(season);

        Assert.assertEquals(episode1, season.including(episode1).nextEpisodeToSee());
        Mockito.verify(listener1, Mockito.times(2)).onChangeNextEpisodeToSee(season);
        Mockito.verify(listener2, Mockito.times(2)).onChangeNextEpisodeToSee(season);
    }

    @Test
    public void listenersAreNotifiedWhenAnInclusionCausesTheSeasonBeMarkedAsNotSeen() {
        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Season season = new Season(1, 1);

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        Episode episode1 = mockEpisode(1, 1, 1, 1);

        markAsSeen(episode1);

        season.including(episode1);

        Mockito.verify(listener1, Mockito.times(0)).onMarkAsNotSeen(season);
        Mockito.verify(listener2, Mockito.times(0)).onMarkAsNotSeen(season);

        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode2);

        season.including(episode2);

        Mockito.verify(listener1, Mockito.times(1)).onMarkAsNotSeen(season);
        Mockito.verify(listener2, Mockito.times(1)).onMarkAsNotSeen(season);
    }

    @Test
    public void listenersAreNotifiedWhenASeasonIsMarkedAsSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        season.markAsSeen();
        markAsSeen(episode1, episode2);
        callOnMarkAsSeenFor(season, episode1, episode2);

        Mockito.verify(listener1, Mockito.times(1)).onMarkAsSeen(season);
        Mockito.verify(listener2, Mockito.times(1)).onMarkAsSeen(season);
    }

    @Test
    public void listenersAreNotifiedWhenASeasonIsMarkedAsNotSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2  = mockEpisode(2, 1, 2, 1);

        markAsSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        season.markAsNotSeen();
        markAsNotSeen(episode1, episode2);
        callOnMarkAsNotSeenFor(season, episode1, episode2);

        Mockito.verify(listener1, Mockito.times(1)).onMarkAsNotSeen(season);
        Mockito.verify(listener2, Mockito.times(1)).onMarkAsNotSeen(season);
    }

    @Test
    public void listenersAreNotifiedWhenASeasonIsMergedWithAnother() {
        Season season1 = new Season(1, 1);
        Season season2 = new Season(1, 1);

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season1.register(listener1));
        Assert.assertTrue(season2.register(listener2));

        season1.mergeWith(season2);

        Mockito.verify(listener1, Mockito.times(1)).onMerge(season1);

        season2.mergeWith(season1);

        Mockito.verify(listener2, Mockito.times(1)).onMerge(season2);
    }

    @Test
    public final void listenersAreNotifiedWhenTheNumberOfSeenEpisodesChangesBecauseAnEpisodeWasMarkedAsSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertEquals(0, season.numberOfSeenEpisodes());

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        markAsSeen(episode1);
        callOnMarkAsSeenFor(season, episode1);

        Assert.assertEquals(1, season.numberOfSeenEpisodes());
        Mockito.verify(listener1, Mockito.times(1)).onChangeNumberOfSeenEpisodes(season);
        Mockito.verify(listener2, Mockito.times(1)).onChangeNumberOfSeenEpisodes(season);

        markAsSeen(episode2);
        callOnMarkAsSeenFor(season, episode2);

        Assert.assertEquals(2, season.numberOfSeenEpisodes());
        Mockito.verify(listener1, Mockito.times(2)).onChangeNumberOfSeenEpisodes(season);
        Mockito.verify(listener2, Mockito.times(2)).onChangeNumberOfSeenEpisodes(season);
    }

    @Test
    public final void listenersAreNotifiedWhenTheNextEpisodeToSeeChangesBecauseAnEpisodeWasMarkedAsSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertEquals(episode1, season.nextEpisodeToSee());

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        markAsSeen(episode1);
        callOnMarkAsSeenFor(season, episode1);

        Assert.assertEquals(episode2, season.nextEpisodeToSee());
        Mockito.verify(listener1, Mockito.times(1)).onChangeNextEpisodeToSee(season);
        Mockito.verify(listener2, Mockito.times(1)).onChangeNextEpisodeToSee(season);

        markAsSeen(episode2);
        callOnMarkAsSeenFor(season, episode2);

        Assert.assertNull(season.nextEpisodeToSee());
        Mockito.verify(listener1, Mockito.times(2)).onChangeNextEpisodeToSee(season);
        Mockito.verify(listener2, Mockito.times(2)).onChangeNextEpisodeToSee(season);
    }

    @Test
    public final void listenersAreNotifiedWhenTheSeasonIsMarkedAsSeenBecauseAnEpisodeWasMarkedAsSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsNotSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertFalse(season.wasSeen());

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        markAsSeen(episode1);
        callOnMarkAsSeenFor(season, episode1);

        Assert.assertFalse(season.wasSeen());
        Mockito.verify(listener1, Mockito.times(0)).onMarkAsSeen(season);
        Mockito.verify(listener2, Mockito.times(0)).onMarkAsSeen(season);

        markAsSeen(episode2);
        callOnMarkAsSeenFor(season, episode2);

        Assert.assertTrue(season.wasSeen());
        Mockito.verify(listener1, Mockito.times(1)).onMarkAsSeen(season);
        Mockito.verify(listener2, Mockito.times(1)).onMarkAsSeen(season);
    }

    @Test
    public final void listenersAreNotifiedWhenTheNumberOfSeenEpisodesChangesBecauseAnEpisodeWasMarkedAsNotSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertEquals(2, season.numberOfSeenEpisodes());

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        markAsNotSeen(episode1);
        callOnMarkAsNotSeenFor(season, episode1);

        Assert.assertEquals(1, season.numberOfSeenEpisodes());
        Mockito.verify(listener1, Mockito.times(1)).onChangeNumberOfSeenEpisodes(season);
        Mockito.verify(listener2, Mockito.times(1)).onChangeNumberOfSeenEpisodes(season);

        markAsNotSeen(episode2);
        callOnMarkAsNotSeenFor(season, episode2);

        Assert.assertEquals(0, season.numberOfSeenEpisodes());
        Mockito.verify(listener1, Mockito.times(2)).onChangeNumberOfSeenEpisodes(season);
        Mockito.verify(listener2, Mockito.times(2)).onChangeNumberOfSeenEpisodes(season);
    }

    @Test
    public final void listenersAreNotifiedWhenTheNextEpisodeToSeeChangesBecauseAnEpisodeWasMarkedAsNotSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertNull(season.nextEpisodeToSee());

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        markAsNotSeen(episode2);
        callOnMarkAsNotSeenFor(season, episode2);

        Assert.assertEquals(episode2, season.nextEpisodeToSee());
        Mockito.verify(listener1, Mockito.times(1)).onChangeNextEpisodeToSee(season);
        Mockito.verify(listener2, Mockito.times(1)).onChangeNextEpisodeToSee(season);

        markAsNotSeen(episode1);
        callOnMarkAsNotSeenFor(season, episode1);

        Assert.assertEquals(episode1, season.nextEpisodeToSee());
        Mockito.verify(listener1, Mockito.times(2)).onChangeNextEpisodeToSee(season);
        Mockito.verify(listener2, Mockito.times(2)).onChangeNextEpisodeToSee(season);
    }

    @Test
    public final void listenersAreNotifiedWhenTheSeasonIsMarkedAsNotSeenBecauseAnEpisodeWasMarkedAsNotSeen() {
        Episode episode1 = mockEpisode(1, 1, 1, 1);
        Episode episode2 = mockEpisode(2, 1, 2, 1);

        markAsSeen(episode1, episode2);

        Season season = new Season(1, 1).including(episode1).including(episode2);

        Assert.assertTrue(season.wasSeen());

        SeasonListener listener1 = mockListener();
        SeasonListener listener2 = mockListener();

        Assert.assertTrue(season.register(listener1));
        Assert.assertTrue(season.register(listener2));

        markAsNotSeen(episode1);
        callOnMarkAsNotSeenFor(season, episode1);

        Assert.assertFalse(season.wasSeen());
        Mockito.verify(listener1, Mockito.times(1)).onMarkAsNotSeen(season);
        Mockito.verify(listener2, Mockito.times(1)).onMarkAsNotSeen(season);

        markAsNotSeen(episode2);
        callOnMarkAsNotSeenFor(season, episode2);

        Assert.assertFalse(season.wasSeen());
        Mockito.verify(listener1, Mockito.times(1)).onMarkAsNotSeen(season);
        Mockito.verify(listener2, Mockito.times(1)).onMarkAsNotSeen(season);
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
