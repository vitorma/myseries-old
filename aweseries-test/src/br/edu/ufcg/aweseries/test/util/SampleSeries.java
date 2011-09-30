package br.edu.ufcg.aweseries.test.util;

import java.io.InputStream;

import android.app.Instrumentation;
import android.content.res.Resources;
import br.edu.ufcg.aweseries.test.R;

public abstract class SampleSeries {
    public static final SampleSeries CHUCK = new SampleSeries() {

        @Override
        public String id() {
            return "80348";
        }

        @Override
        public String name() {
            return "Chuck";
        }

        @Override
        public String status() {
            return "Continuing";
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

    public abstract String id();
    public abstract String name();
    public abstract String status();
    public abstract String posterResourcePath();

    public abstract InputStream baseSeriesStream();
    public abstract InputStream fullSeriesStream();
    public abstract InputStream posterStream();

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
