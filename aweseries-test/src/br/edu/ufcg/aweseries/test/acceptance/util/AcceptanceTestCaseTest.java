package br.edu.ufcg.aweseries.test.acceptance.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.gui.MySeries;

public class AcceptanceTestCaseTest extends AcceptanceTestCase {

    public void setUp() throws Exception {
        super.setUp();

        assertThat(this.solo(), notNullValue());
        assertThat(this.driver(), notNullValue());
    }

    public void testUserFollowsNoSeries() {
        SeriesProvider provider = App.environment().getSeriesProvider();

        assertThat(provider.mySeries().length, equalTo(0));
    }

    public void testSoloIsNotNull() {
        assertThat(this.solo(), notNullValue());
    }

    public void testDriverIsNotNull() {
        assertThat(this.driver(), notNullValue());
    }

    public void testSoloRunsMySeries() {
        assertThat(this.solo().getCurrentActivity(), instanceOf(MySeries.class));
    }
}
