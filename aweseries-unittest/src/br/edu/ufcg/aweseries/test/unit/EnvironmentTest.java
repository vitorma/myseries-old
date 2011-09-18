package br.edu.ufcg.aweseries.test.unit;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.aweseries.Environment;
import br.edu.ufcg.aweseries.SeriesProvider;

public class EnvironmentTest {

    private Environment environment;

    @Before
    public void setUp() {
        this.environment = Environment.instance();
    }

    @After
    public void tearDown() {
        this.environment = null;
    }

    @Test
    public void testChangeSeriesProvider() {
        final SeriesProvider sp = SeriesProvider.newSeriesProvider();

        this.environment.setSeriesProvider(sp);

        Assert.assertThat(this.environment.getSeriesProvider(),
                CoreMatchers.sameInstance(sp));
    }

    @Test
    public void testFirstSeriesProviderNotNull() {
        Assert.assertThat(this.environment.getSeriesProvider(),
                CoreMatchers.notNullValue());
    }

    @Test
    public void testReturnsSameSeriesProviderEachCall() {
        final SeriesProvider sp1 = this.environment.getSeriesProvider();
        final SeriesProvider sp2 = this.environment.getSeriesProvider();

        Assert.assertThat(sp1, CoreMatchers.sameInstance(sp2));
    }

    @Test
    public void
            testSettingSeriesProviderToNullMustInstantiateANewSeriesProvider() {
        final SeriesProvider oldSP = this.environment.getSeriesProvider();
        this.environment.setSeriesProvider(null);

        Assert.assertThat(this.environment.getSeriesProvider(),
                CoreMatchers.notNullValue());
        Assert.assertThat(this.environment.getSeriesProvider(),
                CoreMatchers.not(CoreMatchers.sameInstance(oldSP)));
    }

    @Test
    public void testSingletonEnvironment() {
        final Environment env1 = Environment.instance();
        final Environment env2 = Environment.instance();

        Assert.assertThat(env1, CoreMatchers.sameInstance(env2));
    }

    @Test
    public void testSingletonNotNull() {
        Assert.assertThat(Environment.instance(), CoreMatchers.notNullValue());
    }
}
