package br.edu.ufcg.aweseries.test.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.AssertionFailedError;
import android.test.ActivityInstrumentationTestCase2;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.gui.MySeries;
import br.edu.ufcg.aweseries.test.acceptance.util.AppDriver;
import br.edu.ufcg.aweseries.test.acceptance.util.TestStreamFactory;
import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

import com.jayway.android.robotium.solo.Solo;

public class ViewSeriesInformation extends
        ActivityInstrumentationTestCase2<MySeries> {

    private Solo solo;
    private AppDriver driver;
    
    public ViewSeriesInformation() {
        super("br.edu.ufcg.aweseries.gui", MySeries.class);
    }

    protected Solo solo() {
        return this.solo;
    }

    protected AppDriver driver() {
        return this.driver;
    }

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

    public void tearDown() throws Exception {
        this.clearUserData();

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
        String seriesName = "Chuck";
        this.driver().follow(seriesName);
        try {
            goToFollowedSeries(seriesName);
        } catch (AssertionFailedError e) {}
    }

    public void testGetToSeriesInformationFromFollowedSeries() {
        // Given
        String seriesName = "Chuck";
        this.driver().follow(seriesName);
        goToFollowedSeries(seriesName);
        
        // When

        // Then
        assertThat(this.solo().searchText(seriesName), is(true));
    }
    
    public void testSeriesInformationContent() {
        this.testGetToSeriesInformationFromFollowedSeries();
        
        String[] fields = {
                           "Chuck",           // name
                           "Continuing",      // status
                                              "First aired",
                           "Friday",          "Air days",
                                              "Air time",
                           "60 minutes",      "Runtime",
                           "NBC",             "Network",
                           "Drama", "Action", "Genre",
                           "Zachary Levi",    "Actors",
                           "ace computer geek at Buy More" // overview
                           
        };
        
        for (String field : fields) {
            assertThat("Field "+ field + " was not found",
                       this.solo().searchText(field), is(true));
        }
    }
    
    private void goToFollowedSeries(String seriesName) {
        this.solo().clickOnMenuItem(seriesName);
    }
}
