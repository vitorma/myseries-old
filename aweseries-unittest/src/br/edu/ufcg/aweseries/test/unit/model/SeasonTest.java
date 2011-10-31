package br.edu.ufcg.aweseries.test.unit.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;

public class SeasonTest {

    //TODO Implement tests for all methods but using mocks for episodes

    private Season season;
    private Episode episode1;
    private Episode episode2;

    @Ignore
    @Before
    public void setUp() throws Exception {
        this.season = new Season(1);
        this.episode1 = new Episode("1", "1", 1, 1);
        this.episode2 = new Episode("2", "2", 2, 2);
        this.season.addEpisode(this.episode1);
        this.season.addEpisode(this.episode2);
    }

    @Ignore
    @Test
    public final void testAddEpisode() {
        Assert.assertThat(this.season.getEpisodes(),
                JUnitMatchers.hasItems(this.episode1, this.episode2));
        Assert.assertEquals(this.season.getNumberOfEpisodes(), 2);

        final Episode episode3 = new Episode("3", "3", 3, 3);
        this.season.addEpisode(episode3);
        Assert.assertEquals(this.season.getNumberOfEpisodes(), 3);
        Assert.assertThat(this.season.getEpisodes(), JUnitMatchers.hasItem(episode3));

        this.season.addEpisode(this.episode2);
        Assert.assertEquals(this.season.getNumberOfEpisodes(), 3);
    }
}
