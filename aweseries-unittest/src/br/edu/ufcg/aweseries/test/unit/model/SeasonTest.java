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
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;

public class SeasonTest {

    private Season season;
    private Episode episode2;
    private Episode episode1;
    private Episode episode3;
    private Episode episode4;

    //Auxiliar----------------------------------------------------------------------------------------------------------

    private Episode mockEpisode(String id, String seriesId, int number, int seasonNumber) {
        Episode episode = Mockito.mock(Episode.class);

        Mockito.when(episode.id()).thenReturn(id);
        Mockito.when(episode.seriesId()).thenReturn(seriesId);
        Mockito.when(episode.number()).thenReturn(number);
        Mockito.when(episode.seasonNumber()).thenReturn(seasonNumber);
        
        return episode;
    }

    private void makeAllLookNotSeen(Episode... episodes) {
        for (Episode episode : episodes) {
            Mockito.when(episode.wasSeen()).thenReturn(false);
        }
    }
    
    private void makeAllLookSeen(Episode... episodes) {
        for (Episode episode : episodes) {
            Mockito.when(episode.wasSeen()).thenReturn(true);
        }
    }

    private void callOnMarkAsSeenForAll(Episode... episodes) {
        for (Episode episode : episodes) {
            this.season.onMarkedAsSeen(episode);
        }
    }
    
    private void callOnMarkAsNotSeenForAll(Episode... episodes) {
        for (Episode episode : episodes) {
            this.season.onMarkedAsNotSeen(episode);
        }
    }
    
    //Tests-------------------------------------------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        this.season = new Season("1", 1);

        this.episode1 = mockEpisode("1", "1", 1, 1);
        this.episode2 = mockEpisode("2", "1", 2, 1);
        this.episode3 = mockEpisode("3", "1", 3, 1);
        this.episode4 = mockEpisode("4", "1", 4, 1);
        
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
        this.season.addEpisode(mockEpisode("3", "3", 3, 1));
    }

    @Test(expected=IllegalArgumentException.class)
    public final void testAddEpisodeWithAnotherSeasonNumber() {
        this.season.addEpisode(mockEpisode("3", "1", 3, 2));
    }

    @Test(expected=IllegalArgumentException.class)
    public final void testAddAlreadyExistentEpisode() {
        Episode episode2Copy = mockEpisode("2", "1", 2, 1);
        
        this.season.addEpisode(episode2);
        this.season.addEpisode(episode2Copy);
    }

    @Test
    public final void testAddEpisode() {
        Assert.assertThat(this.season.episodes(),
                JUnitMatchers.hasItems(this.episode1, this.episode2));
        Assert.assertEquals(this.season.numberOfEpisodes(), 4);

        final Episode episode5 = mockEpisode("5", "1", 5, 1);
        this.season.addEpisode(episode5);
        Assert.assertEquals(this.season.numberOfEpisodes(), 5);
        Assert.assertThat(this.season.episodes(), JUnitMatchers.hasItem(episode5));
    }

    @Test
    public final void testMarkAllAsSeen() {                
        this.season.markAllAsSeen();
        
        this.makeAllLookSeen(episode1, episode2, episode3, episode4);
        this.callOnMarkAsSeenForAll(episode1, episode2, episode3, episode4);
        
        Mockito.verify(this.episode1).markAsSeen();
        Mockito.verify(this.episode2).markAsSeen();
        Mockito.verify(this.episode3).markAsSeen();
        Mockito.verify(this.episode4).markAsSeen();
                
        Assert.assertTrue(this.season.areAllSeen());
        Assert.assertEquals(null, this.season.nextEpisodeToSee());
    }

    @Test
    public final void testMarkAllAsNotSeen() {
        this.season.markAllAsNotSeen();
        this.makeAllLookNotSeen(episode1, episode2, episode3, episode4);
        
        this.callOnMarkAsNotSeenForAll(episode1, episode2, episode3, episode4);
        
        Mockito.verify(this.episode1).markAsNotSeen();
        Mockito.verify(this.episode2).markAsNotSeen();
        Mockito.verify(this.episode3).markAsNotSeen();
        Mockito.verify(this.episode4).markAsNotSeen();
        
        Assert.assertFalse(this.season.areAllSeen());
        Assert.assertEquals(this.episode1, this.season.nextEpisodeToSee());
    }

    @Test
    public final void testGetNextEpisodeToSee() {
        Assert.assertEquals(this.episode1, this.season.nextEpisodeToSee());
        
        this.makeAllLookSeen(episode1, episode2, episode3, episode4);
        
        callOnMarkAsSeenForAll(episode1, episode2, episode3, episode4);

        Assert.assertEquals(null, this.season.nextEpisodeToSee());
          
        
        this.makeAllLookNotSeen(episode1, episode2, episode3, episode4);
        
        callOnMarkAsNotSeenForAll(episode1, episode2, episode3, episode4);
        
        Assert.assertEquals(this.episode1, this.season.nextEpisodeToSee());
        
        this.makeAllLookSeen(episode1, episode2, episode3, episode4);
        
        Mockito.when(this.episode1.wasSeen()).thenReturn(false);
        this.season.onMarkedAsNotSeen(episode1);
        Assert.assertEquals(this.episode1, this.season.nextEpisodeToSee());
        
        Mockito.when(this.episode1.wasSeen()).thenReturn(true);
        this.season.onMarkedAsSeen(episode1);
        Assert.assertEquals(null, this.season.nextEpisodeToSee());
        
        Mockito.when(this.episode3.wasSeen()).thenReturn(false);
        this.season.onMarkedAsNotSeen(episode3);
        Assert.assertEquals(this.episode3, this.season.nextEpisodeToSee());
        
        Mockito.when(this.episode3.wasSeen()).thenReturn(true);
        this.season.onMarkedAsSeen(episode3);
        Assert.assertEquals(null, this.season.nextEpisodeToSee());

        Mockito.when(this.episode2.wasSeen()).thenReturn(false);
        this.season.onMarkedAsNotSeen(episode2);
        Assert.assertEquals(this.episode2, this.season.nextEpisodeToSee());
        
        Mockito.when(this.episode4.wasSeen()).thenReturn(false);
        this.season.onMarkedAsNotSeen(episode4);
        Assert.assertEquals(this.episode2, this.season.nextEpisodeToSee());
        
        Mockito.when(this.episode1.wasSeen()).thenReturn(false);
        this.season.onMarkedAsNotSeen(episode1);
        Assert.assertEquals(this.episode1, this.season.nextEpisodeToSee());
        
        Mockito.when(this.episode2.wasSeen()).thenReturn(true);
        this.season.onMarkedAsSeen(episode2);
        Assert.assertEquals(this.episode1, this.season.nextEpisodeToSee());
        
        Mockito.when(this.episode1.wasSeen()).thenReturn(true);
        this.season.onMarkedAsSeen(episode1);
        Assert.assertEquals(this.episode4, this.season.nextEpisodeToSee());
        }
    
    @Test
    public final void testGetNextEpisodeToAir() {
        Assert.assertEquals(null, this.season.nextEpisodeToAir());
        final Date today = new Date();
        final long oneDayMilis = 1000 * 60 * 60 * 24;

        final Date tomorrow = new Date(today.getTime() + oneDayMilis);
        final Date dayAfterTomorrow = new Date(today.getTime() + 2 * oneDayMilis);
        final Date nextWeek = new Date(today.getTime() + 7 * oneDayMilis);
        final Date weekAfterNextWeek = new Date(today.getTime() + 14 * oneDayMilis);
        
        Episode episode5 = mockEpisode("5", "1", 5, 1);
        Mockito.when(episode5.airdate()).thenReturn(tomorrow);
        
        Episode episode6 = mockEpisode("6", "1", 6, 1);
        Mockito.when(episode6.airdate()).thenReturn(dayAfterTomorrow);
        
        Episode episode7 = mockEpisode("7", "1", 7, 1);
        Mockito.when(episode7.airdate()).thenReturn(nextWeek);
        
        Episode episode8 = mockEpisode("8", "1", 8, 1);
        Mockito.when(episode8.airdate()).thenReturn(weekAfterNextWeek);
        
        this.season.addEpisode(episode7);
        Assert.assertEquals(episode7, this.season.nextEpisodeToAir());

        this.season.addEpisode(episode6);
        Assert.assertEquals(episode6, this.season.nextEpisodeToAir());

        this.season.addEpisode(episode8);
        Assert.assertEquals(episode6, this.season.nextEpisodeToAir());

        this.season.addEpisode(episode5);
        Assert.assertEquals(episode5, this.season.nextEpisodeToAir());
    }
   
    @Test
    public final void testGetLastAiredEpisode() {
        Assert.assertEquals(null , this.season.lastAiredEpisode());
        
        final Date today = new Date();
        final long oneDayMilis = 1000 * 60 * 60 * 24;

        final Date tenSecondsAgo =  new Date(today.getTime() - 1000 * 60);
        final Date yesterday =  new Date(today.getTime() - oneDayMilis);
        final Date dayBeforeYesterday =  new Date(today.getTime() - 2 * oneDayMilis);
        final Date lastWeek =  new Date(today.getTime() - 7 * oneDayMilis);
        final Date weekBeforeLast =  new Date(today.getTime() - 14 * oneDayMilis);

        Episode episode5 = mockEpisode("5", "1", 5, 1);
        Mockito.when(episode5.airdate()).thenReturn(weekBeforeLast);
        Episode episode6 = mockEpisode("6", "1", 6, 1);
        Mockito.when(episode6.airdate()).thenReturn(lastWeek);
        Episode episode7 = mockEpisode("7", "1", 7, 1);
        Mockito.when(episode7.airdate()).thenReturn(dayBeforeYesterday);
        Episode episode8 = mockEpisode("8", "1", 8, 1);
        Mockito.when(episode8.airdate()).thenReturn(yesterday);
        Episode episode9 = mockEpisode("9", "1", 9, 1);
        Mockito.when(episode9.airdate()).thenReturn(tenSecondsAgo);
        
        this.season.addEpisode(episode6);
        Assert.assertEquals(episode6, this.season.lastAiredEpisode());

        this.season.addEpisode(episode5);
        Assert.assertEquals(episode6, this.season.lastAiredEpisode());
        
        this.season.addEpisode(episode7);
        Assert.assertEquals(episode7, this.season.lastAiredEpisode());

        this.season.addEpisode(episode9);
        Assert.assertEquals(episode9, this.season.lastAiredEpisode());

        this.season.addEpisode(episode8);
        Assert.assertEquals(episode9, this.season.lastAiredEpisode());
    }

    @Test
    public final void testGetLastAiredNotSeenEpisode() {
        final List<Episode> lastNotSeenEpisodes = new ArrayList<Episode>();

        Assert.assertEquals(lastNotSeenEpisodes , this.season.lastAiredNotSeenEpisodes());
        
        final Date today = new Date();
        final long oneDayMilis = 1000 * 60 * 60 * 24;

        final Date tenSecondsAgo =  new Date(today.getTime() - 1000 * 60);
        final Date yesterday =  new Date(today.getTime() - oneDayMilis);
        final Date dayBeforeYesterday =  new Date(today.getTime() - 2 * oneDayMilis);
        final Date lastWeek =  new Date(today.getTime() - 7 * oneDayMilis);
        final Date weekBeforeLast =  new Date(today.getTime() - 14 * oneDayMilis);

        Episode episode5 = mockEpisode("5", "1", 5, 1);
        Mockito.when(episode5.airdate()).thenReturn(weekBeforeLast);
        Episode episode6 = mockEpisode("6", "1", 6, 1);
        Mockito.when(episode6.airdate()).thenReturn(lastWeek);
        Episode episode7 = mockEpisode("7", "1", 7, 1);
        Mockito.when(episode7.airdate()).thenReturn(dayBeforeYesterday);
        Episode episode8 = mockEpisode("8", "1", 8, 1);
        Mockito.when(episode8.airdate()).thenReturn(yesterday);
        Episode episode9 = mockEpisode("9", "1", 9, 1);
        Mockito.when(episode9.airdate()).thenReturn(tenSecondsAgo);
        
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
        
        Assert.assertEquals(lastNotSeenEpisodes, this.season.lastAiredNotSeenEpisodes());
        
        Mockito.when(episode9.wasSeen()).thenReturn(true);
        lastNotSeenEpisodes.remove(episode9);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.lastAiredNotSeenEpisodes());
        
        Mockito.when(episode7.wasSeen()).thenReturn(true);
        lastNotSeenEpisodes.remove(episode7);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.lastAiredNotSeenEpisodes());
        
        Mockito.when(episode8.wasSeen()).thenReturn(true);
        Mockito.when(episode6.wasSeen()).thenReturn(true);
        Mockito.when(episode5.wasSeen()).thenReturn(true);
        lastNotSeenEpisodes.clear();
        Assert.assertEquals(lastNotSeenEpisodes, this.season.lastAiredNotSeenEpisodes());
        
        Mockito.when(episode8.wasSeen()).thenReturn(false);
        lastNotSeenEpisodes.add(episode8);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.lastAiredNotSeenEpisodes());

        lastNotSeenEpisodes.clear();
        Mockito.when(episode5.wasSeen()).thenReturn(false);

        lastNotSeenEpisodes.add(episode5);
        lastNotSeenEpisodes.add(episode8);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.lastAiredNotSeenEpisodes());
        
        Mockito.when(episode9.wasSeen()).thenReturn(false);
        lastNotSeenEpisodes.add(episode9);
        Assert.assertEquals(lastNotSeenEpisodes, this.season.lastAiredNotSeenEpisodes());
    }
    
    @Test
    public final void testGetNextEpisodesToAir() {
        final List<Episode> nextEpisodes = new ArrayList<Episode>();
        
        Assert.assertEquals(nextEpisodes, this.season.nextEpisodesToAir());
        
        final Date today = new Date();
        final long oneDayMilis = 1000 * 60 * 60 * 24;

        final Date tomorrow = new Date(today.getTime() + oneDayMilis );
        final Date dayAfterTomorrow = new Date(today.getTime() + 2 * oneDayMilis );
        final Date nextWeek = new Date(today.getTime() + 7 * oneDayMilis );
        final Date weekAfterNextWeek = new Date(today.getTime() + 14 * oneDayMilis );
        
        Episode episode5 = mockEpisode("5", "1", 5, 1);
        Mockito.when(episode5.airdate()).thenReturn(tomorrow);
        Episode episode6 = mockEpisode("6", "1", 6, 1);
        Mockito.when(episode6.airdate()).thenReturn(dayAfterTomorrow);
        Episode episode7 = mockEpisode("7", "1", 7, 1);
        Mockito.when(episode7.airdate()).thenReturn(nextWeek);
        Episode episode8 = mockEpisode("8", "1", 8, 1);
        Mockito.when(episode8.airdate()).thenReturn(weekAfterNextWeek);
        

        nextEpisodes.add(episode7);
        this.season.addEpisode(episode7);
        Assert.assertEquals(nextEpisodes, this.season.nextEpisodesToAir());
        
        nextEpisodes.clear();
        nextEpisodes.add(episode6);
        nextEpisodes.add(episode7);
        this.season.addEpisode(episode6);
        Assert.assertEquals(nextEpisodes, this.season.nextEpisodesToAir());

        nextEpisodes.add(episode8);
        this.season.addEpisode(episode8);
        Assert.assertEquals(nextEpisodes, this.season.nextEpisodesToAir());
        
        nextEpisodes.clear();
        nextEpisodes.add(episode5);
        nextEpisodes.add(episode6);
        nextEpisodes.add(episode7);
        nextEpisodes.add(episode8);
        this.season.addEpisode(episode5);
        Assert.assertEquals(nextEpisodes, this.season.nextEpisodesToAir());
    }
    
    @Test(expected = InvalidParameterException.class)
    public final void testMergeWithNull() {
    	this.season.mergeWith(null);
    }
    
    @Test
    public final void testMergeWith() {
        Episode episode5 = mockEpisode("5", "1", 5, 1);
        
        Season newSeason = new Season(this.episode1.seriesId(), this.episode1.seasonNumber());
        newSeason.addEpisode(this.episode1);
        newSeason.addEpisode(this.episode2);
        newSeason.addEpisode(this.episode4);
        newSeason.addEpisode(episode5);
        
        Assert.assertFalse(this.season.has(episode5));
        this.season.mergeWith(newSeason);
        Assert.assertTrue(this.season.has(episode5));
        Assert.assertFalse(newSeason.has(episode3));

        Episode episode6 = mockEpisode("6", "1", 6, 1);

        Episode episode6Copy = mockEpisode("6", "1", 6, 1);
        
        this.season.addEpisode(episode6);
        newSeason.addEpisode(episode6Copy);        
    }
}
