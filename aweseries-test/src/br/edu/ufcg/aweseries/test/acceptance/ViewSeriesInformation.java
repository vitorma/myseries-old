package br.edu.ufcg.aweseries.test.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.test.ActivityInstrumentationTestCase2;
import br.edu.ufcg.aweseries.gui.MySeries;

import com.jayway.android.robotium.solo.Solo;

public class ViewSeriesInformation extends
        ActivityInstrumentationTestCase2<MySeries> {

    private Solo solo;
    
    public ViewSeriesInformation() {
        super("br.edu.ufcg.aweseries.gui", MySeries.class);
    }

    public void setUp() {
        this.solo = new Solo(getInstrumentation(), getActivity());
    }

    public void tearDown() throws Exception {
        try {
            this.solo.finalize();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        getActivity().finish();
        super.tearDown();
    }

    public void testGetToSeriesInformationFromFollowedSeries() {
        // Given
        String seriesName = "Chuck";
        followSeries(seriesName);
        goToFollowedSeries(seriesName);
        
        // When

        // Then
        assertThat(solo.searchText(seriesName), is(true));
    }
    
    public void testSeriesInformationContent() {
        this.testGetToSeriesInformationFromFollowedSeries();
        
        String[] fields = {
                           "Chuck",           // name
                           "Continuing",      // status
                                              "First aired",
                           "Monday",          "Air days",
                                              "Air time",
                           "60 minutes",      "Runtime",
                           "NBC",             "Network",
                           "Drama", "Action", "Genre",
                           "Zachary Levi",    "Actors",
                           "ace computer geek at Buy More" // overview
                           
        };
        
        for (String field : fields) {
            assertThat("Field "+ field + " was not found",
                       solo.searchText(field), is(true));
        }
    }
    
    private void goToFollowedSeries(String seriesName) {
        solo.clickOnMenuItem(seriesName);
    }

    private void followSeries(String seriesName) {
        // TODO: add the series to the user's list of followed series
        // TODO: maybe it should be in an Application Driver class
    }
}
