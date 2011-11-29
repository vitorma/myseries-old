/*
 *   EpisodeTest.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.test.unit.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.DomainObjectListener;
import br.edu.ufcg.aweseries.model.Episode;

public class EpisodeTest {

    private Episode episode1;
    private Episode episode2;
    private DomainObjectListener<Episode> episode1Listener;
    private Episode episode1Copy;
    private static final String validEpisodeId = "1234";
    private static final String anotherValidEpisodeId = "1235";
    private static final String validSeriesId = "1237";
    private static final String anotherValidSeriesId = "1236";
    private static final int validEpisodeNumber = 1;
    private static final int anotherValidEpisodeNumber = 2;
    private static final int validSeasonNumber = 3;
    private static final int anotherValidSeasonNumber = 4;
    private static final String validEpisodeName = "Owl Stretching Time";
    private static final Date validDate = new Date();
    private static final String validOverview =
            "BBC-1 began colour broadcasting officially on 15 November 1969. Since September " +
            "1969, however, they had been broadcasting colour programmes \"unofficially\", so " +
            "while the whole of the first series was broadcast in colour, this episode was the " +
            "first to be advertised as being in colour (source: Notes taken from BBC videotape " +
            "operators and transmission managers made at the time).";
    private static final String validDirector = "Graham Chapman";
    private static final String validWriter = "John Cleese";
    private static final String validGuestStars = "Douglas Adams";
    private static final String validPoster = "someposter.png";
    
    @Before
    public void setUp() throws Exception {
        this.episode1 = new Episode(validEpisodeId, validSeriesId, validEpisodeNumber,
                validSeasonNumber);
        this.episode1Copy = new Episode(validEpisodeId, validSeriesId, validEpisodeNumber,
                validSeasonNumber);
        this.episode2 = new Episode(anotherValidEpisodeId, validSeriesId, validEpisodeNumber,
                validSeasonNumber);
        this.episode1Listener = Mockito.mock(DomainObjectListener.class);
        this.episode1.addListener(this.episode1Listener);
    }

    @Test
    public final void testEpisode() {
        Assert.assertEquals(validEpisodeId, this.episode1.getId());
        Assert.assertEquals(validSeriesId, this.episode1.getSeriesId());
        Assert.assertEquals(validEpisodeNumber, this.episode1.getNumber());
        Assert.assertEquals(validSeasonNumber, this.episode1.getSeasonNumber());
        Assert.assertEquals("", this.episode1.getDirector());
        Assert.assertEquals(null, this.episode1.getFirstAired());
        Assert.assertEquals("", this.episode1.getGuestStars());
        Assert.assertEquals("", this.episode1.getName());
        Assert.assertEquals("", this.episode1.getOverview());
        Assert.assertEquals("", this.episode1.getWriter());
        Assert.assertEquals("", this.episode1.getPoster());
        Assert.assertEquals(false, this.episode1.wasSeen());
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
        this.episode1.markWetherSeen(true);
        this.episode1.markAsNotSeen();
        Assert.assertFalse(this.episode1.wasSeen());
    }

    @Test
    public final void testMarkAsViewed() {
        this.episode1.markWetherSeen(false);
        verify(this.episode1Listener, times(1)).onUpdate(this.episode1);
        this.episode1.markAsSeen();
        verify(this.episode1Listener, times(2)).onUpdate(this.episode1);
        Assert.assertTrue(this.episode1.wasSeen());
    }

    public final void testMergeWith() {
        this.episode1.markAsSeen();
        this.episode1.setName(validEpisodeName);
        this.episode1.setFirstAired(validDate);
        this.episode1.setOverview(validOverview);
        this.episode1.setDirector(validDirector);
        this.episode1.setWriter(validWriter);
        this.episode1.setGuestStars(validGuestStars);
        this.episode1.setPoster(validPoster);
        
        Episode newEpisode =
                new Episode(validEpisodeId, validSeriesId, validEpisodeNumber, validSeasonNumber);
        
        newEpisode.mergeWith(this.episode1);
        
        
        Assert.assertEquals(validEpisodeName, newEpisode.getName());
        Assert.assertEquals(validDate, newEpisode.getFirstAired());
        Assert.assertEquals(validOverview, newEpisode.getOverview());
        Assert.assertEquals(validDirector, newEpisode.getDirector());
        Assert.assertEquals(validWriter, newEpisode.getWriter());
        Assert.assertEquals(validGuestStars, newEpisode.getGuestStars());
        Assert.assertEquals(validPoster, newEpisode.getPoster());
        Assert.assertEquals(true, newEpisode.wasSeen());

    }

    @Test(expected = IllegalArgumentException.class)
    public final void testMergeWithNull() {
        this.episode1.mergeWith(null);
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
        final Date today = new Date();
        final Date aDay = new Date(Long.MAX_VALUE);
        this.episode1.setFirstAired(today);
        assertEquals(today, this.episode1.getFirstAired());
        this.episode1.setFirstAired(aDay);
        assertEquals(aDay, this.episode1.getFirstAired());
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
        this.episode1.markWetherSeen(false);
        Assert.assertFalse(this.episode1.wasSeen());
        this.episode1.markWetherSeen(true);
        verify(this.episode1Listener, times(2)).onUpdate(this.episode1);
        Assert.assertTrue(this.episode1.wasSeen());
        this.episode1.markWetherSeen(false);
        verify(this.episode1Listener, times(3)).onUpdate(this.episode1);
        Assert.assertFalse(this.episode1.wasSeen());
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
