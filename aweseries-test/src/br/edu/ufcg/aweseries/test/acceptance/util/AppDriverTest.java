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
        App.environment().seriesProvider().loadExampleData = false;

        App.environment().seriesProvider().wipeFollowedSeries();
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

        this.clearUserData();
    }

    private String testSeriesName = SampleSeries.CHUCK.name();

    // Construction ------------------------------------------------------------
    public void testNullSoloThrowsAnException() {
        try {
            new AppDriver(null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    // Full Actions ------------------------------------------------------------
    public void testFollowNullSeries() {
        try {
            this.driver().follow(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testFollowBlankSeries() {
        try {
            this.driver().follow("  \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    // Navigation --------------------------------------------------------------
    public void testViewMyFollowedSeries() {
        this.driver().viewMyFollowedSeries();

        assertThat(this.solo().getCurrentActivity(), instanceOf(MySeries.class));
    }

    public void testViewMyFollowedSeriesAfterViewingItsSeasons() {
        this.driver().follow(testSeriesName);
        this.driver().viewSeasonsOf(testSeriesName);
        this.driver().viewMyFollowedSeries();

        assertThat(this.solo().getCurrentActivity(), instanceOf(MySeries.class));
    }

    public void testViewDetailsOfNullSeries() {
        try {
            this.driver().viewDetailsOf(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testViewDetailsOfBlankSeries() {
        try {
            this.driver().viewDetailsOf("   \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testViewDetailsOfSeries() {
        this.driver().follow(testSeriesName);
        this.driver().viewDetailsOf(testSeriesName);

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesView.class));
    }

    public void testViewSeasonsOfNullSeries() {
        try {
            this.driver().viewSeasonsOf(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testViewSeasonsOfBlankSeries() {
        try {
            this.driver().viewSeasonsOf("   \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testViewSeasonsOfSeries() {
        this.driver().follow(testSeriesName);
        this.driver().viewSeasonsOf(testSeriesName);

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeasonsView.class));
    }

    public void testViewDetailsOfSeriesAfterViewingItsSeasons() {
        this.driver().follow(testSeriesName);
        this.driver().viewSeasonsOf(testSeriesName);
        this.driver().viewDetailsOf(testSeriesName);

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesView.class));
    }

    // Verification ------------------------------------------------------------
    public void testAssertThatNullSeries() {
        try {
            this.driver().assertThatSeries(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }
}
