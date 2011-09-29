package br.edu.ufcg.aweseries.test.acceptance;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import br.edu.ufcg.aweseries.test.acceptance.util.AcceptanceTestCase;
import br.edu.ufcg.aweseries.test.util.SampleSeries;

public class ViewSeriesInformation extends AcceptanceTestCase {

    public void testGetToSeriesInformationFromFollowedSeries() {
        // Given
        String seriesName = SampleSeries.CHUCK.name();
        this.driver().follow(seriesName);
        goToFollowedSeries(seriesName);
        
        // When

        // Then
        assertThat(this.solo().searchText(seriesName), equalTo(true));
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
