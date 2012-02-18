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
import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.util.Validate;

public class SeriesParser {
    private StreamFactory streamFactory;

    //Construction------------------------------------------------------------------------------------------------------

    public SeriesParser(StreamFactory streamFactory) {
        Validate.isNonNull(streamFactory, "streamFactory");

        this.streamFactory = streamFactory;
    }

    //Parse-------------------------------------------------------------------------------------------------------------

    public Series parse(int seriesId, Language language) {
        InputStream stream = this.streamFactory.streamForSeries(seriesId, language);

        Content content = new Content();

        try {
            Xml.parse(stream, Xml.Encoding.UTF_8, content.handler());
        } catch (IOException e) {
            throw new ParsingFailedException(e);
        } catch (SAXException e) {
            throw new ParsingFailedException(e);
        }

        return content.handled();
    }

    //Content-----------------------------------------------------------------------------------------------------------

    private static class Content {
        private static final String DATA = "Data";

        private RootElement rootElement;
        private SeriesElementHandler seriesElementHandler;
        private EpisodeElementHandler episodeElementHandler;

        private Content() {
            this.rootElement = new RootElement(DATA);

            this.seriesElementHandler = SeriesElementHandler.from(this.rootElement)
                .handlingId()
                .handlingName()
                .handlingStatus()
                .handlingAirDay()
                .handlingAirTime()
                .handlingAirDate()
                .handlingRuntime()
                .handlingNetwork()
                .handlingOverview()
                .handlingGenres()
                .handlingActors()
                .handlingPosterFileName();

            this.episodeElementHandler = EpisodeElementHandler.from(this.rootElement)
                .handlingId()
                .handlingSeriesId()
                .handlingNumber()
                .handlingSeasonNumber()
                .handlingName()
                .handlingAirDate()
                .handlingOverview()
                .handlingDirectors()
                .handlingWriters()
                .handlingGuestStars()
                .handlingImageFileName();
        }

        private ContentHandler handler() {
            return this.rootElement.getContentHandler();
        }

        private Series handled() {
            Series series = this.seriesElementHandler.currentResult();
            List<Episode> episodes = this.episodeElementHandler.allResults();
            return series.includingAll(episodes);
        }
    }
}