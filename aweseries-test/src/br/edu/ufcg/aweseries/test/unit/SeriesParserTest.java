/*
 *   SeriesParserTest.java
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


package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;
import br.edu.ufcg.aweseries.model.Poster;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.util.SampleBitmap;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesParser;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public class SeriesParserTest extends TestCase {

	@SuppressWarnings("unused")
    private String seriesWithoutPosterDescription =
		"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
		"<Data>" +
		"  <Series>" +
		"    <id>80248</id>" +
		"    <Actors>||</Actors>" +
		"    <Airs_DayOfWeek></Airs_DayOfWeek>" +
		"    <Airs_Time></Airs_Time>" +
		"    <ContentRating></ContentRating>" +
		"    <FirstAired>2006-09-21</FirstAired>" +
		"    <Genre>|Children|</Genre>" +
		"    <IMDB_ID>tt0876219</IMDB_ID>" +
		"    <Language>en</Language>" +
		"    <Network>BBC One</Network>" +
		"    <NetworkID></NetworkID>" +
		"    <Overview>Single father, Count Dracula, moves to London from Transylvania with his two kids, Vlad and Ingrid. The story revolves around Vlad wanting to fit in with his classmates in his new school rather than sucking their blood as his father wants him to. Vlad befriends another outsider named Robin who wants to become less like the popular crowd and preferably more vampiric. </Overview>" +
		"    <Rating>10.0</Rating>" +
		"    <RatingCount>1</RatingCount>" +
		"    <Runtime>30</Runtime>" +
		"    <SeriesID>68779</SeriesID>" +
		"    <SeriesName>Young Dracula</SeriesName>" +
		"    <Status>Ended</Status>" +
		"    <added></added>" +
		"    <addedBy></addedBy>" +
		"    <banner>graphical/80248-g3.jpg</banner>" +
		"    <fanart>fanart/original/80248-2.jpg</fanart>" +
		"    <lastupdated>1306063604</lastupdated>" +
		"    <poster></poster>" +
		"    <zap2it_id></zap2it_id>" +
		"  </Series>" +
		"</Data>";

       private String baseSeriesWithPosterDescription =
               "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
               "<Data>" +
               "  <Series>" +
               "    <id>80348</id>" +
               "    <Actors>|Zachary Levi|Yvonne Strahovski|Adam Baldwin|Bonita Friedericy|Julia Ling|Vik Sahay|Ryan McPartlin|Scott Krinsky|Mark Christopher Lawrence|Sarah Lancaster|Joshua Gomez|Mekenna Melvin|Linda Hamilton|Brandon Routh|Matthew Bomer|</Actors>" +
               "    <Airs_DayOfWeek>Monday</Airs_DayOfWeek>" +
               "    <Airs_Time>8:00 PM</Airs_Time>" +
               "    <ContentRating>TV-PG</ContentRating>" +
               "    <FirstAired>2007-09-24</FirstAired>" +
               "    <Genre>|Action and Adventure|Comedy|Drama|</Genre>" +
               "    <IMDB_ID>tt0934814</IMDB_ID>" +
               "    <Language>en</Language>" +
               "    <Network>NBC</Network>" +
               "    <NetworkID></NetworkID>" +
               "    <Overview>Chuck Bartowski, ace computer geek at Buy More, is not in his right mind. That's a good thing. Ever since he unwittingly downloaded stolen governmeent secrets into his brain, action, excitement and a cool secret- agent girlfriend have entered his life. It's a bad thing, too. Because now Chuck is in danger 24/7.</Overview>" +
               "    <Rating>8.8</Rating>" +
               "    <RatingCount>655</RatingCount>" +
               "    <Runtime>60</Runtime>" +
               "    <SeriesID>68724</SeriesID>" +
               "    <SeriesName>Chuck</SeriesName>" +
               "    <Status>Continuing</Status>" +
               "    <added></added>" +
               "    <addedBy></addedBy>" +
               "    <banner>graphical/80348-g21.jpg</banner>" +
               "    <fanart>fanart/original/80348-18.jpg</fanart>" +
               "    <lastupdated>1315879458</lastupdated>" +
               "    <poster>posters/80348-15.jpg</poster>" +
               "    <zap2it_id>EP00930779</zap2it_id>" +
               "  </Series>" +
               "</Data>";

       private String fullSeriesWithPosterDescription =
               "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
               "<Data>" +
               "  <Series>" +
               "    <id>80348</id>" +
               "    <Actors>|Zachary Levi|Yvonne Strahovski|Adam Baldwin|Bonita Friedericy|Julia Ling|Vik Sahay|Ryan McPartlin|Scott Krinsky|Mark Christopher Lawrence|Sarah Lancaster|Joshua Gomez|Mekenna Melvin|Linda Hamilton|Brandon Routh|Matthew Bomer|</Actors>" +
               "    <Airs_DayOfWeek>Monday</Airs_DayOfWeek>" +
               "    <Airs_Time>8:00 PM</Airs_Time>" +
               "    <ContentRating>TV-PG</ContentRating>" +
               "    <FirstAired>2007-09-24</FirstAired>" +
               "    <Genre>|Action and Adventure|Comedy|Drama|</Genre>" +
               "    <IMDB_ID>tt0934814</IMDB_ID>" +
               "    <Language>en</Language>" +
               "    <Network>NBC</Network>" +
               "    <NetworkID></NetworkID>" +
               "    <Overview>Chuck Bartowski, ace computer geek at Buy More, is not in his right mind. That's a good thing. Ever since he unwittingly downloaded stolen government secrets into his brain, action, excitement and a cool secret- agent girlfriend have entered his life. It's a bad thing, too. Because now Chuck is in danger 24/7.</Overview>" +
               "    <Rating>8.8</Rating>" +
               "    <RatingCount>655</RatingCount>" +
               "    <Runtime>60</Runtime>" +
               "    <SeriesID>68724</SeriesID>" +
               "    <SeriesName>Chuck</SeriesName>" +
               "    <Status>Continuing</Status>" +
               "    <added></added>" +
               "    <addedBy></addedBy>" +
               "    <banner>graphical/80348-g21.jpg</banner>" +
               "    <fanart>fanart/original/80348-18.jpg</fanart>" +
               "    <lastupdated>1315879458</lastupdated>" +
               "    <poster>posters/80348-15.jpg</poster>" +
               "    <zap2it_id>EP00930779</zap2it_id>" +
               "  </Series>" +
               "  <Episode>" +
               "   <id>935481</id>" +
               "   <Combined_episodenumber>1</Combined_episodenumber>" +
               "   <Combined_season>0</Combined_season>" +
               "   <DVD_chapter></DVD_chapter>" +
               "   <DVD_discid></DVD_discid>" +
               "   <DVD_episodenumber></DVD_episodenumber>" +
               "   <DVD_season></DVD_season>" +
               "   <Director>Robert Duncan McNeill</Director>" +
               "   <EpImgFlag>2</EpImgFlag>" +
               "   <EpisodeName>Chuck Versus the Third Dimension (2D)</EpisodeName>" +
               "   <EpisodeNumber>1</EpisodeNumber>" +
               "   <FirstAired>2009-02-03</FirstAired>" +
               "   <GuestStars>|Dominic Monaghan|Jerome Bettis|</GuestStars>" +
               "   <IMDB_ID></IMDB_ID>" +
               "   <Language>en</Language>" +
               "   <Overview>Chuck foils a plan to kill Tyler Martin, an international rock star. Chuck's night out with Tyler leads to trouble. Morgan holds a contest among his fellow employees. 2D version of the Episode originally aired in 3D</Overview>" +
               "   <ProductionCode></ProductionCode>" +
               "   <Rating>7.0</Rating>" +
               "   <RatingCount>2</RatingCount>" +
               "   <SeasonNumber>0</SeasonNumber>" +
               "   <Writer>Chris Fedak|Josh Schwartz</Writer>" +
               "   <absolute_number></absolute_number>" +
               "   <airsafter_season></airsafter_season>" +
               "   <airsbefore_episode>13</airsbefore_episode>" +
               "   <airsbefore_season>2</airsbefore_season>" +
               "   <filename>episodes/80348/935481.jpg</filename>" +
               "   <lastupdated>1286047969</lastupdated>" +
               "   <seasonid>27984</seasonid>" +
               "   <seriesid>80348</seriesid>" +
               "  </Episode>" +
               "</Data>";

    private Poster seriesPoster = new Poster(SampleBitmap.pixel);

    private class SeriesParserTestStreamFactory implements StreamFactory {

        @Override
        public InputStream streamForBaseSeries(String seriesId) {
            return new ByteArrayInputStream(baseSeriesWithPosterDescription.getBytes());
        }

        @Override
        public InputStream streamForFullSeries(String seriesId) {
            return new ByteArrayInputStream(fullSeriesWithPosterDescription.getBytes());
        }

        @Override
        public InputStream streamForSeriesSearch(String seriesName) {
            //TODO Auto-generated method stub
            return null;
        }

        @Override
        public InputStream streamForSeriesPosterAt(String resourcePath) {
            return new ByteArrayInputStream(SampleBitmap.pixelBytes);
        }

		@Override
		public InputStream streamForFullSeries(String seriesId, String language) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public InputStream streamForSeriesSearch(String seriesName,
				String language) {
			// TODO Auto-generated method stub
			return null;
		}
    }

    public void testNullStreamFactoryThrowsException() {
        try {
            new SeriesParser(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

	public void failing_testSeriesWithoutPoster() {
		Series seriesWithoutPoster = new SeriesParser(new SeriesParserTestStreamFactory()).parse("80248", "en");

		assertThat(seriesWithoutPoster.poster(), nullValue());
	}

	public void testSeriesWithPoster() {
        Series seriesWithPoster = new SeriesParser(new SeriesParserTestStreamFactory()).parse("80348", "en");

        assertThat(seriesWithPoster.poster(), notNullValue());
        assertThat(seriesWithPoster.poster(), equalTo(seriesPoster));
    }

	public void testSeriesOverview() {
		Series series = new SeriesParser(new SeriesParserTestStreamFactory()).parse("80348", "en");
		
		assertThat(series.overview(), notNullValue());
		assertThat(series.overview(), equalTo("Chuck Bartowski, ace computer geek at Buy More, is not in his right mind. That's a good thing. Ever since he unwittingly downloaded stolen government secrets into his brain, action, excitement and a cool secret- agent girlfriend have entered his life. It's a bad thing, too. Because now Chuck is in danger 24/7."));
	}
}
