package br.edu.ufcg.aweseries.test.acceptance.util;

import android.test.ActivityInstrumentationTestCase2;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.gui.MySeries;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

import com.jayway.android.robotium.solo.Solo;

public class AcceptanceTestCase extends ActivityInstrumentationTestCase2<MySeries> {

    private Solo solo;
    private AppDriver driver;

    public AcceptanceTestCase() {
        super("br.edu.ufcg.aweseries.gui", MySeries.class);
    }

    protected Solo solo() {
        return this.solo;
    }

    protected AppDriver driver() {
        return this.driver;
    }

    /**
     * Must be called before the test's setUp commands.
     *
     * @param instrumentation ActivityInstrumentationTestCase2.getInstrumentation();
     * @param activity ActivityInstrumentationTestCase2.getActivity();
     */
    public void setUp() {
        App.environment().setTheTVDBTo(new TheTVDB(new TestStreamFactory()));

        // XXX: Avoids instantiation of the default SeriesProvider with some example series.
        // It should not be necessary when the user may start following series.
        App.environment().setSeriesProvider(SeriesProvider.newSeriesProvider());

        this.solo = new Solo(getInstrumentation(), getActivity());
        this.driver = new AppDriver(this.solo);
    }

    /**
     * Must be called before the test's tear down commands
     */
    public void tearDown() throws Exception {
        try {
            this.solo.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        getActivity().finish();
        super.tearDown();
    }
}
