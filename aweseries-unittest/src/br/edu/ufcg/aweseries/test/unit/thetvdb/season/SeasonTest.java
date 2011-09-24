package br.edu.ufcg.aweseries.test.unit.thetvdb.season;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.episode.Episode;
import br.edu.ufcg.aweseries.thetvdb.season.Season;

/**
 * Unit tests for the class {@link Season}.
 * 
 */
public class SeasonTest {

    private Season season;
    private Episode episode1;
    private Episode episode2;

    @Before
    public void setUp() throws Exception {
        this.season = new Season(1);
        this.episode1 = new Episode();
        this.episode2 = new Episode();
        this.season.addEpisode(this.episode1);
        this.season.addEpisode(this.episode2);
    }

    @Test
    public final void testAddEpisode() {
        assertThat(this.season.getEpisodes(),
                   hasItems(this.episode1, this.episode2));
        Assert.assertEquals(this.season.getNumberOfEpisodes(), 2);

        final Episode episode3 = new Episode();
        this.season.addEpisode(episode3);
        Assert.assertEquals(this.season.getNumberOfEpisodes(), 3);
        assertThat(this.season.getEpisodes(), hasItem(episode3));

        this.season.addEpisode(this.episode2);
        Assert.assertEquals(this.season.getNumberOfEpisodes(), 3);
    }

    @Test
    public final void testGetEpisodeAt() {
        Assert.assertEquals(this.season.getEpisodeAt(0), this.episode1);
        Assert.assertEquals(this.season.getEpisodeAt(1), this.episode2);
        final Episode episode3 = new Episode();
        this.season.addEpisode(episode3);
        Assert.assertEquals(this.season.getEpisodeAt(2), episode3);
    }

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

    @Test
    public final void testGetNumberOfEpisodes() {
        Assert.assertEquals(2, this.season.getNumberOfEpisodes());
        final Episode episode3 = new Episode();
        this.season.addEpisode(episode3);
        Assert.assertEquals(3, this.season.getNumberOfEpisodes());
    }

    @Test
    public final void testSetPoster() {
        Assert.assertEquals("", this.season.getPoster());
        this.season.setPoster("Some poster");
        Assert.assertEquals("Some poster", this.season.getPoster());
    }

    @Test
    public final void testIndexOf() {
        Assert.assertEquals(0, this.season.indexOf(episode1));
        Assert.assertEquals(1, this.season.indexOf(episode2));
        Assert.assertEquals(-1, this.season.indexOf(new Episode()));
    }

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

    @Test
    public final void testMarkAllAsViewed() {
        Assert.assertFalse(this.season.isViewed(0));
        Assert.assertFalse(this.season.isViewed(1));
        this.season.markAllAsViewed();
        Assert.assertTrue(this.season.isViewed(0));
        Assert.assertTrue(this.season.isViewed(1));
    }

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

    @Test
    public final void testMarkAsViewed() {
        Assert.assertFalse(this.season.isViewed(0));
        Assert.assertFalse(this.season.isViewed(1));
        this.season.markAllAsViewed();
        Assert.assertTrue(this.season.isViewed(0));
        Assert.assertTrue(this.season.isViewed(1));
    }

    @Test
    public final void testSeasonWithoutPoster() {
        final Season newSeason = new Season(2);
        Assert.assertEquals(2, newSeason.getNumber());
        Assert.assertEquals(0, newSeason.getNumberOfEpisodes());
        Assert.assertTrue(newSeason.getEpisodes().isEmpty());
        Assert.assertEquals("", newSeason.getPoster());
    }
    
    @Test
    public final void testSeasonWithPoster() {
        final Season newSeason = new Season(2, "ThePoster");
        Assert.assertEquals(2, newSeason.getNumber());
        Assert.assertEquals(0, newSeason.getNumberOfEpisodes());
        Assert.assertTrue(newSeason.getEpisodes().isEmpty());
        Assert.assertEquals("ThePoster", newSeason.getPoster());
    }

}
