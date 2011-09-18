package br.edu.ufcg.aweseries.test.acceptance;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import android.test.ActivityInstrumentationTestCase2;
import br.edu.ufcg.aweseries.gui.MySeries;

import com.jayway.android.robotium.solo.Solo;

public class ViewListOfFollowedSeries extends
        ActivityInstrumentationTestCase2<MySeries> {

    private Solo solo;
    
    public ViewListOfFollowedSeries() {
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

    public void testUserFollowsASeries() {
        // Given
        String seriesName = "Chuck";
        followSeries(seriesName);
        
        // When

        // Then 
        assertThat(solo.searchText(seriesName), is(true));
    }
    
    private void followSeries(String seriesName) {
        // TODO: add the series to the user's list of followed series
        // TODO: maybe it should be in an Application Driver class
    }
}
