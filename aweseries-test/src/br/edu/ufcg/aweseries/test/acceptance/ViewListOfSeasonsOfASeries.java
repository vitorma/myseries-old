package br.edu.ufcg.aweseries.test.acceptance;

import junit.framework.AssertionFailedError;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.gui.MySeries;
import br.edu.ufcg.aweseries.test.acceptance.util.AppDriver;
import br.edu.ufcg.aweseries.test.acceptance.util.TestStreamFactory;
import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ViewListOfSeasonsOfASeries extends ActivityInstrumentationTestCase2<MySeries> {
    private Solo solo;
    private AppDriver driver;

    public ViewListOfSeasonsOfASeries() {
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
     */
    public void setUp() {
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

    // Tests -------------------------------------------------------------------

    // XXX: this test is used as an workaround to the fact that everytime the tests are run, the
    // first TestCase fails. Remove it as soon as possible.
    public void testAAANothing() {
        this.driver().follow("Chuck");
        try {
            this.driver().viewSeasonsOf("Chuck");
        } catch (AssertionFailedError e) {}
    }

    public void testListTitle() {
        // Given
        this.driver().follow("Chuck");

        // When
        this.driver().viewSeasonsOf("Chuck");

        //Then
        assertThat(this.solo().searchText("Chuck's Seasons"), equalTo(true));
    }

    public void testSeriesHasSpecialEpisodes() {
        // Given
        this.driver().follow("Chuck");

        // When
        this.driver().viewSeasonsOf("Chuck");

        // Then
        assertThat(this.solo().searchText("Special Episodes"), equalTo(true));
    }

    public void testSeriesDoesNotHaveSeason0() {
        // Given
        this.driver().follow("Chuck");

        // When
        this.driver().viewSeasonsOf("Chuck");

        // Then
        assertThat(this.solo().searchText("Season 0"), equalTo(false));
    }

    public void testSeriesHasFiveNormalSeasons() {
        // Given
        this.driver().follow("Chuck");

        // When
        this.driver().viewSeasonsOf("Chuck");

        // Then
        assertThat(this.solo().searchText("Season 1"), equalTo(true));
        assertThat(this.solo().searchText("Season 2"), equalTo(true));
        assertThat(this.solo().searchText("Season 3"), equalTo(true));
        assertThat(this.solo().searchText("Season 4"), equalTo(true));
        assertThat(this.solo().searchText("Season 5"), equalTo(true));
    }
}
