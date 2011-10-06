package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import android.test.mock.MockContext;
import br.edu.ufcg.aweseries.Environment;
import br.edu.ufcg.aweseries.data.DatabaseHelper;
import junit.framework.TestCase;

public class EnvironmentLocalSeriesRepositoryTest extends TestCase {

    private Environment environment;

    private Context createContextMock() {
        return new MockContext();
    }

    public void setUp() {
        this.environment = Environment.newEnvironment(this.createContextMock());
    }

    public void tearDown() {
        this.environment = null;
    }

    // LocalSeriesRepository ---------------------------------------------------
    public void testSetLocalSeriesRepository() {
        final DatabaseHelper repository = new DatabaseHelper(this.createContextMock());

        this.environment.setLocalSeriesRepositoryTo(repository);
        assertThat(this.environment.localSeriesRepository(), sameInstance(repository));
    }

    public void testFirstLocalSeriesRepositoryNotNull() {
        assertThat(this.environment.localSeriesRepository(), notNullValue());
    }

    public void testReturnsSameLocalSeriesRepositoryEachCall() {
        final DatabaseHelper repository1 = this.environment.localSeriesRepository();
        final DatabaseHelper repository2 = this.environment.localSeriesRepository();

        assertThat(repository1, sameInstance(repository2));
    }

    public void testSettingLocalSeriesRepositoryToNullMustInstantiateANewLocalSeriesRepository() {
        final DatabaseHelper oldRepository = this.environment.localSeriesRepository();

        this.environment.setLocalSeriesRepositoryTo(null);

        assertThat(this.environment.localSeriesRepository(), notNullValue());
        assertThat(this.environment.localSeriesRepository(), not(sameInstance(oldRepository)));
    }
}
