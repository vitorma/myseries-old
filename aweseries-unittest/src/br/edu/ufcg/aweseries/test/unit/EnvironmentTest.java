package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.aweseries.Environment;
import br.edu.ufcg.aweseries.SeriesProvider;

public class EnvironmentTest {

    private Environment environment;

    @Before
    public void setUp() {
        this.environment = Environment.newEnvironment();
    }

    @After
    public void tearDown() {
        this.environment = null;
    }

    @Test
    public void testChangeSeriesProvider() {
        final SeriesProvider sp = SeriesProvider.newSeriesProvider();

        this.environment.setSeriesProvider(sp);

        assertThat(this.environment.getSeriesProvider(),
                sameInstance(sp));
    }

    @Test
    public void testFirstSeriesProviderNotNull() {
        assertThat(this.environment.getSeriesProvider(), notNullValue());
    }

    @Test
    public void testReturnsSameSeriesProviderEachCall() {
        final SeriesProvider sp1 = this.environment.getSeriesProvider();
        final SeriesProvider sp2 = this.environment.getSeriesProvider();

        assertThat(sp1, sameInstance(sp2));
    }

    @Test
    public void
            testSettingSeriesProviderToNullMustInstantiateANewSeriesProvider() {
        final SeriesProvider oldSP = this.environment.getSeriesProvider();
        this.environment.setSeriesProvider(null);

        assertThat(this.environment.getSeriesProvider(),
                notNullValue());
        assertThat(this.environment.getSeriesProvider(),
                not(sameInstance(oldSP)));
    }
}
