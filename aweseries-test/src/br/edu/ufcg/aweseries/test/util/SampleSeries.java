/*
 *   SampleSeries.java
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


package br.edu.ufcg.aweseries.test.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Instrumentation;
import android.content.res.Resources;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.series_source.Language;
import br.edu.ufcg.aweseries.series_source.SeriesParser;
import br.edu.ufcg.aweseries.series_source.StreamFactory;
import br.edu.ufcg.aweseries.test.R;

public abstract class SampleSeries {
	public static final SampleSeries CHUCK = new SampleSeries() {
		@Override
		public int id() {
			return 80348;
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
		public int id() {
			return 73255;
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

	public abstract int id();
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
			public InputStream streamForSeriesPoster(String resourcePath) {
				return SampleSeries.this.posterStream();
			}

			@Override
			public InputStream streamForSeries(int seriesId, Language language) {
				// TODO Auto-generated method stub
				return SampleSeries.this.fullSeriesStream();
			}

			@Override
			public InputStream streamForSeriesSearch(String seriesName, Language language) {
				// TODO Auto-generated method stub
				return null;
			}

            @Override
            public InputStream streamForEpisodeImage(String fileName) {
                // TODO Auto-generated method stub
                return null;
            }
		});

		return seriesParser.parse(this.id(), Language.EN);
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
		if (instrumentation == null)
			throw new IllegalArgumentException("instrumentation should not be null");

		SampleSeries.resources = instrumentation.getContext().getResources();
	}

	protected InputStream rawResource(int resourceId) {
		return resources.openRawResource(resourceId);
	}
}
