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
        });

        return seriesParser.parse(this.id());
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
