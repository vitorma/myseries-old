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

import br.edu.ufcg.aweseries.model.DomainObjectListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.SeasonSet;

public class SeasonSetTest {
    private SeasonSet seasons;
    private Episode episode1;
    private Episode episode2;
    private Episode episode3;

    @Before
    public void setUp() throws Exception {
        this.seasons = new SeasonSet(1);
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
        this.seasons.addEpisode(this.episode1);
        this.seasons.addEpisode(this.episode1);
    }

    @Test
    public final void testAddEpisode() {
        this.seasons.addEpisode(this.episode1);
        this.seasons.addEpisode(this.episode2);
        this.seasons.addEpisode(this.episode3);

        for (final Season season : this.seasons.toArray()) {
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
        this.seasons.addEpisode(null);
    }

    @Test
    public final void testSeasonSet() {
        Assert.assertNotNull(this.seasons.toArray());
        Assert.assertEquals(0, this.seasons.toArray().length);
    }
        
    //TODO: Remove me, test register()
    @Test
    public void testAddListener() {
        SeasonSet seasonSet = new SeasonSet(1);
        
        DomainObjectListener<SeasonSet> listener1 = Mockito.mock(DomainObjectListener.class);
        DomainObjectListener<SeasonSet> listener2 = Mockito.mock(DomainObjectListener.class);

        Mockito.verify(listener1, Mockito.times(0)).onUpdate(this.seasons);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(this.seasons);       

        Assert.assertTrue(seasonSet.addListener(listener1));
        Assert.assertFalse(seasonSet.addListener(listener1));

        Episode episode2 = Mockito.mock(Episode.class);
        Mockito.when(episode2.id()).thenReturn(123814);
        Mockito.when(episode2.seriesId()).thenReturn(1);
        Mockito.when(episode2.number()).thenReturn(2);
        Mockito.when(episode2.seasonNumber()).thenReturn(1);        
        seasonSet.addEpisode(episode2);
        
//        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
//        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);       

        Assert.assertTrue(seasonSet.addListener(listener2));
        
        Episode episode1 = Mockito.mock(Episode.class);
        Mockito.when(episode1.id()).thenReturn(123810);
        Mockito.when(episode1.seriesId()).thenReturn(1);
        Mockito.when(episode1.number()).thenReturn(1);
        Mockito.when(episode1.seasonNumber()).thenReturn(1);        

        seasonSet.addEpisode(episode1);
        
//        Mockito.verify(listener1, Mockito.times(2)).onUpdate(seasonSet);
//        Mockito.verify(listener2, Mockito.times(1)).onUpdate(seasonSet);
        
    }
    
    
    //TODO: Remove me, test deregister()
    @Test
    public void testRemoveListener() {
        SeasonSet seasonSet = new SeasonSet(1);
        
        DomainObjectListener<SeasonSet> listener1 = Mockito.mock(DomainObjectListener.class);
        DomainObjectListener<SeasonSet> listener2 = Mockito.mock(DomainObjectListener.class);

        Mockito.verify(listener1, Mockito.times(0)).onUpdate(this.seasons);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(this.seasons);       

        Assert.assertTrue(seasonSet.addListener(listener1));
        Assert.assertTrue(seasonSet.addListener(listener2));
        Assert.assertTrue(seasonSet.removeListener(listener2));
        
        Episode episode2 = Mockito.mock(Episode.class);
        Mockito.when(episode2.id()).thenReturn(123814);
        Mockito.when(episode2.seriesId()).thenReturn(1);
        Mockito.when(episode2.number()).thenReturn(2);
        Mockito.when(episode2.seasonNumber()).thenReturn(1);        
        seasonSet.addEpisode(episode2);
                
//        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
//        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);
        
        Assert.assertTrue(seasonSet.removeListener(listener1));
        Assert.assertFalse(seasonSet.removeListener(listener1));
        
        Episode episode1 = Mockito.mock(Episode.class);
        Mockito.when(episode1.id()).thenReturn(123810);
        Mockito.when(episode1.seriesId()).thenReturn(1);
        Mockito.when(episode1.number()).thenReturn(1);
        Mockito.when(episode1.seasonNumber()).thenReturn(1);        

        seasonSet.addEpisode(episode1);
        
//        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
//        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);

    }
    
}
