package br.edu.ufcg.aweseries.test.unit.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Seasons;

public class SeasonsTest {

    private Seasons seasons;
    private Episode episode1;
    private Episode episode2;
    private Episode episode3;

    @Before
    public void setUp() throws Exception {
        this.seasons = new Seasons();
        this.episode1 = Mockito.mock(Episode.class);
        this.episode2 = Mockito.mock(Episode.class);
        this.episode3 = Mockito.mock(Episode.class);

        Mockito.when(this.episode1.getId()).thenReturn("123811");
        Mockito.when(this.episode1.getNumber()).thenReturn(1);
        Mockito.when(this.episode1.getSeasonNumber()).thenReturn(1);

        Mockito.when(this.episode2.getId()).thenReturn("141231");
        Mockito.when(this.episode2.getNumber()).thenReturn(2);
        Mockito.when(this.episode2.getSeasonNumber()).thenReturn(1);

        Mockito.when(this.episode3.getId()).thenReturn("948241");
        Mockito.when(this.episode3.getNumber()).thenReturn(1);
        Mockito.when(this.episode3.getSeasonNumber()).thenReturn(2);
    }

    
    @Test(expected = IllegalArgumentException.class)
    public final void testAddDuplicatedEpisode() {
        this.seasons.addEpisode(this.episode1);
        this.seasons.addEpisode(this.episode1);
    }

    @Ignore //XXX: Implement equals and hashcode for Mocks
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
    public final void testSeasons() {
        Assert.assertNotNull(this.seasons.toArray());
        Assert.assertEquals(0, this.seasons.toArray().length);
    }
}
