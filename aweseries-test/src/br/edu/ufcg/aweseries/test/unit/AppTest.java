package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import junit.framework.TestCase;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.Environment;

public class AppTest extends TestCase {

    public void testSingletonEnvironment() {
        final Environment env1 = App.environment();
        final Environment env2 = App.environment();

        assertThat(env1, sameInstance(env2));
    }

    public void testEnvironmentSingletonNotNull() {
        assertThat(App.environment(), notNullValue());
    }
}
