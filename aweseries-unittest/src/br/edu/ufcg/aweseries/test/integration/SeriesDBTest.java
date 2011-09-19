package br.edu.ufcg.aweseries.test.integration;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import br.edu.ufcg.aweseries.SeriesDB;
import br.edu.ufcg.aweseries.thetvdb.Series;

public class SeriesDBTest {

    private SeriesDB db;

    private Series createTestSeries() {
        final Series series = new Series();
        series.setName("SeriesName");
        //series.setActors("SeriesActors");
        series.setNetwork("SeriesNetwork");
        series.setId("abcdefg");

        return series;
    }

    @Before
    public void setUp() {
        this.db = new SeriesDB();
    }

    @Test
    public void testDoesntReturnNullCollectionOfSeries() {
        Assert.assertThat(this.db.savedSeries(),
                CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test
    public void testDoesntSaveNullSeries() {
        try {
            this.db.saveSeries(null);
            Assert.fail("should have thrown a RuntimeException");
        } catch (final RuntimeException e) {
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIncludeSeries() {
        final Series followedSeries = this.createTestSeries();
        this.db.saveSeries(followedSeries);

        Assert.assertThat((Iterable<Series>) this.db.savedSeries(),
                JUnitMatchers.hasItem(followedSeries));
    }

    @Test
    public void testRetrieveNotSavedSeries() {
        Assert.assertThat(this.db.retrieveSeries("abcde"),
                CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testRetrieveSameSeries() {
        final Series savedSeries = this.createTestSeries();
        final String seriesId = savedSeries.getId();

        this.db.saveSeries(savedSeries);

        Assert.assertThat(this.db.retrieveSeries(seriesId),
                CoreMatchers.is(CoreMatchers.equalTo(savedSeries)));
    }
}
