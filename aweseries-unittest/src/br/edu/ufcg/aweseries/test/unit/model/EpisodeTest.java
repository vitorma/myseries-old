/*
 *   EpisodeTest.java
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

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.aweseries.model.Episode;

public class EpisodeTest {

    private static final String ID1 = "1";
    private static final String ID2 = "2";
    private static final String SERIES_ID1 = "1";
    private static final String SERIES_ID2 = "2";
    private static final int NUMBER1 = 1;
    private static final int NUMBER2 = 2;
    private static final int SEASON_NUMBER1 = 1;
    private static final int SEASON_NUMBER2 = 2;
    private static final String NAME = "name";
    private static final Date FIRST_AIRED = new Date();
    private static final String OVERVIEW = "overview";
    private static final String DIRECTORS = "directors";
    private static final String WRITERS = "writers";
    private static final String GUEST_STARS = "guest stars";
    private static final String POSTER = "poster";

    //Building----------------------------------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void testBuildAnEpisodeWithoutId() {
    	Episode.builder()
        	.withSeriesId(SERIES_ID1)
        	.withNumber(NUMBER1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuildAnEpisodeWithNullId() {
    	Episode.builder()
        	.withId(null)
        	.withSeriesId(SERIES_ID1)
        	.withNumber(NUMBER1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuildAnEpisodeWithBlankId() {
    	Episode.builder()
        	.withId("")
        	.withSeriesId(SERIES_ID1)
        	.withNumber(NUMBER1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildAnEpisodeWithoutSeriesId() {
    	Episode.builder()
        	.withId(ID1)
        	.withNumber(NUMBER1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildAnEpisodeWithNullSeriesId() {
    	Episode.builder()
        	.withId(ID1)
        	.withSeriesId(null)
        	.withNumber(NUMBER1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildAnEpisodeWithBlankSeriesId() {
    	Episode.builder()
        	.withId(ID1)
        	.withSeriesId("  ")
        	.withNumber(NUMBER1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildAnEpisodeWithoutNumber() {
    	Episode.builder()
        	.withId(ID1)
        	.withSeriesId(SERIES_ID1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildAnEpisodeWithNegativeNumber() {
    	Episode.builder()
        	.withId(ID1)
        	.withSeriesId(SERIES_ID1)
        	.withNumber(-1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildAnEpisodeWithoutSeasonNumber() {
    	Episode.builder()
        	.withId(ID1)
        	.withSeriesId(SERIES_ID1)
        	.withNumber(NUMBER1)
        	.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildAnEpisodeWithNegativeSeasonNumber() {
    	Episode.builder()
        	.withId(ID1)
        	.withSeriesId(SERIES_ID1)
        	.withNumber(NUMBER1)
        	.withSeasonNumber(-1)
        	.build();
    }
    
    @Test
    public void testBuildAnEpisode() {
    	Episode e1 = Episode.builder()
        	.withId(ID1)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER1)
            .withSeasonNumber(SEASON_NUMBER1)
            .withName(NAME)
            .withAirdate(FIRST_AIRED)
            .withOverview(OVERVIEW)
            .withDirector(DIRECTORS)
            .withWriter(WRITERS)
            .withGuestStars(GUEST_STARS)
            .withPoster(POSTER)
            .withSeen(true)
            .build();

    	Assert.assertEquals(ID1, e1.id());
    	Assert.assertEquals(SERIES_ID1, e1.seriesId());
    	Assert.assertEquals(NUMBER1, e1.number());
    	Assert.assertEquals(SEASON_NUMBER1, e1.seasonNumber());
    	Assert.assertEquals(NAME, e1.name());
    	Assert.assertEquals(FIRST_AIRED, e1.airdate());
    	Assert.assertEquals(OVERVIEW, e1.overview());
    	Assert.assertEquals(DIRECTORS, e1.directors());
    	Assert.assertEquals(WRITERS, e1.writers());
    	Assert.assertEquals(GUEST_STARS, e1.guestStars());
    	Assert.assertEquals(POSTER, e1.poster());
    	Assert.assertEquals(true, e1.wasSeen());
    	
    	Episode e2 = Episode.builder()
        	.withId(ID2)
            .withSeriesId(SERIES_ID1)
            .withNumber(NUMBER2)
            .withSeasonNumber(SEASON_NUMBER1)
            .build();

    	Assert.assertEquals(ID2, e2.id());
    	Assert.assertEquals(SERIES_ID1, e2.seriesId());
    	Assert.assertEquals(NUMBER2, e2.number());
    	Assert.assertEquals(SEASON_NUMBER1, e2.seasonNumber());
    	Assert.assertEquals(null, e2.name());
    	Assert.assertEquals(null, e2.airdate());
    	Assert.assertEquals(null, e2.overview());
    	Assert.assertEquals(null, e2.directors());
    	Assert.assertEquals(null, e2.writers());
    	Assert.assertEquals(null, e2.guestStars());
    	Assert.assertEquals(null, e2.poster());
    	Assert.assertEquals(false, e2.wasSeen());
    }

    //Seen--------------------------------------------------------------------------------------------------------------
    
    @Test
    public void testMarkSeenAs() {
    	Episode e = Episode.builder()
        	.withId(ID1)
        	.withSeriesId(SERIES_ID1)
        	.withNumber(NUMBER1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.withSeen(false)
        	.build();

    	e.markSeenAs(true);
    	Assert.assertTrue(e.wasSeen());
    	e.markSeenAs(false);
    	Assert.assertFalse(e.wasSeen());
    }

    @Test
    public void testMarkAsSeen() {
    	Episode e = Episode.builder()
    	    .withId(ID1)
    	    .withSeriesId(SERIES_ID1)
    	    .withNumber(NUMBER1)
    	    .withSeasonNumber(SEASON_NUMBER1)
    	    .withSeen(false)
    	    .build();

    	e.markAsSeen();
    	Assert.assertTrue(e.wasSeen());
    }

    @Test
    public void testMarkAsNotSeen() {
    	Episode e = Episode.builder()
	    	.withId(ID1)
	    	.withSeriesId(SERIES_ID1)
	    	.withNumber(NUMBER1)
	    	.withSeasonNumber(SEASON_NUMBER1)
	    	.withSeen(true)
	    	.build();

    	e.markAsNotSeen();
        Assert.assertFalse(e.wasSeen());
    }

    //Merge-------------------------------------------------------------------------------------------------------------
    
    @Test(expected = IllegalArgumentException.class)
    public void testMergeWithNull() {
    	Episode.builder()
    		.withId(ID1)
    		.withSeriesId(SERIES_ID1)
    		.withNumber(NUMBER1)
    		.withSeasonNumber(SEASON_NUMBER1)
    		.build()
    		.mergeWith(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeWithOtherHavingNotSameId() {
    	Episode e1 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	Episode e2 = Episode.builder()
			.withId(ID2)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	e1.mergeWith(e2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeWithOtherHavingNotSameSeriesId() {
    	Episode e1 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	Episode e2 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID2)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	e1.mergeWith(e2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeWithOtherHavingNotSameNumber() {
    	Episode e1 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	Episode e2 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER2)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	e1.mergeWith(e2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeWithOtherHavingNotSameSeasonNumber() {
    	Episode e1 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	Episode e2 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER2)
			.build();

    	e1.mergeWith(e2);
    }

    @Test
    public void testMergeWith() {
    	Episode e1 = Episode.builder()
        	.withId(ID1)
        	.withSeriesId(SERIES_ID1)
        	.withNumber(NUMBER1)
        	.withSeasonNumber(SEASON_NUMBER1)
        	.withName(NAME)
        	.withAirdate(FIRST_AIRED)
        	.withOverview(OVERVIEW)
        	.withDirector(DIRECTORS)
        	.withWriter(WRITERS)
        	.withGuestStars(GUEST_STARS)
        	.withPoster(POSTER)
        	.withSeen(true)
        	.build();

    	Episode e2 = Episode.builder()
    		.withId(ID1)
    		.withSeriesId(SERIES_ID1)
    		.withNumber(NUMBER1)
    		.withSeasonNumber(SEASON_NUMBER1)
    		.withSeen(false)
    		.build();

    	e2.mergeWith(e1);

        Assert.assertEquals(NAME, e2.name());
        Assert.assertEquals(FIRST_AIRED, e2.airdate());
        Assert.assertEquals(OVERVIEW, e2.overview());
        Assert.assertEquals(DIRECTORS, e2.directors());
        Assert.assertEquals(WRITERS, e2.writers());
        Assert.assertEquals(GUEST_STARS, e2.guestStars());
        Assert.assertEquals(POSTER, e2.poster());
        Assert.assertEquals(false, e2.wasSeen());
    }

    //Equals and hashCode-----------------------------------------------------------------------------------------------

    @Test
    public void testEquals() {
    	Episode e1 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	Episode e2 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID2)
			.withNumber(NUMBER2)
			.withSeasonNumber(SEASON_NUMBER2)
			.build();

    	Episode e3 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID2)
			.withNumber(NUMBER2)
			.withSeasonNumber(SEASON_NUMBER2)
			.build();

    	Episode e4 = Episode.builder()
			.withId(ID2)
			.withSeriesId(SERIES_ID2)
			.withNumber(NUMBER2)
			.withSeasonNumber(SEASON_NUMBER2)
			.build();

    	//equals is consistent
    	for (int i=1; i<=1000; i++) {

    		//equals returns false to null objects
    		Assert.assertFalse(e1.equals(null));
    		
    		//equals is reflexive
    		Assert.assertEquals(e1, e1);
    		
    		//equals is symmetric
    		Assert.assertNotSame(e1, e2);
    		Assert.assertEquals(e1, e2);
    		Assert.assertEquals(e2, e1);
    		
    		//equals is transitive
    		Assert.assertEquals(e1, e2);
    		Assert.assertEquals(e2, e3);
    		Assert.assertEquals(e1, e3);

    		//episodes are equal if and only if they have the same id
    		Assert.assertFalse(e2.equals(e4));
    	}
    }
    
    @Test
    public void testHashCode() {
    	Episode e1 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID1)
			.withNumber(NUMBER1)
			.withSeasonNumber(SEASON_NUMBER1)
			.build();

    	Episode e2 = Episode.builder()
			.withId(ID1)
			.withSeriesId(SERIES_ID2)
			.withNumber(NUMBER2)
			.withSeasonNumber(SEASON_NUMBER2)
			.build();

    	Episode e3 = Episode.builder()
			.withId(ID2)
			.withSeriesId(SERIES_ID2)
			.withNumber(NUMBER2)
			.withSeasonNumber(SEASON_NUMBER2)
			.build();

    	//hashCode is consistent
    	for (int i=1; i<=1000; i++) {

    		//equal objects have same hashCode
    		Assert.assertEquals(e1.hashCode(), e2.hashCode());

    		//non equal episodes have distinct hashCodes
    		Assert.assertTrue(e1.hashCode() != e3.hashCode());
    		Assert.assertTrue(e2.hashCode() != e3.hashCode());
    	}
    }
    
    //TODO Listeners----------------------------------------------------------------------------------------------------
}
