package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.thetvdb.series.Series;

public class SeriesProviderTest {

    private SeriesProvider provider;

    @Before
    public void setUp() {
        this.provider = SeriesProvider.newSeriesProvider();
    }

    @Test
    public void noSeriesAreFollowedInTheBeggining() {
        assertThat(this.provider.mySeries().length, equalTo(0));
    }

    @Test
    public void followingASeriesMakesItAppearInFollowedSeries() {
        Series series = mock(Series.class);
        when(series.getName()).thenReturn("SeriesName");

        this.provider.follow(series);

        assertThat(this.provider.mySeries().length, equalTo(1));
        assertThat(this.provider.mySeries()[0], equalTo(series));
    }

    @Test
    public void followingASeriesTwiceMakesItAppearOnlyOnceInFollowedSeries() {
        Series series = mock(Series.class);
        when(series.getName()).thenReturn("SeriesName");

        this.provider.follow(series);
        this.provider.follow(series);

        assertThat(this.provider.mySeries().length, equalTo(1));
        assertThat(this.provider.mySeries()[0], equalTo(series));
    }

    @Test
    public void theFollowedSeriesAreReturnedOrderedByTheirName() {
        Series series1 = mock(Series.class);
        when(series1.getName()).thenReturn("A Series");

        Series series2 = mock(Series.class);
        when(series2.getName()).thenReturn("B Series");

        this.provider.follow(series1);
        this.provider.follow(series2);

        assertThat(this.provider.mySeries().length, equalTo(2));
        assertThat(this.provider.mySeries()[0], equalTo(series1));
        assertThat(this.provider.mySeries()[1], equalTo(series2));
    }
}
