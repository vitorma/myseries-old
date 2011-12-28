package br.edu.ufcg.aweseries.test.matchers;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.DefaultSeriesFactory;

import junit.framework.TestCase;

import static br.edu.ufcg.aweseries.test.matchers.SeriesMatchers.*;

public class SeriesMatchersTest extends TestCase {

    private DefaultSeriesFactory seriesFactory = new DefaultSeriesFactory();

    public void testIdMatching() {
        Series series = this.seriesFactory.createSeries("id : 123");
        assertTrue(hasId("123").matches(series));
        assertFalse(hasId("321").matches(series));
    }

    public void testNameMatching() {
        Series series = this.seriesFactory.createSeries("name : A Name");
        assertTrue(namedAs("A Name").matches(series));
        assertFalse(namedAs("Another Name").matches(series));
    }

    public void testOverviewMatching() {
        Series series = this.seriesFactory.createSeries("overview : A series for tests.");
        assertTrue(overviewedAs("A series for tests.").matches(series));
        assertFalse(overviewedAs("A bad overview.").matches(series));
    }
}
