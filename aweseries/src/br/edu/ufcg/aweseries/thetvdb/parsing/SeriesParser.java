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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Numbers;
import br.edu.ufcg.aweseries.util.Strings;

public class SeriesParser {
    private static final int INVALID_EPISODE_ID = -1;
    private static final int INVALID_EPISODE_NUMBER = -1;
    private static final int INVALID_SEASON_NUMBER = -1;
    private static final int INVALID_SERIES_ID = -1;
    private static final DateFormat THETVDB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private StreamFactory streamFactory;

    public SeriesParser(StreamFactory streamFactory) {
        if (streamFactory == null)
            throw new IllegalArgumentException("streamFactory should not be null");

        this.streamFactory = streamFactory;
    }

    //TODO Refactoring: extract definition of listeners, maybe creating inner types
    public Series parse(int seriesId, String language) {

        //Builders------------------------------------------------------------------------------------------------------

        final Series.Builder seriesBuilder = new Series.Builder();
        final Episode.Builder episodeBuilder = Episode.builder();

        //Root element--------------------------------------------------------------------------------------------------

        final RootElement root = new RootElement("Data");

        //Series element------------------------------------------------------------------------------------------------

        final Element seriesElement = root.getChild("Series");

        seriesElement.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        seriesBuilder.withId(Numbers.parseInt(body, INVALID_SERIES_ID));
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
                        seriesBuilder.withPoster(SeriesParser.this.scaledBitmapFrom(body));
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
                        episodeBuilder.withId(Numbers.parseInt(body, INVALID_EPISODE_ID));
                    }
                });

        episodeElement.getChild("seriesid").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withSeriesId(Numbers.parseInt(body, INVALID_SERIES_ID));
                    }
                });

        episodeElement.getChild("EpisodeNumber").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withNumber(Numbers.parseInt(body, INVALID_EPISODE_NUMBER));
                    }
                });

        episodeElement.getChild("SeasonNumber").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withSeasonNumber(Numbers.parseInt(body, INVALID_SEASON_NUMBER));
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
                        episodeBuilder.withAirdate(Dates.parseDate(body, THETVDB_DATE_FORMAT, null));
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
                        episodeBuilder.withDirectors(Strings.normalizePipeSeparated(body));
                    }
                });

        episodeElement.getChild("Writer").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        episodeBuilder.withWriters(Strings.normalizePipeSeparated(body));
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
                        episodeBuilder.withImageFileName(body);
                    }
                });

        //Parse---------------------------------------------------------------------------------------------------------

        try {
            Xml.parse(this.streamFactory.streamForFullSeries(String.valueOf(seriesId), language), Xml.Encoding.UTF_8, root.getContentHandler());
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
