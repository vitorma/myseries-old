/*
 *   SeriesParser.java
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

package br.edu.ufcg.aweseries.series_source;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.util.Validate;

public class SeriesParser {
    private StreamFactory streamFactory;
    private RootElement rootElement;

    //Construction------------------------------------------------------------------------------------------------------

    public SeriesParser(StreamFactory streamFactory) {
        Validate.isNonNull(streamFactory, "streamFactory");

        this.streamFactory = streamFactory;
        this.rootElement = new RootElement("Data");
    }

   public Series parse(int seriesId, Language language) {
        InputStream stream = this.streamFactory.streamForSeries(seriesId, language);

        SeriesElement seriesElement = this.createSeriesElementFromRoot();

        try {
            Xml.parse(stream, Xml.Encoding.UTF_8, this.rootElement.getContentHandler());
        } catch (IOException e) {
            throw new ParsingFailedException(e);
        } catch (SAXException e) {
            throw new ParsingFailedException(e);
        }

        return seriesElement.handledContent();
    }

    //Element-----------------------------------------------------------------------------------------------------------

    private SeriesElement createSeriesElementFromRoot() {
        return SeriesElement.from(this.rootElement)
            .withId()
            .withName()
            .withStatus()
            .withAirDay()
            .withAirTime()
            .withAirDate()
            .withRuntime()
            .withNetwork()
            .withOverview()
            .withGenres()
            .withActors()
            .withPoster(this.streamFactory)
            .withHandledContentOf(this.createEpisodeElementFromRoot());
    }

    private EpisodeElement createEpisodeElementFromRoot() {
        return EpisodeElement.from(this.rootElement)
            .withId()
            .withSeriesId()
            .withNumber()
            .withSeasonNumber()
            .withName()
            .withAirdate()
            .withOverview()
            .withDirectors()
            .withWriters()
            .withGuestStars()
            .withImageFileName();
    }
}
