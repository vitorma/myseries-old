package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;
import br.edu.ufcg.aweseries.thetvdb.Series;
import br.edu.ufcg.aweseries.thetvdb.SeriesParser;

public class SeriesParserTest extends TestCase {

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

	private String seriesWithPosterDescription =
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
		"</Data>";

	public void testSeriesWithoutPoster() {
		ByteArrayInputStream seriesWithoutPosterStream = new ByteArrayInputStream(seriesWithoutPosterDescription.getBytes());
		Series seriesWithoutPoster = new SeriesParser(seriesWithoutPosterStream).parse();

		assertThat(seriesWithoutPoster.getPoster(), not(nullValue()));
		assertThat(seriesWithoutPoster.getPoster(), equalTo(""));
	}

	public void testSeriesWithPoster() {
		ByteArrayInputStream seriesWithPosterStream = new ByteArrayInputStream(seriesWithPosterDescription.getBytes());
		Series seriesWithPoster = new SeriesParser(seriesWithPosterStream).parse();
		
		assertThat(seriesWithPoster.getPoster(), not(nullValue()));
		assertThat(seriesWithPoster.getPoster(), equalTo("posters/80348-15.jpg"));
	}

	public void testSeriesOverview() {
		ByteArrayInputStream seriesWithPosterStream = new ByteArrayInputStream(seriesWithPosterDescription.getBytes());
		Series series = new SeriesParser(seriesWithPosterStream).parse();
		
		assertThat(series.getOverview(), not(nullValue()));
		assertThat(series.getOverview(), equalTo("Chuck Bartowski, ace computer geek at Buy More, is not in his right mind. That's a good thing. Ever since he unwittingly downloaded stolen government secrets into his brain, action, excitement and a cool secret- agent girlfriend have entered his life. It's a bad thing, too. Because now Chuck is in danger 24/7."));
	}
}
