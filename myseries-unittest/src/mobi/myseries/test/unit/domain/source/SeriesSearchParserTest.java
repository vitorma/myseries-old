/*
 *   SeriesSearchParserTest.java
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
import java.util.List;

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.Language;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesSearchParser;
import mobi.myseries.domain.source.StreamCreationFailedException;
import mobi.myseries.domain.source.StreamFactory;
import mobi.myseries.shared.Validate;

import junit.framework.TestCase;

public class SeriesSearchParserTest extends TestCase {

    /* Field values */

    private static final String SERIES1_ID = "1";
    private static final String SERIES1_NAME = "Series1";
    private static final String SERIES1_OVERVIEW = "Overview1";

    private static final String SERIES2_ID = "2";
    private static final String SERIES2_NAME = "Series2";
    private static final String SERIES2_OVERVIEW = "Overview2";

    private static final String SERIES3_ID = "3";
    private static final String SERIES3_NAME = "Series3";
    private static final String SERIES3_OVERVIEW = "Overview3";

    /* XML content */

    private static final String EMPTY_SEARCH_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
        "<Data>" +
        "</Data>";

    private static final String NON_EMPTY_SEARCH_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
        "<Data>" +
        "  <Series>" +
        "    <id>" + SERIES1_ID + "</id>" +
        "    <Overview>" + SERIES1_OVERVIEW + "</Overview>" +
        "    <SeriesName>" + SERIES1_NAME + "</SeriesName>" +
        "  </Series>" +
        "  <Series>" +
        "    <id>" + SERIES2_ID + "</id>" +
        "    <Overview>" + SERIES2_OVERVIEW + "</Overview>" +
        "    <SeriesName>" + SERIES2_NAME + "</SeriesName>" +
        "  </Series>" +
        "  <Series>" +
        "    <id>" + SERIES3_ID + "</id>" +
        "    <Overview>" + SERIES3_OVERVIEW + "</Overview>" +
        "    <SeriesName>" + SERIES3_NAME + "</SeriesName>" +
        "  </Series>" +
        "</Data>";

    private static final String SEARCH_XML_WITHOUT_ROOT_ELEMENT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
        "  <Series>" +
        "    <id>" + SERIES1_ID + "</id>" +
        "    <Overview>" + SERIES1_OVERVIEW + "</Overview>" +
        "    <SeriesName>" + SERIES1_NAME + "</SeriesName>" +
        "  </Series>" +
        "  <Series>" +
        "    <id>" + SERIES2_ID + "</id>" +
        "    <Overview>" + SERIES2_OVERVIEW + "</Overview>" +
        "    <SeriesName>" + SERIES2_NAME + "</SeriesName>" +
        "  </Series>" +
        "  <Series>" +
        "    <id>" + SERIES3_ID + "</id>" +
        "    <Overview>" + SERIES3_OVERVIEW + "</Overview>" +
        "    <SeriesName>" + SERIES3_NAME + "</SeriesName>" +
        "</Data>";

    /* StreamFactory */

    private static final String UNMATCHED_SERIES_NAME = "Unmatched series name";
    private static final String MATCHED_SERIES_NAME = "Matched series name";
    private static final String SERIES_NAME_FOR_INVALID_XML = "Series name for invalid xml";

    private static class SeriesSearchParserTestStreamFactory implements StreamFactory {

        private InputStream streamFor(String xml) {
            return new ByteArrayInputStream(xml.getBytes());
        }

        private void checkLikeUrlFactoryWouldCheck(String seriesName, Language language) {
            Validate.isNonBlank(seriesName, "seriesName");
            Validate.isNonNull(language, "language");
        }

        @Override
        public InputStream streamForSeries(int seriesId, Language language) {return null;}

        @Override
        public InputStream streamForSeriesSearch(String seriesName, Language language)
                throws StreamCreationFailedException {
            this.checkLikeUrlFactoryWouldCheck(seriesName, language);

            if (seriesName.equals(UNMATCHED_SERIES_NAME)) return this.streamFor(EMPTY_SEARCH_XML);

            if (seriesName.equals(MATCHED_SERIES_NAME)) return this.streamFor(NON_EMPTY_SEARCH_XML);

            if (seriesName.equals(SERIES_NAME_FOR_INVALID_XML)) return this.streamFor(SEARCH_XML_WITHOUT_ROOT_ELEMENT);

            throw new StreamCreationFailedException();
        }

        @Override
        public InputStream streamForSeriesPoster(String resourcePath) {return null;}

        @Override
        public InputStream streamForEpisodeImage(String fileName) {return null;}
    }

    /* Parser to test */

    private SeriesSearchParser seriesSearchParser = new SeriesSearchParser(new SeriesSearchParserTestStreamFactory());

    /* Construction */

    public void testConstructingSeriesParserWithNullStreamFactoryCausesIllegalArgumentException() {
        try {
            new SeriesSearchParser(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    /* Parse */

    public void testBlankSeriesNameCausesIllegalArgumentExceptionAtTheParsing()
            throws ParsingFailedException, StreamCreationFailedException, ConnectionFailedException {
        try {
            this.seriesSearchParser.parse("", Language.ENGLISH);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testNullLanguageCausesIllegalArgumentExceptionAtTheParsing()
            throws ParsingFailedException, StreamCreationFailedException, ConnectionFailedException {
        try {
            this.seriesSearchParser.parse(MATCHED_SERIES_NAME, null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testParsingInvalidXmlCausesParsingFailedException()
            throws StreamCreationFailedException, ConnectionFailedException {
        try {
            this.seriesSearchParser.parse(SERIES_NAME_FOR_INVALID_XML, Language.ENGLISH);
            fail("Should have thrown an ParsingFailedException");
        } catch (ParsingFailedException e) {}
    }

    public void testSeriesParserParsesEmptySearchXml()
            throws ParsingFailedException, StreamCreationFailedException, ConnectionFailedException {
        List<Series> searchResult = this.seriesSearchParser.parse(UNMATCHED_SERIES_NAME, Language.ENGLISH);

        assertThat(searchResult.size(), equalTo(0));
    }

    public void testSeriesParserParsesNonEmptySearchXml()
            throws ParsingFailedException, StreamCreationFailedException, ConnectionFailedException {
        List<Series> searchResult = this.seriesSearchParser.parse(MATCHED_SERIES_NAME, Language.ENGLISH);

        assertThat(searchResult.size(), equalTo(3));

        for (Series s : searchResult) {
            if (s.id() == Integer.valueOf(SERIES1_ID)) {
                assertThat(s.name(), equalTo(SERIES1_NAME));
                assertThat(s.overview(), equalTo(SERIES1_OVERVIEW));
                continue;
            }

            if (s.id() == Integer.valueOf(SERIES2_ID)) {
                assertThat(s.name(), equalTo(SERIES2_NAME));
                assertThat(s.overview(), equalTo(SERIES2_OVERVIEW));
                continue;
            }

            if (s.id() == Integer.valueOf(SERIES3_ID)) {
                assertThat(s.name(), equalTo(SERIES3_NAME));
                assertThat(s.overview(), equalTo(SERIES3_OVERVIEW));
                continue;
            }

            fail("Series id should be one of the given above");
        }
    }
}
