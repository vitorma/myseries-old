package br.edu.ufcg.aweseries.test.acceptance;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import br.edu.ufcg.aweseries.test.acceptance.util.AcceptanceTestCase;

public class ViewListOfFollowedSeries extends AcceptanceTestCase {

    public void testUserFollowsASeries() {
        // Given
        this.driver().follow("Chuck");

        // When
        this.driver().viewMyFollowedSeries();

        // Then
        assertThat(this.solo().searchText("My Series"), equalTo(true));

        this.driver().assertThatSeries("Chuck").name().isShown();
        this.driver().assertThatSeries("Chuck").status().isShown();
    }
}
