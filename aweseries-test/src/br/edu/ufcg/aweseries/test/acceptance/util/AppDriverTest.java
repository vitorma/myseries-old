package br.edu.ufcg.aweseries.test.acceptance.util;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.gui.MySeries;
import br.edu.ufcg.aweseries.gui.SeasonsView;
import br.edu.ufcg.aweseries.gui.SeriesView;
import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppDriverTest extends ActivityInstrumentationTestCase2<MySeries> {
    private Solo solo;
    private AppDriver driver;

    public AppDriverTest() {
        super(MySeries.class);
    }

    protected Solo solo() {
        return this.solo;
    }

    protected AppDriver driver() {
        return this.driver;
    }

    /**
     * Must be called before the test's setUp commands.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        this.setUpTestStreamFactory();
        this.clearUserData();
        this.setUpTestTools();
    }

    private void setUpTestStreamFactory() {
        SampleSeries.injectInstrumentation(getInstrumentation());
        App.environment().setTheTVDBTo(new TheTVDB(new TestStreamFactory()));
    }

    private void clearUserData() {
        // XXX: It is here because the user can't follow a series yet. Remove it ASAP
        App.environment().getSeriesProvider().loadExampleData = false;

        App.environment().getSeriesProvider().wipeFollowedSeries();
    }

    private void setUpTestTools() {
        this.solo = new Solo(getInstrumentation(), getActivity());
        this.driver = new AppDriver(this.solo);
    }

    /**
     * Must be called after the test's tear down commands
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

    private SampleSeries sampleSeries = SampleSeries.CHUCK;

    public void testNullSoloThrowsAnException() {
        try {
            new AppDriver(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testViewMyFollowedSeries() {
        this.driver().viewMyFollowedSeries();

        assertThat(this.solo().getCurrentActivity(), instanceOf(MySeries.class));
    }

    public void testViewMyFollowedSeriesAfterViewingItsSeasons() {
        this.driver().follow(sampleSeries.name());
        this.driver().viewSeasonsOf(sampleSeries.name());
        this.driver().viewMyFollowedSeries();

        assertThat(this.solo().getCurrentActivity(), instanceOf(MySeries.class));
    }

    public void testViewDetailsOfSeries() {
        this.driver().follow(sampleSeries.name());
        this.driver().viewDetailsOf(sampleSeries.name());

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesView.class));
    }

    public void testViewSeasonsOf() {
        this.driver().follow(sampleSeries.name());
        this.driver().viewSeasonsOf(sampleSeries.name());

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeasonsView.class));
    }

    public void testViewDetailsOfSeriesAfterViewingItsSeasons() {
        this.driver().follow(sampleSeries.name());
        this.driver().viewSeasonsOf(sampleSeries.name());
        this.driver().viewDetailsOf(sampleSeries.name());

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesView.class));
    }
}
