package br.edu.ufcg.aweseries.test.acceptance.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import android.test.ActivityInstrumentationTestCase2;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.gui.EpisodeListActivity;
import br.edu.ufcg.aweseries.gui.SeasonListActivity;
import br.edu.ufcg.aweseries.gui.SeriesDetailsActivity;
import br.edu.ufcg.aweseries.gui.SeriesListActivity;
import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

import com.jayway.android.robotium.solo.Solo;

public class AppDriverTest extends ActivityInstrumentationTestCase2<SeriesListActivity> {
    private Solo solo;
    private AppDriver driver;

    public AppDriverTest() {
        super(SeriesListActivity.class);
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
        App.environment().seriesProvider().wipeFollowedSeries();
    }

    private void setUpTestTools() {
        this.solo = new Solo(getInstrumentation());
        this.driver = new AppDriver(this.solo);
    }

    /**
     * Must be called after the test's tear down commands
     */
    @Override
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

    private String testSeriesName = "Chuck";
    private String testSeasonName = "Season 1";

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

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesListActivity.class));
    }

    public void testViewMyFollowedSeriesAfterViewingItsSeasons() {
        this.driver().follow(testSeriesName);
        this.driver().viewSeasonsOf(testSeriesName);
        this.driver().viewMyFollowedSeries();

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesListActivity.class));
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

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesDetailsActivity.class));
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

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeasonListActivity.class));
    }

    public void testViewEpisodesOfANullSeries() {
        try {
            this.driver().viewEpisodesOf(null, testSeasonName);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testViewEpisodesOfABlankSeries() {
        try {
            this.driver().viewEpisodesOf("   \t ", testSeasonName);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }
    public void testViewEpisodesOfANullSeason() {
        try {
            this.driver().viewEpisodesOf(testSeriesName, null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testViewEpisodesOfABlankSeason() {
        try {
            this.driver().viewEpisodesOf(testSeasonName, "   \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testViewEpisodesOfASeason() {
        this.driver().follow(testSeriesName);
        this.driver().viewEpisodesOf(testSeriesName, testSeasonName);

        assertThat(this.solo().getCurrentActivity(), instanceOf(EpisodeListActivity.class));
    }

    public void testViewDetailsOfSeriesAfterViewingItsSeasons() {
        this.driver().follow(testSeriesName);
        this.driver().viewSeasonsOf(testSeriesName);
        this.driver().viewDetailsOf(testSeriesName);

        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesDetailsActivity.class));
    }

    // Verification ------------------------------------------------------------
    public void testAssertThatNullSeriesThrowsException() {
        try {
            this.driver().assertThatSeries(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testAssertThatEmptySeriesThrowsException() {
        try {
            this.driver().assertThatSeries("   \t ");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testAssertThatNotFollowedSeriesThrowsException() {
        try {
            this.driver().assertThatSeries("House");
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testAssertThatRightSeriesReturnsSomeData() {
        this.driver().follow(testSeriesName);

        assertThat(this.driver().assertThatSeries(testSeriesName), notNullValue());
    }

    public void testAssertThatSeriesNullSeasonThrowsException() {
        try {
            this.driver().assertThatSeries(testSeriesName).season(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testAssertThatSeriesEmptySeasonThrowsException() {
        try {
            this.driver().follow(testSeriesName);

            this.driver().assertThatSeries(testSeriesName).season("   \t ");

            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testAssertThatSeriesInvalidSeasonThrowsException() {
        try {
            this.driver().follow(testSeriesName);

            this.driver().assertThatSeries(testSeriesName).season("Season Blah");

            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testAssertThatSeriesInvalidSeasonNumberThrowsException() {
        try {
            this.driver().follow(testSeriesName);

            this.driver().assertThatSeries(testSeriesName).season("-1");

            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testAssertThatSeriesSpecialEpisodesSeasonReturnsRightSeason() {
        this.driver().follow(testSeriesName);

        final String seasonName = this.driver().assertThatSeries(testSeriesName)
                .season("Special Episodes").name().text();

        assertThat(seasonName, equalTo("Special Episodes"));
    }

    public void testAssertThatSeriesUsualSeasonReturnsRightSeason() {
        this.driver().follow(testSeriesName);

        final String seasonName = this.driver().assertThatSeries(testSeriesName)
                .season("1").name().text();

        assertThat(seasonName, equalTo("Season 1"));
    }
}
