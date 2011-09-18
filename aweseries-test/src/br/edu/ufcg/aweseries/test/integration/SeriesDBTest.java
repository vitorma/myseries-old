package br.edu.ufcg.aweseries.test.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import junit.framework.TestCase;

import br.edu.ufcg.aweseries.SeriesDB;
import br.edu.ufcg.aweseries.thetvdb.Series;

public class SeriesDBTest extends TestCase {

    private SeriesDB db;

    @Override
    public void setUp() {
        this.db = new SeriesDB();
    }

    @Override
    public void tearDown() {
        this.db = null;
    }

    public void testDoesntReturnNullCollectionOfSeries() {
        assertThat(db.savedSeries(), is(not(nullValue())));
    }

    public void testIncludeSeries() {
        Series followedSeries = createTestSeries();
        db.saveSeries(followedSeries);

        assertThat(db.savedSeries(), hasItem(followedSeries));
    }

    public void testDoesntSaveNullSeries() {
        try {
            db.saveSeries(null);
            fail("should have thrown a RuntimeException");
        } catch (RuntimeException e) {}
    }

    public void testRetrieveSameSeries() {
        Series savedSeries = createTestSeries();
        String seriesId = savedSeries.getId();
        
        db.saveSeries(savedSeries);
        
        assertThat(db.retrieveSeries(seriesId), is(equalTo(savedSeries)));
    }
    
    public void testRetrieveNotSavedSeries() {
        assertThat(db.retrieveSeries("abcde"), is(nullValue()));
    }

    private Series createTestSeries() {
        Series series = new Series();
        series.setName("SeriesName");
        //series.setActors("SeriesActors");
        series.setNetwork("SeriesNetwork");
        series.setId("abcdefg");
        
        return series;
    }
}
