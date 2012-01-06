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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.DomainObjectListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;

public class SeasonTest {

    //TODO Implement tests for all methods but using mocks for episodes

    private Season season;
    private Episode episode1;
    private Episode episode2;
    private Episode episode3;
    private Episode episode4;

    @Before
    public void setUp() throws Exception {
        this.season = new Season("1", 1);
        this.episode1 = new Episode("1", "1", 1, 1);
        this.episode2 = new Episode("2", "1", 2, 1);
        this.episode3 = new Episode("3", "1", 3, 1);
        this.episode4 = new Episode("4", "1", 4, 1);
        this.season.addEpisode(this.episode1);
        this.season.addEpisode(this.episode2);
        this.season.addEpisode(this.episode3);
        this.season.addEpisode(this.episode4);
    }

    @Test(expected=IllegalArgumentException.class)
    public final void testAddNullEpisode() {
        this.season.addEpisode(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public final void testAddEpisodeWithAnotherSeriesId() {
        this.season.addEpisode(new Episode("3", "3", 3, 1));
    }

    @Test(expected=IllegalArgumentException.class)
    public final void testAddEpisodeWithAnotherSeasonNumber() {
        this.season.addEpisode(new Episode("3", "1", 3, 2));
    }

    @Test(expected=IllegalArgumentException.class)
    public final void testAddAlreadyExistentEpisode() {
        this.season.addEpisode(this.episode2);
    }

    @Test
    public final void testAddEpisode() {
        Assert.assertThat(this.season.getEpisodes(),
                JUnitMatchers.hasItems(this.episode1, this.episode2));
        Assert.assertEquals(this.season.getNumberOfEpisodes(), 4);

        final Episode episode3 = new Episode("5", "1", 5, 1);
        this.season.addEpisode(episode3);
        Assert.assertEquals(this.season.getNumberOfEpisodes(), 5);
        Assert.assertThat(this.season.getEpisodes(), JUnitMatchers.hasItem(episode3));
    }
    
    @Test
    public final void testMarkAllAsSeen() {
        this.season.markAllAsSeen();
        Assert.assertTrue(this.season.areAllSeen());
        Assert.assertEquals(null, this.season.getNextEpisodeToSee());
    }
    
    @Test
    public final void testMarkAllAsNotSeen() {
        this.season.markAllAsSeen();
        
        this.season.markAllAsNotSeen();
         
        Assert.assertFalse(this.season.areAllSeen());
        Assert.assertEquals(this.episode1, this.season.getNextEpisodeToSee());
    }
    
    @Test
    public final void testGetNextEpisodeToSee() {
        Assert.assertEquals(this.episode1, this.season.getNextEpisodeToSee());
        this.season.markAllAsSeen();
        Assert.assertEquals(null, this.season.getNextEpisodeToSee());
        this.season.markAllAsNotSeen();
        Assert.assertEquals(this.episode1, this.season.getNextEpisodeToSee());
        this.season.markAllAsSeen();
        this.episode1.markAsNotSeen();
        Assert.assertEquals(this.episode1, this.season.getNextEpisodeToSee());
        this.episode1.markAsSeen();
        Assert.assertEquals(null, this.season.getNextEpisodeToSee());
        this.episode3.markAsNotSeen();
        Assert.assertEquals(this.episode3, this.season.getNextEpisodeToSee());
        this.episode2.markAsNotSeen();
        Assert.assertEquals(this.episode2, this.season.getNextEpisodeToSee());
        this.episode4.markAsNotSeen();
        Assert.assertEquals(this.episode2, this.season.getNextEpisodeToSee());
        this.episode1.markAsNotSeen();
        Assert.assertEquals(this.episode1, this.season.getNextEpisodeToSee());
        this.episode2.markAsSeen();
        Assert.assertEquals(this.episode1, this.season.getNextEpisodeToSee());
        this.episode1.markAsSeen();
        Assert.assertEquals(this.episode3, this.season.getNextEpisodeToSee());
    }
    
    //TODO: The notification policy has changed after Updating Data User Story
    @Ignore
    @Test
    public final void testNotifyListeners() {
        java.util.List<DomainObjectListener<Season>> listeners =
                new ArrayList<DomainObjectListener<Season>>();

        for (int i = 0; i < 10; ++i) {
            DomainObjectListener<Season> listener = Mockito.mock(DomainObjectListener.class);
            listeners.add(listener);
            this.season.addListener(listener);
        }
        
        this.episode1.markAsSeen();
        
        for (DomainObjectListener<Season> listener : listeners) {
            Mockito.verify(listener, Mockito.times(1)).onUpdate(this.season);
        }
        
        this.episode2.markAsSeen();

        for (DomainObjectListener<Season> listener : listeners) {
            Mockito.verify(listener, Mockito.times(2)).onUpdate(this.season);
        }

        this.episode1.markAsNotSeen();
        
        for (DomainObjectListener<Season> listener : listeners) {
            Mockito.verify(listener, Mockito.times(3)).onUpdate(this.season);
        }
        
        this.episode2.markAsNotSeen();

        for (DomainObjectListener<Season> listener : listeners) {
            Mockito.verify(listener, Mockito.times(3)).onUpdate(this.season);
        }
        
        this.season.markAllAsSeen();
        
        for (DomainObjectListener<Season> listener : listeners) {
            Mockito.verify(listener, Mockito.times(7)).onUpdate(this.season);
        }

        this.season.markAllAsNotSeen();
        
        for (DomainObjectListener<Season> listener : listeners) {
            Mockito.verify(listener, Mockito.times(8)).onUpdate(this.season);
        }
    }
    
    
    @Test
    public final void testGetNextEpisodeToAir() {
        Assert.assertEquals(null, this.season.getNextEpisodeToAir());
        final Date today = new Date();
        final long oneDayMilis = 1000 * 60 * 60 * 24;

        final Date tomorrow = new Date(today.getTime() + oneDayMilis );
        final Date dayAfterTomorrow = new Date(today.getTime() + 2 * oneDayMilis );
        final Date nextWeek = new Date(today.getTime() + 7 * oneDayMilis );
        final Date weekAfterNextWeek = new Date(today.getTime() + 14 * oneDayMilis );
        
        Episode episode5 = new Episode("5", "1", 5, 1);
        episode5.setFirstAired(tomorrow);
        Episode episode6 = new Episode("6", "1", 6, 1);
        episode6.setFirstAired(dayAfterTomorrow);
        Episode episode7 = new Episode("7", "1", 7, 1);
        episode7.setFirstAired(nextWeek);
        Episode episode8 = new Episode("8", "1", 8, 1);
        episode8.setFirstAired(weekAfterNextWeek);
        
        this.season.addEpisode(episode7);
        Assert.assertEquals(episode7, this.season.getNextEpisodeToAir());

        this.season.addEpisode(episode6);
        Assert.assertEquals(episode6, this.season.getNextEpisodeToAir());

        this.season.addEpisode(episode8);
        Assert.assertEquals(episode6, this.season.getNextEpisodeToAir());

        this.season.addEpisode(episode5);
        Assert.assertEquals(episode5, this.season.getNextEpisodeToAir());
    }
   
    @Test
    public final void testGetLastAiredEpisode() {
        Assert.assertEquals(null , this.season.getLastAiredEpisode());
        
        final Date today = new Date();
        final long oneDayMilis = 1000 * 60 * 60 * 24;

        final Date tenSecondsAgo =  new Date(today.getTime() - 1000 * 60);
        final Date yesterday =  new Date(today.getTime() - oneDayMilis);
        final Date dayBeforeYesterday =  new Date(today.getTime() - 2 * oneDayMilis);
        final Date lastWeek =  new Date(today.getTime() - 7 * oneDayMilis);
        final Date weekBeforeLast =  new Date(today.getTime() - 14 * oneDayMilis);

        Episode episode5 = new Episode("5", "1", 5, 1);
        episode5.setFirstAired(weekBeforeLast);
        Episode episode6 = new Episode("6", "1", 6, 1);
        episode6.setFirstAired(lastWeek);
        Episode episode7 = new Episode("7", "1", 7, 1);
        episode7.setFirstAired(dayBeforeYesterday);
        Episode episode8 = new Episode("8", "1", 8, 1);
        episode8.setFirstAired(yesterday);
        Episode episode9 = new Episode("9", "1", 9, 1);
        episode9.setFirstAired(tenSecondsAgo);
        
        this.season.addEpisode(episode6);
        Assert.assertEquals(episode6, this.season.getLastAiredEpisode());

        this.season.addEpisode(episode5);
        Assert.assertEquals(episode6, this.season.getLastAiredEpisode());
        
        this.season.addEpisode(episode7);
        Assert.assertEquals(episode7, this.season.getLastAiredEpisode());

        this.season.addEpisode(episode9);
        Assert.assertEquals(episode9, this.season.getLastAiredEpisode());

        this.season.addEpisode(episode8);
        Assert.assertEquals(episode9, this.season.getLastAiredEpisode());
    }

    @Test
    public final void testGetLastAiredNotSeenEpisode() {
        final List<Episode> lastNotSeenEpisodes = new ArrayList<Episode>();

        Assert.assertEquals(lastNotSeenEpisodes , this.season.getLastAiredNotSeenEpisodes());
        
        final Date today = new Date();
        final long oneDayMilis = 1000 * 60 * 60 * 24;

        final Date tenSecondsAgo =  new Date(today.getTime() - 1000 * 60);
        final Date yesterday =  new Date(today.getTime() - oneDayMilis);
        final Date dayBeforeYesterday =  new Date(today.getTime() - 2 * oneDayMilis);
        final Date lastWeek =  new Date(today.getTime() - 7 * oneDayMilis);
        final Date weekBeforeLast =  new Date(today.getTime() - 14 * oneDayMilis);

        Episode episode5 = new Episode("5", "1", 5, 1);
        episode5.setFirstAired(weekBeforeLast);
        Episode episode6 = new Episode("6", "1", 6, 1);
        episode6.setFirstAired(lastWeek);
        Episode episode7 = new Episode("7", "1", 7, 1);
        episode7.setFirstAired(dayBeforeYesterday);
        Episode episode8 = new Episode("8", "1", 8, 1);
        episode8.setFirstAired(yesterday);
        Episode episode9 = new Episode("9", "1", 9, 1);
        episode9.setFirstAired(tenSecondsAgo);
        
        this.season.addEpisode(episode6);
        this.season.addEpisode(episode5);
        this.season.addEpisode(episode7);
        this.season.addEpisode(episode9);
        this.season.addEpisode(episode8);
        
        lastNotSeenEpisodes.add(episode5);
        lastNotSeenEpisodes.add(episode6);
        lastNotSeenEpisodes.add(episode7);
        lastNotSeenEpisodes.add(episode8);
        lastNotSeenEpisodes.add(episode9);
        
        Assert.assertEquals(lastNotSeenEpisodes, this.season.getLastAiredNotSeenEpisodes());
        
        episode9.markAsSeen();
        lastNotSeenEpisodes.remove(episode9);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.getLastAiredNotSeenEpisodes());
        
        episode7.markAsSeen();
        lastNotSeenEpisodes.remove(episode7);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.getLastAiredNotSeenEpisodes());
        
        episode8.markAsSeen();
        episode6.markAsSeen();
        episode5.markAsSeen();
        lastNotSeenEpisodes.clear();
        Assert.assertEquals(lastNotSeenEpisodes, this.season.getLastAiredNotSeenEpisodes());
        
        episode8.markAsNotSeen();
        lastNotSeenEpisodes.add(episode8);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.getLastAiredNotSeenEpisodes());

        lastNotSeenEpisodes.clear();
        episode5.markAsNotSeen();
        lastNotSeenEpisodes.add(episode5);
        lastNotSeenEpisodes.add(episode8);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.getLastAiredNotSeenEpisodes());
        
        episode9.markAsNotSeen();
        lastNotSeenEpisodes.add(episode9);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.getLastAiredNotSeenEpisodes());
    }
    
    @Test
    public final void testGetNextEpisodesToAir() {
        final List<Episode> nextEpisodes = new ArrayList<Episode>();
        
        Assert.assertEquals(nextEpisodes, this.season.getNextEpisodesToAir());
        
        final Date today = new Date();
        final long oneDayMilis = 1000 * 60 * 60 * 24;

        final Date tomorrow = new Date(today.getTime() + oneDayMilis );
        final Date dayAfterTomorrow = new Date(today.getTime() + 2 * oneDayMilis );
        final Date nextWeek = new Date(today.getTime() + 7 * oneDayMilis );
        final Date weekAfterNextWeek = new Date(today.getTime() + 14 * oneDayMilis );
        
        Episode episode5 = new Episode("5", "1", 5, 1);
        episode5.setFirstAired(tomorrow);
        Episode episode6 = new Episode("6", "1", 6, 1);
        episode6.setFirstAired(dayAfterTomorrow);
        Episode episode7 = new Episode("7", "1", 7, 1);
        episode7.setFirstAired(nextWeek);
        Episode episode8 = new Episode("8", "1", 8, 1);
        episode8.setFirstAired(weekAfterNextWeek);
        

        nextEpisodes.add(episode7);
        this.season.addEpisode(episode7);
        Assert.assertEquals(nextEpisodes, this.season.getNextEpisodesToAir());
        
        nextEpisodes.clear();
        nextEpisodes.add(episode6);
        nextEpisodes.add(episode7);
        this.season.addEpisode(episode6);
        Assert.assertEquals(nextEpisodes, this.season.getNextEpisodesToAir());

        nextEpisodes.add(episode8);
        this.season.addEpisode(episode8);
        Assert.assertEquals(nextEpisodes, this.season.getNextEpisodesToAir());
        
        nextEpisodes.clear();
        nextEpisodes.add(episode5);
        nextEpisodes.add(episode6);
        nextEpisodes.add(episode7);
        nextEpisodes.add(episode8);
        this.season.addEpisode(episode5);
        Assert.assertEquals(nextEpisodes, this.season.getNextEpisodesToAir());
    }
    
    @Test
    public final void testMergeWith() {
        //this.episode1 = new Episode("1", "1", 1, 1);
        //this.episode2 = new Episode("2", "1", 2, 1);
        //this.episode3 = new Episode("3", "1", 3, 1);
        //this.episode4 = new Episode("4", "1", 4, 1);
        Episode episode5 = new Episode("5", "1", 5, 1);
        
        Season newSeason = new Season(this.episode1.seriesId(), this.episode1.seasonNumber());
        newSeason.addEpisode(this.episode1);
        newSeason.addEpisode(this.episode2);
        newSeason.addEpisode(this.episode4);
        newSeason.addEpisode(episode5);
        
        Assert.assertFalse(this.season.has(episode5));
        this.season.mergeWith(newSeason);
        Assert.assertTrue(this.season.has(episode5));
        Assert.assertFalse(newSeason.has(episode3));

        Episode episode6 = Mockito.mock(Episode.class);
        Mockito.when(episode6.id()).thenReturn("6");
        Mockito.when(episode6.seriesId()).thenReturn("1");
        Mockito.when(episode6.number()).thenReturn(6);
        Mockito.when(episode6.seasonNumber()).thenReturn(1);

        Episode episode6Copy = Mockito.mock(Episode.class);
        Mockito.when(episode6Copy.id()).thenReturn("6");
        Mockito.when(episode6Copy.seriesId()).thenReturn("1");
        Mockito.when(episode6Copy.number()).thenReturn(6);
        Mockito.when(episode6Copy.seasonNumber()).thenReturn(1);
        
        this.season.addEpisode(episode6);
        newSeason.addEpisode(episode6Copy);
        
        
        //TODO: Fix this
        //Assert.assertEquals(episode6, episode6Copy);
        
        //Mockito.verify(episode6, Mockito.times(1)).mergeWith(episode6Copy);
        this.season.mergeWith(newSeason);
        
    }

    @Test(expected = InvalidParameterException.class)
    public final void testMergeWithNull() {
        this.season.mergeWith(null);
    }
    
    @Test
    public final void testAddListener() {
        DomainObjectListener<Season> listener1 = Mockito.mock(DomainObjectListener.class);
        DomainObjectListener<Season> listener2 = Mockito.mock(DomainObjectListener.class);
        
        this.season.addListener(listener1);
        
        Mockito.verify(listener1, Mockito.times(0)).onUpdate(this.season);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(this.season);
        
        this.episode1.markAsSeen();
        
        Mockito.verify(listener1, Mockito.times(1)).onUpdate(this.season);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(this.season);
        
        Assert.assertTrue(this.season.addListener(listener2));
        Assert.assertFalse(this.season.addListener(listener1));
        Assert.assertFalse(this.season.addListener(listener2));
        
        this.episode2.markAsSeen();
        
        Mockito.verify(listener1, Mockito.times(2)).onUpdate(this.season);
        Mockito.verify(listener2, Mockito.times(1)).onUpdate(this.season);
    }
    
    @Test
    public final void testRemoveListener() {
        DomainObjectListener<Season> listener1 = Mockito.mock(DomainObjectListener.class);
        DomainObjectListener<Season> listener2 = Mockito.mock(DomainObjectListener.class);
        
        Assert.assertTrue(this.season.addListener(listener1));
        Assert.assertTrue(this.season.addListener(listener2));
        
        Mockito.verify(listener1, Mockito.times(0)).onUpdate(this.season);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(this.season);
        
        this.episode1.markAsSeen();
        
        Mockito.verify(listener1, Mockito.times(1)).onUpdate(this.season);
        Mockito.verify(listener2, Mockito.times(1)).onUpdate(this.season);
        
        Assert.assertTrue(this.season.removeListener(listener1));
        this.episode2.markAsSeen();
        
        Mockito.verify(listener1, Mockito.times(1)).onUpdate(this.season);
        Mockito.verify(listener2, Mockito.times(2)).onUpdate(this.season);

        Assert.assertFalse(this.season.removeListener(listener1));
        
        Assert.assertTrue(this.season.removeListener(listener2));
        
        this.episode2.markAsSeen();
        
        Mockito.verify(listener1, Mockito.times(1)).onUpdate(this.season);
        Mockito.verify(listener2, Mockito.times(2)).onUpdate(this.season);
    }    
}
