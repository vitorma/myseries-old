package br.edu.ufcg.aweseries.test.unit.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.DomainEntityListener;
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
        this.seasons = new SeasonSet("1");
        this.episode1 = Mockito.mock(Episode.class);
        this.episode2 = Mockito.mock(Episode.class);
        this.episode3 = Mockito.mock(Episode.class);
        
        Mockito.when(this.episode1.getId()).thenReturn("123811");
        Mockito.when(this.episode1.getSeriesId()).thenReturn("1");
        Mockito.when(this.episode1.getNumber()).thenReturn(1);
        Mockito.when(this.episode1.getSeasonNumber()).thenReturn(1);

        Mockito.when(this.episode2.getId()).thenReturn("141231");
        Mockito.when(this.episode2.getSeriesId()).thenReturn("1");
        Mockito.when(this.episode2.getNumber()).thenReturn(2);
        Mockito.when(this.episode2.getSeasonNumber()).thenReturn(1);

        Mockito.when(this.episode3.getId()).thenReturn("948241");
        Mockito.when(this.episode3.getSeriesId()).thenReturn("1");
        Mockito.when(this.episode3.getNumber()).thenReturn(1);
        Mockito.when(this.episode3.getSeasonNumber()).thenReturn(2);
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
            if (season.getNumber() == 1) {
                Assert.assertThat(season.getEpisodes(), JUnitMatchers.hasItems(this.episode1, this.episode2));
                Assert.assertEquals(2, season.getEpisodes().size());
            } else if (season.getNumber() == 2) {
                Assert.assertThat(season.getEpisodes(), JUnitMatchers.hasItem(this.episode3));
                Assert.assertEquals(1, season.getEpisodes().size());
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
        
    @Test
    public void testAddListener() {
        SeasonSet seasonSet = new SeasonSet("1");
        
        DomainEntityListener<SeasonSet> listener1 = Mockito.mock(DomainEntityListener.class);
        DomainEntityListener<SeasonSet> listener2 = Mockito.mock(DomainEntityListener.class);

        Mockito.verify(listener1, Mockito.times(0)).onUpdate(this.seasons);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(this.seasons);       

        Assert.assertTrue(seasonSet.addListener(listener1));
        Assert.assertFalse(seasonSet.addListener(listener1));

        Episode episode2 = Mockito.mock(Episode.class);
        Mockito.when(episode2.getId()).thenReturn("123814");
        Mockito.when(episode2.getSeriesId()).thenReturn("1");
        Mockito.when(episode2.getNumber()).thenReturn(2);
        Mockito.when(episode2.getSeasonNumber()).thenReturn(1);        
        seasonSet.addEpisode(episode2);
        
        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);       

        Assert.assertTrue(seasonSet.addListener(listener2));
        
        Episode episode1 = Mockito.mock(Episode.class);
        Mockito.when(episode1.getId()).thenReturn("123810");
        Mockito.when(episode1.getSeriesId()).thenReturn("1");
        Mockito.when(episode1.getNumber()).thenReturn(1);
        Mockito.when(episode1.getSeasonNumber()).thenReturn(1);        

        seasonSet.addEpisode(episode1);
        
        Mockito.verify(listener1, Mockito.times(2)).onUpdate(seasonSet);
        Mockito.verify(listener2, Mockito.times(1)).onUpdate(seasonSet);
        
    }
    
    @Test
    public void testRemoveListener() {
        SeasonSet seasonSet = new SeasonSet("1");
        
        DomainEntityListener<SeasonSet> listener1 = Mockito.mock(DomainEntityListener.class);
        DomainEntityListener<SeasonSet> listener2 = Mockito.mock(DomainEntityListener.class);

        Mockito.verify(listener1, Mockito.times(0)).onUpdate(this.seasons);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(this.seasons);       

        Assert.assertTrue(seasonSet.addListener(listener1));
        Assert.assertTrue(seasonSet.addListener(listener2));
        Assert.assertTrue(seasonSet.removeListener(listener2));
        
        Episode episode2 = Mockito.mock(Episode.class);
        Mockito.when(episode2.getId()).thenReturn("123814");
        Mockito.when(episode2.getSeriesId()).thenReturn("1");
        Mockito.when(episode2.getNumber()).thenReturn(2);
        Mockito.when(episode2.getSeasonNumber()).thenReturn(1);        
        seasonSet.addEpisode(episode2);
        
        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);
        
        Assert.assertTrue(seasonSet.removeListener(listener1));
        Assert.assertFalse(seasonSet.removeListener(listener1));
        
        Episode episode1 = Mockito.mock(Episode.class);
        Mockito.when(episode1.getId()).thenReturn("123810");
        Mockito.when(episode1.getSeriesId()).thenReturn("1");
        Mockito.when(episode1.getNumber()).thenReturn(1);
        Mockito.when(episode1.getSeasonNumber()).thenReturn(1);        

        seasonSet.addEpisode(episode1);
        
        Mockito.verify(listener1, Mockito.times(1)).onUpdate(seasonSet);
        Mockito.verify(listener2, Mockito.times(0)).onUpdate(seasonSet);

    }
    
}
