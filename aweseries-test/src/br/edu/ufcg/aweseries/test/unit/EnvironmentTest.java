package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.TestCase;
import br.edu.ufcg.aweseries.Environment;
import br.edu.ufcg.aweseries.SeriesProvider;

public class EnvironmentTest extends TestCase {
    
    private Environment environment;

    public void setUp() {
        this.environment = Environment.instance();
    }
    
    public void tearDown() {
        this.environment = null;
    }

    public void testSingletonEnvironment() {
        Environment env1 = Environment.instance();
        Environment env2 = Environment.instance();
        
        assertThat(env1, sameInstance(env2));
    }

    public void testSingletonNotNull() {
        assertThat(Environment.instance(), notNullValue());
    }

    public void testFirstSeriesProviderNotNull() {
        assertThat(environment.getSeriesProvider(), notNullValue());
    }

    public void testReturnsSameSeriesProviderEachCall() {
        SeriesProvider sp1 = environment.getSeriesProvider();
        SeriesProvider sp2 = environment.getSeriesProvider();
        
        assertThat(sp1, sameInstance(sp2));
    }

    public void testChangeSeriesProvider() {
        SeriesProvider sp = SeriesProvider.newSeriesProvider();
        
        environment.setSeriesProvider(sp);
        
        assertThat(environment.getSeriesProvider(), sameInstance(sp));
    }

    public void testSettingSeriesProviderToNullMustInstantiateANewSeriesProvider(){
        SeriesProvider oldSP = environment.getSeriesProvider();
        environment.setSeriesProvider(null);
        
        assertThat(environment.getSeriesProvider(), notNullValue());
        assertThat(environment.getSeriesProvider(), not(sameInstance(oldSP)));
    }
}
