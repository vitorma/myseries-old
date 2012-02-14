/*
 *   SeasonSetTest.java
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
import org.junit.matchers.JUnitMatchers;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.SeasonSet;
import br.edu.ufcg.aweseries.model.SeasonSetListener;

public class SeasonSetTest {
    private SeasonSet seasonSet;
    private Episode episode1;
    private Episode episode2;
    private Episode episode3;

    @Before
    public void setUp() throws Exception {
        this.seasonSet = new SeasonSet(1);
        this.episode1 = Mockito.mock(Episode.class);
        this.episode2 = Mockito.mock(Episode.class);
        this.episode3 = Mockito.mock(Episode.class);
        
        Mockito.when(this.episode1.id()).thenReturn(123811);
        Mockito.when(this.episode1.seriesId()).thenReturn(1);
        Mockito.when(this.episode1.number()).thenReturn(1);
        Mockito.when(this.episode1.seasonNumber()).thenReturn(1);

        Mockito.when(this.episode2.id()).thenReturn(141231);
        Mockito.when(this.episode2.seriesId()).thenReturn(1);
        Mockito.when(this.episode2.number()).thenReturn(2);
        Mockito.when(this.episode2.seasonNumber()).thenReturn(1);

        Mockito.when(this.episode3.id()).thenReturn(948241);
        Mockito.when(this.episode3.seriesId()).thenReturn(1);
        Mockito.when(this.episode3.number()).thenReturn(1);
        Mockito.when(this.episode3.seasonNumber()).thenReturn(2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testAddDuplicatedEpisode() {
        this.seasonSet.including(this.episode1);
        this.seasonSet.including(this.episode1);
    }

    @Test
    public final void testAddEpisode() {
        this.seasonSet.including(this.episode1);
        this.seasonSet.including(this.episode2);
        this.seasonSet.including(this.episode3);

        for (final Season season : this.seasonSet.seasons()) {
            if (season.number() == 1) {
                Assert.assertThat(season.episodes(), JUnitMatchers.hasItems(this.episode1, this.episode2));
                Assert.assertEquals(2, season.episodes().size());
            } else if (season.number() == 2) {
                Assert.assertThat(season.episodes(), JUnitMatchers.hasItem(this.episode3));
                Assert.assertEquals(1, season.episodes().size());
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testAddNullEpisode() {
        this.seasonSet.including(null);
    }

    @Test
    public final void testSeasonSet() {
        Assert.assertNotNull(this.seasonSet.seasons());
        Assert.assertEquals(0, this.seasonSet.seasons().size());
    }
        
    //TODO: Remove me, test register()
    @Test
    public void testAddListener() {
        SeasonSet seasonSet = new SeasonSet(1);
        
        SeasonSetListener listener1 = Mockito.mock(SeasonSetListener.class);
        SeasonSetListener listener2 = Mockito.mock(SeasonSetListener.class);

        Mockito.verify(listener1, Mockito.times(0)).onChangeNextEpisodeToSee(this.seasonSet);
        Mockito.verify(listener2, Mockito.times(0)).onChangeNextEpisodeToSee(this.seasonSet);       

        Assert.assertTrue(seasonSet.register(listener1));
        Assert.assertFalse(seasonSet.register(listener1));

        Episode episode2 = Mockito.mock(Episode.class);
        Mockito.when(episode2.id()).thenReturn(123814);
        Mockito.when(episode2.seriesId()).thenReturn(1);
        Mockito.when(episode2.number()).thenReturn(2);
        Mockito.when(episode2.seasonNumber()).thenReturn(1);        
        seasonSet.including(episode2);
        
//        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
//        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);       

        Assert.assertTrue(seasonSet.register(listener2));
        
        Episode episode1 = Mockito.mock(Episode.class);
        Mockito.when(episode1.id()).thenReturn(123810);
        Mockito.when(episode1.seriesId()).thenReturn(1);
        Mockito.when(episode1.number()).thenReturn(1);
        Mockito.when(episode1.seasonNumber()).thenReturn(1);        

        seasonSet.including(episode1);
        
//        Mockito.verify(listener1, Mockito.times(2)).onUpdate(seasonSet);
//        Mockito.verify(listener2, Mockito.times(1)).onUpdate(seasonSet);
        
    }
    
    
    //TODO: Remove me, test deregister()
    @Test
    public void testRemoveListener() {
        SeasonSet seasonSet = new SeasonSet(1);
        
        SeasonSetListener listener1 = Mockito.mock(SeasonSetListener.class);
        SeasonSetListener listener2 = Mockito.mock(SeasonSetListener.class);

        Mockito.verify(listener1, Mockito.times(0)).onChangeNextEpisodeToSee(this.seasonSet);
        Mockito.verify(listener2, Mockito.times(0)).onChangeNextEpisodeToSee(this.seasonSet);       

        Assert.assertTrue(seasonSet.register(listener1));
        Assert.assertTrue(seasonSet.register(listener2));
        Assert.assertFalse(seasonSet.register(listener2));
        
        Episode episode2 = Mockito.mock(Episode.class);
        Mockito.when(episode2.id()).thenReturn(123814);
        Mockito.when(episode2.seriesId()).thenReturn(1);
        Mockito.when(episode2.number()).thenReturn(2);
        Mockito.when(episode2.seasonNumber()).thenReturn(1);        
        seasonSet.including(episode2);
                
//        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
//        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);
        
        Assert.assertTrue(seasonSet.deregister(listener1));
        Assert.assertFalse(seasonSet.deregister(listener1));
        
        Episode episode1 = Mockito.mock(Episode.class);
        Mockito.when(episode1.id()).thenReturn(123810);
        Mockito.when(episode1.seriesId()).thenReturn(1);
        Mockito.when(episode1.number()).thenReturn(1);
        Mockito.when(episode1.seasonNumber()).thenReturn(1);        

        seasonSet.including(episode1);
        
//        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
//        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);

    }
    
}
