package br.edu.ufcg.aweseries.test.util;

import java.io.InputStream;

import android.app.Instrumentation;
import android.content.res.Resources;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.R;
import br.edu.ufcg.aweseries.thetvdb.parsing.EpisodesParser;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesParser;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public abstract class SampleSeries {
    public static final SampleSeries CHUCK = new SampleSeries() {

        @Override
        public String posterResourcePath() {
            return "posters/80348-16.jpg";
        }

        @Override
        protected String id() {
            return "80348";
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

    public abstract String posterResourcePath();

    public abstract InputStream baseSeriesStream();
    public abstract InputStream fullSeriesStream();
    public abstract InputStream posterStream();

    protected abstract String id();

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
            public InputStream streamForSeriesPosterAt(String resourcePath) {
                return posterStream();
            }
        });

        final Series series = seriesParser.parse(this.id());

        final EpisodesParser episodesParser = new EpisodesParser(fullSeriesStream());
        series.getSeasons().addAllEpisodes(episodesParser.parse());

        return series;
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
