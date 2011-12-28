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


package br.edu.ufcg.aweseries.thetvdb.parsing;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.model.EpisodeBuilder;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesBuilder;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.util.Strings;

public class SeriesParser {
    private StreamFactory streamFactory;

    public SeriesParser(StreamFactory streamFactory) {
        if (streamFactory == null) {
            throw new IllegalArgumentException("streamFactory should not be null");
        }

        this.streamFactory = streamFactory;
    }

    //TODO Refactoring: extract definition of listeners, maybe creating inner types
    public Series parse(String seriesId, String language) {
        if (seriesId == null) {
            throw new IllegalArgumentException("seriesId should not be null");
        }
        if (Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("seriesId should not be blank");
        }

        //Builders------------------------------------------------------------------------------------------------------

        final SeriesBuilder seriesBuilder = new SeriesBuilder();
        final EpisodeBuilder episodeBuilder = new EpisodeBuilder();

        //Root element--------------------------------------------------------------------------------------------------

        final RootElement root = new RootElement("Data");

        //Series element------------------------------------------------------------------------------------------------

        final Element seriesElement = root.getChild("Series");

        seriesElement.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withId(body);
                    }
                });

        seriesElement.getChild("SeriesName").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withName(body);
                    }
                });

        seriesElement.getChild("Status").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withStatus(body);
                    }
                });

        seriesElement.getChild("Airs_DayOfWeek").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withAirsDay(body);
                    }
                });

        seriesElement.getChild("Airs_Time").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withAirsTime(body);
                    }
                });

        seriesElement.getChild("FirstAired").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withFirstAired(body);
                    }
                });

        seriesElement.getChild("Runtime").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withRuntime(body);
                    }
                });

        seriesElement.getChild("Network").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withNetwork(body);
                    }
                });

        seriesElement.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withOverview(body);
                    }
                });

        seriesElement.getChild("Genre").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withGenres(Strings.normalizePipeSeparated(body));
                    }
                });

        seriesElement.getChild("Actors").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withActors(Strings.normalizePipeSeparated(body));
                    }
                });

        seriesElement.getChild("poster").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withPoster((Bitmap) SeriesParser.this.scaledBitmapFrom(body));
                    }
                });

        //Episode element-----------------------------------------------------------------------------------------------

        final Element episodeElement = root.getChild("Episode");

        episodeElement.setEndElementListener(
                new EndElementListener() {
                    @Override
                    public void end() {
                        seriesBuilder.withEpisode(episodeBuilder.build());
                    }
                });

        episodeElement.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withId(body);
                    }
                });

        episodeElement.getChild("seriesid").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withSeriesId(body);
                    }
                });

        episodeElement.getChild("EpisodeNumber").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withNumber(body);
                    }
                });

        episodeElement.getChild("SeasonNumber").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withSeasonNumber(body);
                    }
                });

        episodeElement.getChild("EpisodeName").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withName(body);
                    }
                });

        episodeElement.getChild("FirstAired").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withFirstAired(body);
                    }
                });

        episodeElement.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withOverview(body);
                    }
                });

        episodeElement.getChild("Director").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withDirector(Strings.normalizePipeSeparated(body));
                    }
                });

        episodeElement.getChild("Writer").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withWriter(Strings.normalizePipeSeparated(body));
                    }
                });

        episodeElement.getChild("GuestStars").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withGuestStars(Strings.normalizePipeSeparated(body));
                    }
                });

        episodeElement.getChild("filename").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withPoster(body);
                    }
                });

        //Parse---------------------------------------------------------------------------------------------------------

        try {
            Xml.parse(this.streamFactory.streamForFullSeries(seriesId), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        return seriesBuilder.build();
    }

    private Bitmap scaledBitmapFrom(String resourcePath) {
        return Strings.isBlank(resourcePath)
               ? null
               : BitmapFactory.decodeStream(this.streamFactory.streamForSeriesPosterAt(resourcePath));
    }
}
