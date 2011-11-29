/*
 *   SampleSeries.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.test.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Instrumentation;
import android.content.res.Resources;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.R;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesParser;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public abstract class SampleSeries {
    public static final SampleSeries CHUCK = new SampleSeries() {
        @Override
        public String id() {
            return "80348";
        }

        @Override
        public String posterResourcePath() {
            return "posters/80348-16.jpg";
        }

        @Override
        public InputStream baseSeriesStream() {
            return this.rawResource(R.raw.chuck_base_series);
        }

        @Override
        public InputStream fullSeriesStream() {
            return this.rawResource(R.raw.chuck_full_series);
        }

        @Override
        public InputStream posterStream() {
            return this.rawResource(R.raw.chuck_poster_16);
        }
    };

    public static final SampleSeries HOUSE = new SampleSeries() {
        @Override
        public String id() {
            return "73255";
        }

        @Override
        public String posterResourcePath() {
            return "posters/73255-37.jpg";
        }

        @Override
        public InputStream baseSeriesStream() {
            return this.rawResource(R.raw.house_base_series);
        }

        @Override
        public InputStream fullSeriesStream() {
            return this.rawResource(R.raw.house_full_series);
        }

        @Override
        public InputStream posterStream() {
            return this.rawResource(R.raw.house_poster_37);
        }
    };

    public static final Set<SampleSeries> allSamples
            = new HashSet<SampleSeries>(Arrays.asList(CHUCK, HOUSE));

    public abstract String id();
    public abstract String posterResourcePath();

    public abstract InputStream baseSeriesStream();
    public abstract InputStream fullSeriesStream();
    public abstract InputStream posterStream();

    /**
     * @see TheTVDB.getFullSeries()
     */
    public Series series() {
        final SeriesParser seriesParser = new SeriesParser(new StreamFactory() {
            @Override
            public InputStream streamForBaseSeries(String seriesId) {
                return baseSeriesStream();
            }

            @Override
            public InputStream streamForFullSeries(String seriesId) {
                return fullSeriesStream();
            }

            @Override
            public InputStream streamForSeriesSearch(String seriesName) {
                //TODO Auto-generated method stub
                return null;
            }

            @Override
            public InputStream streamForSeriesPosterAt(String resourcePath) {
                return posterStream();
            }

			@Override
			public InputStream streamForFullSeries(String seriesId, String language) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream streamForSeriesSearch(String seriesName, String language) {
				// TODO Auto-generated method stub
				return null;
			}
        });

        return seriesParser.parse(this.id(), "en");
    }

    private static Resources resources;

    /**
     * To get the test data from the test project's resources, we need the
     * getInstrumentation().getContext().getResources() from the InstrumentationTests where these
     * samples will run. If I could find a better way to do that, you wouldn't see this doc. Sorry.
     *
     * @param instrumentation InstrumentationTestCase.getInstrumentation()
     */
    public static void injectInstrumentation(Instrumentation instrumentation) {
        if (instrumentation == null) {
            throw new IllegalArgumentException("instrumentation should not be null");
        }

        SampleSeries.resources = instrumentation.getContext().getResources();
    }

    protected InputStream rawResource(int resourceId) {
        return resources.openRawResource(resourceId);
    }
}
