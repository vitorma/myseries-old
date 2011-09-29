package br.edu.ufcg.aweseries.test.acceptance;

import br.edu.ufcg.aweseries.test.acceptance.util.AcceptanceTestCase;
import br.edu.ufcg.aweseries.test.util.SampleSeries;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ViewListOfFollowedSeries extends AcceptanceTestCase {

    public void testUserFollowsASeries() {
        SampleSeries sample = SampleSeries.CHUCK;
        // Given
        this.driver().follow(sample.name());

        // When
        // app is loaded

        // Then
        assertThat(this.solo().searchText(sample.name()), equalTo(true));
    }

    public void testViewTitle() {
        assertThat(this.solo().searchText("My Series"), equalTo(true));
    }
}
