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

package mobi.myseries.domain.source;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Numbers;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.Validate;

import org.xml.sax.Attributes;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;

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

    private Element episodeElement;
    private Episode.Builder episodeBuilder;
    private List<Episode> results;

    private EpisodeElementHandler(RootElement rootElement) {
        Validate.isNonNull(rootElement, "rootElement");

        this.episodeElement = rootElement.getChild(EPISODE);
        this.results = new LinkedList<Episode>();

        this.initializeTheBuilderAtTheStartOfEachEpisodeElement();
        this.storeTheCurrentResultAtTheEndOfEachEpisodeElement();
    }

    public static EpisodeElementHandler from(RootElement rootElement) {
        return new EpisodeElementHandler(rootElement);
    }

    private void storeTheCurrentResultAtTheEndOfEachEpisodeElement() {
        this.episodeElement.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                EpisodeElementHandler.this.results.add(EpisodeElementHandler.this.currentResult());
            }
        });
    }

    private void initializeTheBuilderAtTheStartOfEachEpisodeElement() {
        this.episodeElement.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                EpisodeElementHandler.this.episodeBuilder = Episode.builder();
            }
        });
    }

    public EpisodeElementHandler handlingId() {
        this.episodeElement.getChild(ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int id = Numbers.parseInt(body.trim(), Invalid.EPISODE_ID);
                EpisodeElementHandler.this.episodeBuilder.withId(id);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingSeriesId() {
        this.episodeElement.getChild(SERIES_ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int seriesId = Numbers.parseInt(body.trim(), Invalid.SERIES_ID);
                EpisodeElementHandler.this.episodeBuilder.withSeriesId(seriesId);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingNumber() {
        this.episodeElement.getChild(NUMBER).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int number = Numbers.parseInt(body.trim(), Invalid.EPISODE_NUMBER);
                EpisodeElementHandler.this.episodeBuilder.withNumber(number);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingSeasonNumber() {
        this.episodeElement.getChild(SEASON_NUMBER).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int seasonNumber = Numbers.parseInt(body.trim(), Invalid.SEASON_NUMBER);
                EpisodeElementHandler.this.episodeBuilder.withSeasonNumber(seasonNumber);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingName() {
        this.episodeElement.getChild(NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElementHandler.this.episodeBuilder.withTitle(body.trim());
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingAirDate() {
        this.episodeElement.getChild(AIR_DATE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                Date airDate = DatesAndTimes.parse(body.trim(), TheTVDBConstants.DATE_FORMAT, null);
                EpisodeElementHandler.this.episodeBuilder.withAirDate(airDate);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingOverview() {
        this.episodeElement.getChild(OVERVIEW).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElementHandler.this.episodeBuilder.withOverview(body.trim());
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingDirectors() {
        this.episodeElement.getChild(DIRECTORS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String directors = Strings.normalizePipeSeparated(body.trim());
                EpisodeElementHandler.this.episodeBuilder.withDirectors(directors);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingWriters() {
        this.episodeElement.getChild(WRITERS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String writers = Strings.normalizePipeSeparated(body.trim());
                EpisodeElementHandler.this.episodeBuilder.withWriters(writers);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingGuestStars() {
        this.episodeElement.getChild(GUEST_STARS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String guestStars = Strings.normalizePipeSeparated(body.trim());
                EpisodeElementHandler.this.episodeBuilder.withGuestStars(guestStars);
            }
        });

        return this;
    }

    public EpisodeElementHandler handlingImageFileName() {
        this.episodeElement.getChild(IMAGE_FILE_NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElementHandler.this.episodeBuilder.withScreenUrl(body.trim());
            }
        });

        return this;
    }

    public Episode currentResult() {
        return this.episodeBuilder.build();
    }

    public List<Episode> allResults() {
        return this.results;
    }
}