package br.edu.ufcg.aweseries.test.unit.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;

/**
 * Unit tests for the class {@link Season}.
 * 
 */
public class SeasonTest {

    //TODO Reimplements all tests using mocks to episodes

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

    @Ignore
    @Test
    public final void testGetEpisodeAt() {
        Assert.assertEquals(this.season.getEpisodeAt(0), this.episode1);
        Assert.assertEquals(this.season.getEpisodeAt(1), this.episode2);
        final Episode episode3 = new Episode("3", "3", 3, 3);
        this.season.addEpisode(episode3);
        Assert.assertEquals(this.season.getEpisodeAt(2), episode3);
    }

    @Ignore
    @Test
    public final void testGetNextEpisode() {
        Assert.assertEquals(this.episode1, this.season.getNextEpisode());
        this.season.markAsViewed(0);
        Assert.assertEquals(this.episode2, this.season.getNextEpisode());
        this.season.markAsViewed(1);
        Assert.assertEquals(null, this.season.getNextEpisode());
        this.season.markAsNotViewed(0);
        Assert.assertEquals(this.episode1, this.season.getNextEpisode());
        this.season.markAsViewed(0);
        Assert.assertEquals(null, this.season.getNextEpisode());
    }

    @Ignore
    @Test
    public final void testGetNumberOfEpisodes() {
        Assert.assertEquals(2, this.season.getNumberOfEpisodes());
        final Episode episode3 = new Episode("3", "3", 3, 3);
        this.season.addEpisode(episode3);
        Assert.assertEquals(3, this.season.getNumberOfEpisodes());
    }

    @Ignore
    @Test
    public final void testIndexOf() {
        Assert.assertEquals(0, this.season.indexOf(this.episode1));
        Assert.assertEquals(1, this.season.indexOf(this.episode2));
        Assert.assertEquals(-1, this.season.indexOf(new Episode("3", "3", 3, 3)));
    }

    @Ignore
    @Test
    public final void testIsViewed() {
        Assert.assertFalse(this.season.isViewed(0));
        Assert.assertFalse(this.season.isViewed(1));
        this.season.markAsViewed(0);
        Assert.assertTrue(this.season.isViewed(0));
        Assert.assertFalse(this.season.isViewed(1));
        this.season.markAsViewed(1);
        Assert.assertTrue(this.season.isViewed(0));
        Assert.assertTrue(this.season.isViewed(1));
    }

    @Ignore
    @Test
    public final void testMarkAllAsNotViewed() {
        this.season.markAsViewed(0);
        this.season.markAsViewed(1);
        Assert.assertTrue(this.season.isViewed(0));
        Assert.assertTrue(this.season.isViewed(1));
        this.season.markAllAsNotViewed();
        Assert.assertFalse(this.season.isViewed(0));
        Assert.assertFalse(this.season.isViewed(1));
    }

    @Ignore
    @Test
    public final void testMarkAllAsViewed() {
        Assert.assertFalse(this.season.isViewed(0));
        Assert.assertFalse(this.season.isViewed(1));
        this.season.markAllAsViewed();
        Assert.assertTrue(this.season.isViewed(0));
        Assert.assertTrue(this.season.isViewed(1));
    }

    @Ignore
    @Test
    public final void testMarkAsNotViewed() {
        this.season.markAllAsViewed();
        Assert.assertTrue(this.season.isViewed(0));
        Assert.assertTrue(this.season.isViewed(1));
        this.season.markAsNotViewed(0);
        Assert.assertFalse(this.season.isViewed(0));
        Assert.assertTrue(this.season.isViewed(1));
        this.season.markAsNotViewed(1);
        Assert.assertFalse(this.season.isViewed(0));
        Assert.assertFalse(this.season.isViewed(1));
    }

    @Ignore
    @Test
    public final void testMarkAsViewed() {
        Assert.assertFalse(this.season.isViewed(0));
        Assert.assertFalse(this.season.isViewed(1));
        this.season.markAllAsViewed();
        Assert.assertTrue(this.season.isViewed(0));
        Assert.assertTrue(this.season.isViewed(1));
    }
}
