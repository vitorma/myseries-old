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

package mobi.myseries.test.unit.domain.source;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.Language;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesParser;
import mobi.myseries.domain.source.StreamCreationFailedException;
import mobi.myseries.domain.source.StreamFactory;
import mobi.myseries.domain.source.TheTVDBConstants;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.Validate;
import mobi.myseries.shared.WeekDay;

public class SeriesParserTest extends TestCase {

    private static final DateFormat DATE_FORMAT = TheTVDBConstants.DATE_FORMAT;

    /* Field values */

    private static final String BASE_SERIES_ID = "1";
    private static final String BASE_SERIES_NAME = "BaseSeries";
    private static final String BASE_SERIES_STATUS = "";
    private static final String BASE_SERIES_AIR_DAY = "";
    private static final String BASE_SERIES_AIR_TIME = "";
    private static final String BASE_SERIES_AIR_DATE = "";
    private static final String BASE_SERIES_RUNTIME = "";
    private static final String BASE_SERIES_NETWORK = "";
    private static final String BASE_SERIES_OVERVIEW = "";
    private static final String BASE_SERIES_GENRES = "";
    private static final String BASE_SERIES_ACTORS = "";
    private static final String BASE_SERIES_POSTER_FILE_NAME = "";

    private static final String FULL_SERIES_ID = "2";
    private static final String FULL_SERIES_NAME = "FullSeries";
    private static final String FULL_SERIES_STATUS = "Continuing";
    private static final String FULL_SERIES_AIR_DAY = "Sunday";
    private static final String FULL_SERIES_AIR_TIME = "1:00 AM";
    private static final String FULL_SERIES_AIR_DATE = "2012-01-01";
    private static final String FULL_SERIES_RUNTIME = "60";
    private static final String FULL_SERIES_NETWORK = "Network";
    private static final String FULL_SERIES_OVERVIEW = "Overview";
    private static final String FULL_SERIES_GENRES = "|Genre1|Genre2|";
    private static final String FULL_SERIES_ACTORS = "|Actor1|Actor2|Actor3|Actor4|Actor5|Actor6|";
    private static final String FULL_SERIES_POSTER_FILE_NAME = "Poster";

    private static final String SEASON_NUMBER = "1";

    private static final String EPISODE1_ID = "1";
    private static final String EPISODE1_NUMBER = "1";
    private static final String EPISODE1_NAME = "Episode1";
    private static final String EPISODE1_AIR_DATE = "2012-01-01";
    private static final String EPISODE1_OVERVIEW = "Overview1";
    private static final String EPISODE1_DIRECTORS = "|Director1|";
    private static final String EPISODE1_WRITERS = "|Writer1|";
    private static final String EPISODE1_GUEST_STARS = "|Actor1|Actor2|";
    private static final String EPISODE1_IMAGE_FILE_NAME = "Image1";

    private static final String EPISODE2_ID = "2";
    private static final String EPISODE2_NUMBER = "2";
    private static final String EPISODE2_NAME = "Episode2";
    private static final String EPISODE2_AIR_DATE = "2012-01-08";
    private static final String EPISODE2_OVERVIEW = "Overview2";
    private static final String EPISODE2_DIRECTORS = "|Director1|Director2|";
    private static final String EPISODE2_WRITERS = "|Writer1|Writer2|Writer3|";
    private static final String EPISODE2_GUEST_STARS = "|Actor1|Actor2|Actor3|Actor4|Actor5|";
    private static final String EPISODE2_IMAGE_FILE_NAME = "Image2";

    /* XML content */

    private static final String BASE_SERIES_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
        "<Data>" +
        "  <Series>" +
        "    <id>" + BASE_SERIES_ID + "</id>" +
        "    <Actors>" + BASE_SERIES_ACTORS + "</Actors>" +
        "    <Airs_DayOfWeek>" + BASE_SERIES_AIR_DAY + "</Airs_DayOfWeek>" +
        "    <Airs_Time>" + BASE_SERIES_AIR_TIME + "</Airs_Time>" +
        "    <FirstAired>" + BASE_SERIES_AIR_DATE + "</FirstAired>" +
        "    <Genre>" + BASE_SERIES_GENRES + "</Genre>" +
        "    <Network>" + BASE_SERIES_NETWORK + "</Network>" +
        "    <Overview>" + BASE_SERIES_OVERVIEW + "</Overview>" +
        "    <Runtime>" + BASE_SERIES_RUNTIME + "</Runtime>" +
        "    <SeriesName>" + BASE_SERIES_NAME + "</SeriesName>" +
        "    <Status>" + BASE_SERIES_STATUS + "</Status>" +
        "    <poster>" + BASE_SERIES_POSTER_FILE_NAME + "</poster>" +
        "  </Series>" +
        "</Data>";

    private static final String FULL_SERIES_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
        "<Data>" +
        "  <Series>" +
        "    <id>" + FULL_SERIES_ID + "</id>" +
        "    <Actors>" + FULL_SERIES_ACTORS + "</Actors>" +
        "    <Airs_DayOfWeek>" + FULL_SERIES_AIR_DAY + "</Airs_DayOfWeek>" +
        "    <Airs_Time>" + FULL_SERIES_AIR_TIME + "</Airs_Time>" +
        "    <FirstAired>" + FULL_SERIES_AIR_DATE + "</FirstAired>" +
        "    <Genre>" + FULL_SERIES_GENRES + "</Genre>" +
        "    <Network>" + FULL_SERIES_NETWORK + "</Network>" +
        "    <Overview>" + FULL_SERIES_OVERVIEW + "</Overview>" +
        "    <Runtime>" + FULL_SERIES_RUNTIME + "</Runtime>" +
        "    <SeriesName>" + FULL_SERIES_NAME + "</SeriesName>" +
        "    <Status>" + FULL_SERIES_STATUS + "</Status>" +
        "    <poster>" + FULL_SERIES_POSTER_FILE_NAME + "</poster>" +
        "  </Series>" +
        "  <Episode>" +
        "   <id>" + EPISODE1_ID + "</id>" +
        "   <Director>" + EPISODE1_DIRECTORS + "</Director>" +
        "   <EpisodeName>" + EPISODE1_NAME +"</EpisodeName>" +
        "   <EpisodeNumber>" + EPISODE1_NUMBER + "</EpisodeNumber>" +
        "   <FirstAired>" + EPISODE1_AIR_DATE + "</FirstAired>" +
        "   <GuestStars>" + EPISODE1_GUEST_STARS + "</GuestStars>" +
        "   <Overview>" + EPISODE1_OVERVIEW + "</Overview>" +
        "   <SeasonNumber>" + SEASON_NUMBER + "</SeasonNumber>" +
        "   <Writer>" + EPISODE1_WRITERS + "</Writer>" +
        "   <filename>" + EPISODE1_IMAGE_FILE_NAME + "</filename>" +
        "   <seriesid>" + FULL_SERIES_ID + "</seriesid>" +
        "  </Episode>" +
        "  <Episode>" +
        "   <id>" + EPISODE2_ID + "</id>" +
        "   <Director>" + EPISODE2_DIRECTORS + "</Director>" +
        "   <EpisodeName>" + EPISODE2_NAME +"</EpisodeName>" +
        "   <EpisodeNumber>" + EPISODE2_NUMBER + "</EpisodeNumber>" +
        "   <FirstAired>" + EPISODE2_AIR_DATE + "</FirstAired>" +
        "   <GuestStars>" + EPISODE2_GUEST_STARS + "</GuestStars>" +
        "   <Overview>" + EPISODE2_OVERVIEW + "</Overview>" +
        "   <SeasonNumber>" + SEASON_NUMBER + "</SeasonNumber>" +
        "   <Writer>" + EPISODE2_WRITERS + "</Writer>" +
        "   <filename>" + EPISODE2_IMAGE_FILE_NAME + "</filename>" +
        "   <seriesid>" + FULL_SERIES_ID + "</seriesid>" +
        "  </Episode>" +
        "</Data>";

    private static final String SERIES_XML_WITHOUT_ROOT_ELEMENT =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
      "  <Series>" +
      "    <id>" + BASE_SERIES_ID + "</id>" +
      "    <Actors>" + BASE_SERIES_ACTORS + "</Actors>" +
      "    <Airs_DayOfWeek>" + BASE_SERIES_AIR_DAY + "</Airs_DayOfWeek>" +
      "    <Airs_Time>" + BASE_SERIES_AIR_TIME + "</Airs_Time>" +
      "    <FirstAired>" + BASE_SERIES_AIR_DATE + "</FirstAired>" +
      "    <Genre>" + BASE_SERIES_GENRES + "</Genre>" +
      "    <Network>" + BASE_SERIES_NETWORK + "</Network>" +
      "    <Overview>" + BASE_SERIES_OVERVIEW + "</Overview>" +
      "    <Runtime>" + BASE_SERIES_RUNTIME + "</Runtime>" +
      "    <SeriesName>" + BASE_SERIES_NAME + "</SeriesName>" +
      "    <Status>" + BASE_SERIES_STATUS + "</Status>" +
      "    <poster>" + BASE_SERIES_POSTER_FILE_NAME + "</poster>" +
      "  </Series>";

    /* StreamFactory */

    private static final int SERIES_ID_FOR_INVALID_XML = -1;

    private static class SeriesParserTestStreamFactory implements StreamFactory {

        private InputStream streamFor(String xml) {
            return new ByteArrayInputStream(xml.getBytes());
        }

        private void checkLikeUrlFactoryWouldCheck(Language language) {
            Validate.isNonNull(language, "language");
        }

        @Override
        public InputStream streamForSeries(int seriesId, Language language) throws StreamCreationFailedException {
            this.checkLikeUrlFactoryWouldCheck(language);

            if (seriesId == Integer.valueOf(BASE_SERIES_ID)) {
                return this.streamFor(BASE_SERIES_XML);
            }

            if (seriesId == Integer.valueOf(FULL_SERIES_ID)) {
                return this.streamFor(FULL_SERIES_XML);
            }

            if (seriesId == SERIES_ID_FOR_INVALID_XML) {
                return this.streamFor(SERIES_XML_WITHOUT_ROOT_ELEMENT);
            }

            throw new StreamCreationFailedException();
        }

        @Override
        public InputStream streamForSeriesSearch(String seriesName, Language language) {return null;}

        @Override
        public InputStream streamForSeriesPoster(String resourcePath) {return null;}

        @Override
        public InputStream streamForEpisodeImage(String fileName) {return null;}

        @Override
        public ZipInputStream streamForUpdatesSince(long dateInMiliseconds) {return null;}
    }

    /* Parser to test */

    private SeriesParser seriesParser = new SeriesParser(new SeriesParserTestStreamFactory());

    /* Construction */

    public void testConstructingSeriesParserWithNullStreamFactoryCausesIllegalArgumentException() {
        try {
            new SeriesParser(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    /* Parse */

    public void testNullLanguageCausesIllegalArgumentExceptionAtTheParsing()
            throws ParsingFailedException, StreamCreationFailedException, ConnectionFailedException, ConnectionTimeoutException {
        try {
            this.seriesParser.parse(Integer.valueOf(BASE_SERIES_ID), null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testParsingInvalidXmlCausesParsingFailedException()
            throws StreamCreationFailedException, ConnectionFailedException, ConnectionTimeoutException {
        try {
            this.seriesParser.parse(SERIES_ID_FOR_INVALID_XML, Language.ENGLISH);
            fail("Should have thrown a ParsingFailedException");
        } catch (ParsingFailedException e) {}
    }

    public void testSeriesParserParsesBaseSeriesXml()
            throws ParsingFailedException, StreamCreationFailedException, ConnectionFailedException, NumberFormatException, ConnectionTimeoutException {
        Series baseSeries = this.seriesParser.parse(Integer.valueOf(BASE_SERIES_ID), Language.ENGLISH);

        assertThat(baseSeries.id(), equalTo(Integer.valueOf(BASE_SERIES_ID)));
        assertThat(baseSeries.name(), equalTo(BASE_SERIES_NAME));
        assertThat(baseSeries.status(), equalTo(Status.from(BASE_SERIES_STATUS)));
        assertThat(baseSeries.airDay(), equalTo(null));
        assertThat(baseSeries.airtime(), equalTo(null));
        assertThat(baseSeries.airDate(), equalTo(null));
        assertThat(baseSeries.runtime(), equalTo(BASE_SERIES_RUNTIME));
        assertThat(baseSeries.network(), equalTo(BASE_SERIES_NETWORK));
        assertThat(baseSeries.overview(), equalTo(BASE_SERIES_OVERVIEW));
        assertThat(baseSeries.genres(), equalTo(Strings.normalizePipeSeparated(BASE_SERIES_GENRES)));
        assertThat(baseSeries.actors(), equalTo(Strings.normalizePipeSeparated(BASE_SERIES_ACTORS)));
        assertThat(baseSeries.posterFileName(), equalTo(BASE_SERIES_POSTER_FILE_NAME));

        assertThat(baseSeries.numberOfEpisodes(), equalTo(0));
    }

    public void testSeriesParserParsesFullSeriesXml()
            throws ParsingFailedException, StreamCreationFailedException, ConnectionFailedException, NumberFormatException, ConnectionTimeoutException {
        Series fullSeries = this.seriesParser.parse(Integer.valueOf(FULL_SERIES_ID), Language.ENGLISH);

        assertThat(fullSeries.id(), equalTo(Integer.valueOf(FULL_SERIES_ID)));
        assertThat(fullSeries.name(), equalTo(FULL_SERIES_NAME));
        assertThat(fullSeries.status(), equalTo(Status.from(FULL_SERIES_STATUS)));
        assertThat(fullSeries.airDay(), equalTo(WeekDay.valueOf(FULL_SERIES_AIR_DAY)));
        assertThat(fullSeries.airtime(), equalTo(Time.valueOf(FULL_SERIES_AIR_TIME)));
        assertThat(fullSeries.airDate(), equalTo(DatesAndTimes.parse(FULL_SERIES_AIR_DATE, DATE_FORMAT, null)));
        assertThat(fullSeries.runtime(), equalTo(FULL_SERIES_RUNTIME));
        assertThat(fullSeries.network(), equalTo(FULL_SERIES_NETWORK));
        assertThat(fullSeries.overview(), equalTo(FULL_SERIES_OVERVIEW));
        assertThat(fullSeries.genres(), equalTo(Strings.normalizePipeSeparated(FULL_SERIES_GENRES)));
        assertThat(fullSeries.actors(), equalTo(Strings.normalizePipeSeparated(FULL_SERIES_ACTORS)));
        assertThat(fullSeries.posterFileName(), equalTo(FULL_SERIES_POSTER_FILE_NAME));

        assertThat(fullSeries.numberOfEpisodes(), equalTo(2));

        Episode episode1 = fullSeries.season(Integer.valueOf(SEASON_NUMBER)).episode(Integer.valueOf(EPISODE1_NUMBER));

        assertThat(episode1.id(), equalTo(Integer.valueOf(EPISODE1_ID)));
        assertThat(episode1.seriesId(), equalTo(Integer.valueOf(FULL_SERIES_ID)));
        assertThat(episode1.number(), equalTo(Integer.valueOf(EPISODE1_NUMBER)));
        assertThat(episode1.seasonNumber(), equalTo(Integer.valueOf(SEASON_NUMBER)));
        assertThat(episode1.name(), equalTo(EPISODE1_NAME));
        assertThat(episode1.airDate(), equalTo(DatesAndTimes.parse(EPISODE1_AIR_DATE, TheTVDBConstants.DATE_FORMAT, null)));
        assertThat(episode1.overview(), equalTo(EPISODE1_OVERVIEW));
        assertThat(episode1.directors(), equalTo(Strings.normalizePipeSeparated(EPISODE1_DIRECTORS)));
        assertThat(episode1.writers(), equalTo(Strings.normalizePipeSeparated(EPISODE1_WRITERS)));
        assertThat(episode1.guestStars(), equalTo(Strings.normalizePipeSeparated(EPISODE1_GUEST_STARS)));
        assertThat(episode1.imageFileName(), equalTo(EPISODE1_IMAGE_FILE_NAME));

        Episode episode2 = fullSeries.season(Integer.valueOf(SEASON_NUMBER)).episode(Integer.valueOf(EPISODE2_NUMBER));

        assertThat(episode2.id(), equalTo(Integer.valueOf(EPISODE2_ID)));
        assertThat(episode2.seriesId(), equalTo(Integer.valueOf(FULL_SERIES_ID)));
        assertThat(episode2.number(), equalTo(Integer.valueOf(EPISODE2_NUMBER)));
        assertThat(episode2.seasonNumber(), equalTo(Integer.valueOf(SEASON_NUMBER)));
        assertThat(episode2.name(), equalTo(EPISODE2_NAME));
        assertThat(episode2.airDate(), equalTo(DatesAndTimes.parse(EPISODE2_AIR_DATE, TheTVDBConstants.DATE_FORMAT, null)));
        assertThat(episode2.overview(), equalTo(EPISODE2_OVERVIEW));
        assertThat(episode2.directors(), equalTo(Strings.normalizePipeSeparated(EPISODE2_DIRECTORS)));
        assertThat(episode2.writers(), equalTo(Strings.normalizePipeSeparated(EPISODE2_WRITERS)));
        assertThat(episode2.guestStars(), equalTo(Strings.normalizePipeSeparated(EPISODE2_GUEST_STARS)));
        assertThat(episode2.imageFileName(), equalTo(EPISODE2_IMAGE_FILE_NAME));
    }
}
