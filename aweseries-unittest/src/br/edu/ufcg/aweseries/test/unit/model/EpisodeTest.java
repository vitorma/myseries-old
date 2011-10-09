package br.edu.ufcg.aweseries.test.unit.model;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import br.edu.ufcg.aweseries.model.Episode;

public class EpisodeTest {

    private Episode episode1;
    private Episode episode2;
    private Episode episode1Copy;
    private static final String validEpisodeId = "1234";
    private static final String anotherValidEpisodeId = "1235";
    private static final String validSeriesId = "1237";
    private static final String anotherValidSeriesId = "1236";
    private static final int validEpisodeNumber = 1;
    private static final int anotherValidEpisodeNumber = 2;
    private static final int validSeasonNumber = 3;
    private static final int anotherValidSeasonNumber = 4;

    @Before
    public void setUp() throws Exception {
        this.episode1 = new Episode(validEpisodeId, validSeriesId, validEpisodeNumber,
                validSeasonNumber);
        this.episode1Copy = new Episode(validEpisodeId, validSeriesId, validEpisodeNumber,
                validSeasonNumber);
        this.episode2 = new Episode(anotherValidEpisodeId, validSeriesId, validEpisodeNumber,
                validSeasonNumber);
    }

    @Test
    public final void testEpisode() {
        Assert.assertEquals(validEpisodeId, this.episode1.getId());
        Assert.assertEquals(validSeriesId, this.episode1.getSeriesId());
        Assert.assertEquals(validEpisodeNumber, this.episode1.getNumber());
        Assert.assertEquals(validSeasonNumber, this.episode1.getSeasonNumber());
        Assert.assertEquals("", this.episode1.getDirector());
        Assert.assertEquals("", this.episode1.getFirstAired());
        Assert.assertEquals("", this.episode1.getGuestStars());
        Assert.assertEquals("", this.episode1.getName());
        Assert.assertEquals("", this.episode1.getOverview());
        Assert.assertEquals("", this.episode1.getWriter());
        Assert.assertEquals("", this.episode1.getPoster());
        Assert.assertEquals(false, this.episode1.isViewed());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testEpisodeEpisodeIdNull() {
        final String notNullNotEmpty = "123124";
        final int positiveInteger = 2;

        new Episode(null, notNullNotEmpty, positiveInteger, positiveInteger);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testEpisodeEpisodeIdEmpty() {
        final String notNullNotEmpty = "123124";
        final int positiveInteger = 2;

        new Episode("", notNullNotEmpty, positiveInteger, positiveInteger);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testEpisodeSeriesIdNull() {
        final String notNullNotEmpty = "123124";
        final int positiveInteger = 2;

        new Episode(notNullNotEmpty, null, positiveInteger, positiveInteger);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testEpisodeSeriesIdEmpty() {
        final String notNullNotEmpty = "123124";
        final int positiveInteger = 2;

        new Episode(notNullNotEmpty, null, positiveInteger, positiveInteger);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testEpisodeNegativeNumber() {
        final String notNullNotEmpty = "123124";
        final int positiveInteger = 2;

        new Episode(notNullNotEmpty, notNullNotEmpty, -1, positiveInteger);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testEpisodeNegativeSeasonNumber() {
        final String notNullNotEmpty = "123124";
        final int positiveInteger = 2;

        new Episode(notNullNotEmpty, notNullNotEmpty, positiveInteger, -1);
    }

    @Test
    public final void testEqualsObject() {
        // basics
        Assert.assertFalse(this.episode1.equals(null));
        Assert.assertFalse(this.episode1.equals("randomString"));

        // reflexive
        Assert.assertEquals(this.episode1, this.episode1);

        // symmetric
        final Episode newEpisode = new Episode(this.episode1.getId(), anotherValidSeriesId,
                anotherValidEpisodeNumber, anotherValidSeasonNumber);

        Assert.assertNotSame(this.episode1, this.episode1Copy);
        Assert.assertEquals(this.episode1, this.episode1Copy);
        Assert.assertEquals(this.episode1Copy, this.episode1);

        Assert.assertEquals(this.episode1, newEpisode);
        Assert.assertEquals(newEpisode, this.episode1);

        Assert.assertFalse(this.episode1.equals(this.episode2));
        Assert.assertFalse(this.episode2.equals(this.episode1));
        Assert.assertFalse(this.episode2.equals(newEpisode));
        Assert.assertFalse(newEpisode.equals(this.episode2));

        // transitive
        Assert.assertEquals(this.episode1, this.episode1Copy);
        Assert.assertEquals(this.episode1Copy, newEpisode);
        Assert.assertEquals(this.episode1, newEpisode);
    }

    @Test
    public final void testHashCode() {
        Assert.assertEquals(this.episode1.hashCode(), this.episode1.hashCode());

        final Episode newEpisode = new Episode(this.episode1.getId(), anotherValidSeriesId,
                anotherValidEpisodeNumber, anotherValidSeasonNumber);

        Assert.assertEquals(this.episode1.hashCode(), this.episode1Copy.hashCode());
        Assert.assertEquals(this.episode1.hashCode(), newEpisode.hashCode());

        Assert.assertFalse(this.episode1.hashCode() == this.episode2.hashCode());
        Assert.assertFalse(this.episode2.hashCode() == newEpisode.hashCode());
    }

    @Test
    public final void testMarkAsNotViewed() {
        this.episode1.setViewed(true);
        this.episode1.markAsNotViewed();
        Assert.assertFalse(this.episode1.isViewed());
    }

    @Test
    public final void testMarkAsViewed() {
        this.episode1.setViewed(false);
        this.episode1.markAsViewed();
        Assert.assertTrue(this.episode1.isViewed());
    }

    @Test
    public final void testSetDirector() {
        final String director = "Michael Palin";
        this.episode1.setDirector(director);
        assertEquals(director, this.episode1.getDirector());
        this.episode1.setDirector("");
        assertEquals("", this.episode1.getDirector());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSetDirectorNull() {
        this.episode1.setDirector(null);
    }

    @Test
    public final void testSetFirstAired() {
        this.episode1.setFirstAired("12/14/1969");
        assertEquals("12/14/1969", this.episode1.getFirstAired());
        this.episode1.setFirstAired("");
        assertEquals("", this.episode1.getFirstAired());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSetFirstAiredNull() {
        this.episode1.setFirstAired(null);
    }

    @Test
    public final void testSetGuestStars() {
        final String guestStars = "Fred Tomlinson Singers";
        this.episode1.setGuestStars(guestStars);
        assertEquals(guestStars, this.episode1.getGuestStars());
        this.episode1.setGuestStars("");
        assertEquals("", this.episode1.getGuestStars());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testSetGuestStarsNull() {
        this.episode1.setGuestStars(null);
    }

    @Test
    public final void testSetName() {
        final String name = "The Lumberjack Song";
        this.episode1.setName(name);
        assertEquals(name, this.episode1.getName());
        this.episode1.setName("");
        assertEquals("", this.episode1.getName());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testSetNameNull() {
        this.episode1.setName(null);
    }

    @Test
    public final void testSetOverview() {
        final String overview = "The common theme was of an average man (played by Michael Palin "
                + "in the original television version, but in later live versions by Eric "
                + "Idle) who expresses dissatisfaction with his current job (as a barber, "
                + "weatherman, pet shop owner, etc.) and then announces, \"I didn't want to "
                + "be [the given profession]. I wanted to be... a lumberjack!\" He proceeds "
                + "to talk about the life of a lumberjack (\"Leaping from tree to tree\"), "
                + "and lists various trees (e.g. fir, larch, Scots pine). Ripping off his "
                + "coat to reveal a red flannel shirt, he walks over to a stage with a "
                + "coniferous forest backdrop, and he begins to sing about the wonders of "
                + "being a lumberjack in British Columbia. Then, he is unexpectedly backed "
                + "up by a small choir of male singers, all dressed as Canadian Mounties "
                + "(several were regular Python performers, while the rest were generally "
                + "members of an actual singing troupe, such as the Fred Tomlinson Singers "
                + "in the TV version).";
        this.episode1.setOverview(overview);
        assertEquals(overview, this.episode1.getOverview());
        this.episode1.setOverview("");
        assertEquals("", this.episode1.getOverview());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public final void testSetOverviewNull() {
        this.episode1.setOverview(null);
    }

    @Test
    public final void testSetPoster() {
        String poster = "SomePoster";
        this.episode1.setPoster(poster);
        assertEquals(poster, episode1.getPoster());
        this.episode1.setPoster("");
        assertEquals("", episode1.getPoster());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSetPosterNull() {
        this.episode1.setPoster(null);
    }

    @Test
    public final void testSetViewed() {
        this.episode1.setViewed(false);
        Assert.assertFalse(this.episode1.isViewed());
        this.episode1.setViewed(true);
        Assert.assertTrue(this.episode1.isViewed());
        this.episode1.setViewed(false);
        Assert.assertFalse(this.episode1.isViewed());
    }

    @Test
    public final void testSetWriter() {
        final String writer = "Terry Jones";
        this.episode1.setWriter(writer);
        Assert.assertEquals(writer, this.episode1.getWriter());
        this.episode1.setWriter("");
        Assert.assertEquals("", this.episode1.getWriter());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSetWriterNull() {
        this.episode1.setWriter(null);
    }

    @Test
    public final void testToString() {
        final String name = "The Lumberjack Song";
        this.episode1.setName(name);
        Assert.assertEquals(name, this.episode1.toString());
        
        this.episode1.setName("");
        Assert.assertEquals("", this.episode1.toString());
    }
}
