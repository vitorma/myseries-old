package br.edu.ufcg.aweseries.test.acceptance;

import br.edu.ufcg.aweseries.test.acceptance.util.AcceptanceTestCase;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ViewListOfFollowedSeries extends AcceptanceTestCase {

    public void testUserFollowsASeries() {
        // Given
        this.driver().follow("Chuck");

        // When
        this.driver().viewMyFollowedSeries();

        // Then
        this.driver().assertThatSeries("Chuck").name().isShown();
        this.driver().assertThatSeries("Chuck").status().isShown();
    }

    public void testViewTitle() {
        // When
        this.driver().viewMyFollowedSeries();

        // Then
        assertThat(this.solo().searchText("My Series"), equalTo(true));
    }
}
