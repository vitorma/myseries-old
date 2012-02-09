/*
 *   EpisodeElementHandler.java
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

import java.text.SimpleDateFormat;
import java.util.Date;

import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Numbers;
import br.edu.ufcg.aweseries.util.Strings;
import br.edu.ufcg.aweseries.util.Validate;

public class EpisodeElementHandler {
    private static final String EPISODE = "Episode";
    private static final String ID = "id";
    private static final String SERIES_ID = "seriesid";
    private static final String NUMBER = "EpisodeNumber";
    private static final String SEASON_NUMBER = "SeasonNumber";
    private static final String NAME = "EpisodeName";
    private static final String AIR_DATE = "FirstAired";
    private static final String OVERVIEW = "Overview";
    private static final String DIRECTORS = "Director";
    private static final String WRITERS = "Writer";
    private static final String GUEST_STARS = "GuestStars";
    private static final String IMAGE_FILE_NAME = "filename";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Element episodeElement;
    private Episode.Builder episodeBuilder;

    //Construction------------------------------------------------------------------------------------------------------

    private EpisodeElementHandler(RootElement rootElement) {
        Validate.isNonNull(rootElement, "rootElement");

        this.episodeElement = rootElement.requireChild(EPISODE);
        this.episodeBuilder = Episode.builder();
    }

    //Factory-----------------------------------------------------------------------------------------------------------

    public static EpisodeElementHandler from(RootElement rootElement) {
        return new EpisodeElementHandler(rootElement);
    }

    //Episode element---------------------------------------------------------------------------------------------------

    public Element episodeElement() {
        return this.episodeElement;
    }

    //Content handling--------------------------------------------------------------------------------------------------

    public EpisodeElementHandler handlingId() {
        this.episodeElement.getChild(ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int id = Numbers.parseInt(body, Episode.INVALID_ID);
                EpisodeElementHandler.this.episodeBuilder.withId(id);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingSeriesId() {
        this.episodeElement.getChild(SERIES_ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int seriesId = Numbers.parseInt(body, Series.INVALID_ID);
                EpisodeElementHandler.this.episodeBuilder.withSeriesId(seriesId);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingNumber() {
        this.episodeElement.getChild(NUMBER).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int number = Numbers.parseInt(body, Episode.INVALID_NUMBER);
                EpisodeElementHandler.this.episodeBuilder.withNumber(number);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingSeasonNumber() {
        this.episodeElement.getChild(SEASON_NUMBER).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int seasonNumber = Numbers.parseInt(body, Season.INVALID_NUMBER);
                EpisodeElementHandler.this.episodeBuilder.withSeasonNumber(seasonNumber);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingName() {
        this.episodeElement.getChild(NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElementHandler.this.episodeBuilder.withName(body);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingAirDate() {
        this.episodeElement.getChild(AIR_DATE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                Date airDate = Dates.parseDate(body, DATE_FORMAT, null);
                EpisodeElementHandler.this.episodeBuilder.withAirDate(airDate);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingOverview() {
        this.episodeElement.getChild(OVERVIEW).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElementHandler.this.episodeBuilder.withOverview(body);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingDirectors() {
        this.episodeElement.getChild(DIRECTORS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String directors = Strings.normalizePipeSeparated(body);
                EpisodeElementHandler.this.episodeBuilder.withDirectors(directors);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingWriters() {
        this.episodeElement.getChild(WRITERS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String writers = Strings.normalizePipeSeparated(body);
                EpisodeElementHandler.this.episodeBuilder.withWriters(writers);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingGuestStars() {
        this.episodeElement.getChild(GUEST_STARS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String guestStars = Strings.normalizePipeSeparated(body);
                EpisodeElementHandler.this.episodeBuilder.withGuestStars(guestStars);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingImageFileName() {
        this.episodeElement.getChild(IMAGE_FILE_NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElementHandler.this.episodeBuilder.withImageFileName(body);
            }
        });

        return this;
    }

    //Handled element---------------------------------------------------------------------------------------------------

    public Episode handledElement() {
        return this.episodeBuilder.build();
    }
}
