/*
 *   SeriesElementHandler.java
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
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Numbers;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.Validate;
import mobi.myseries.shared.WeekDay;

import org.xml.sax.Attributes;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;

public class SeriesElementHandler {
    private static final String SERIES = "Series";
    private static final String ID = "id";
    private static final String NAME = "SeriesName";
    private static final String STATUS = "Status";
    private static final String AIR_DAY = "Airs_DayOfWeek";
    private static final String AIR_TIME = "Airs_Time";
    private static final String AIR_DATE = "FirstAired";
    private static final String RUNTIME = "Runtime";
    private static final String NETWORK = "Network";
    private static final String OVERVIEW = "Overview";
    private static final String GENRES = "Genre";
    private static final String ACTORS = "Actors";
    private static final String POSTER_FILE_NAME = "poster";

    protected Element seriesElement;
    protected Series.Builder seriesBuilder;
    private List<Series> results;

    protected SeriesElementHandler(RootElement rootElement) {
        Validate.isNonNull(rootElement, "rootElement");

        this.seriesElement = rootElement.getChild(SERIES);
        this.results = new LinkedList<Series>();

        this.initializeTheBuilderAtTheStartOfEachSeriesElement();
        this.storeTheCurrentResultAtTheEndOfEachSeriesElement();
    }

    public static SeriesElementHandler from(RootElement rootElement) {
        return new SeriesElementHandler(rootElement);
    }

    private void initializeTheBuilderAtTheStartOfEachSeriesElement() {
        this.seriesElement.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                SeriesElementHandler.this.seriesBuilder = Series.builder();
            }
        });
    }

    private void storeTheCurrentResultAtTheEndOfEachSeriesElement() {
        this.seriesElement.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                SeriesElementHandler.this.results.add(SeriesElementHandler.this.currentResult());
            }
        });
    }

    public SeriesElementHandler handlingId() {
        this.seriesElement.getChild(ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int id = Numbers.parseInt(body.trim(), Invalid.SERIES_ID);
                SeriesElementHandler.this.seriesBuilder.withId(id);
            }
        });

        return this;
    }

    public SeriesElementHandler handlingName() {
        this.seriesElement.getChild(NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElementHandler.this.seriesBuilder.withName(body.trim());
            }
        });

        return this;
    }

    public SeriesElementHandler handlingStatus() {
        this.seriesElement.getChild(STATUS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                Status status = Status.from(body.trim());
                SeriesElementHandler.this.seriesBuilder.withStatus(status);
            }
        });

        return this;
    }

    public SeriesElementHandler handlingAirDay() {
        this.seriesElement.getChild(AIR_DAY).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                WeekDay airDay = WeekDay.valueOf(body.trim());
                SeriesElementHandler.this.seriesBuilder.withAirDay(airDay);
            }
        });

        return this;
    }

    public SeriesElementHandler handlingAirTime() {
        this.seriesElement.getChild(AIR_TIME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                Time airtime = Time.valueOf(body.trim());
                SeriesElementHandler.this.seriesBuilder.withAirtime(airtime);
            }
        });

        return this;
    }

    public SeriesElementHandler handlingAirDate() {
        this.seriesElement.getChild(AIR_DATE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                Date airDate = DatesAndTimes.parse(body.trim(), TheTVDBConstants.DATE_FORMAT, null);
                SeriesElementHandler.this.seriesBuilder.withAirDate(airDate);
            }
        });

        return this;
    }

    public SeriesElementHandler handlingRuntime() {
        this.seriesElement.getChild(RUNTIME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElementHandler.this.seriesBuilder.withRuntime(body.trim());
            }
        });

        return this;
    }

    public SeriesElementHandler handlingNetwork() {
        this.seriesElement.getChild(NETWORK).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElementHandler.this.seriesBuilder.withNetwork(body.trim());
            }
        });

        return this;
    }

    public SeriesElementHandler handlingOverview() {
        this.seriesElement.getChild(OVERVIEW).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElementHandler.this.seriesBuilder.withOverview(body.trim());
            }
        });

        return this;
    }

    public SeriesElementHandler handlingGenres() {
        this.seriesElement.getChild(GENRES).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String genres = Strings.normalizePipeSeparated(body.trim());
                SeriesElementHandler.this.seriesBuilder.withGenres(genres);
            }
        });

        return this;
    }

    public SeriesElementHandler handlingActors() {
        this.seriesElement.getChild(ACTORS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String actors = Strings.normalizePipeSeparated(body.trim());
                SeriesElementHandler.this.seriesBuilder.withActors(actors);
            }
        });

        return this;
    }

    public SeriesElementHandler handlingPosterFileName() {
        this.seriesElement.getChild(POSTER_FILE_NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElementHandler.this.seriesBuilder.withPosterFileName(body.trim());
            }
        });

        return this;
    }

    public Series currentResult() {
        return this.seriesBuilder.build();
    }

    public List<Series> allResults() {
        return this.results;
    }
}