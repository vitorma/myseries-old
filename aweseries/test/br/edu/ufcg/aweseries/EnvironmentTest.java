package br.edu.ufcg.aweseries;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    public void singletonEnvironment() {
        Environment env1 = Environment.instance();
        Environment env2 = Environment.instance();
        
        assertThat(env1, sameInstance(env2));
    }

    @Test
    public void singletonNotNull() {
        assertThat(Environment.instance(), notNullValue());
    }

    @Test
    public void firstSeriesProviderNotNull() {
        assertThat(environment.getSeriesProvider(), notNullValue());
    }

    @Test
    public void returnsSameSeriesProviderEachCall() {
        SeriesProvider sp1 = environment.getSeriesProvider();
        SeriesProvider sp2 = environment.getSeriesProvider();
        
        assertThat(sp1, sameInstance(sp2));
    }

    @Test
    public void changeSeriesProvider() {
        SeriesProvider sp = new SeriesProvider();
        
        environment.setSeriesProvider(sp);
        
        assertThat(environment.getSeriesProvider(), sameInstance(sp));
    }

    @Test
    public void settingSeriesProviderToNullMustInstantiateANewSeriesProvider(){
        SeriesProvider oldSP = environment.getSeriesProvider();
        environment.setSeriesProvider(null);
        
        assertThat(environment.getSeriesProvider(), notNullValue());
        assertThat(environment.getSeriesProvider(), not(sameInstance(oldSP)));
    }
}
