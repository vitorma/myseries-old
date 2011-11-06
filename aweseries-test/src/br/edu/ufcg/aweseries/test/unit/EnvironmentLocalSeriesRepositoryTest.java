package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.TestCase;
import android.content.Context;
import android.test.mock.MockContext;
import br.edu.ufcg.aweseries.Environment;
import br.edu.ufcg.aweseries.repository.SeriesDatabase;

public class EnvironmentLocalSeriesRepositoryTest extends TestCase {

    private Environment environment;

    private Context createContextMock() {
        return new MockContext();
    }

    @Override
    public void setUp() {
        this.environment = Environment.newEnvironment(this.createContextMock());
    }

    @Override
    public void tearDown() {
        this.environment = null;
    }

    // LocalSeriesRepository ---------------------------------------------------
    public void testSetLocalSeriesRepository() {
        final SeriesDatabase repository = new SeriesDatabase(this.createContextMock());

        this.environment.setLocalSeriesRepositoryTo(repository);
        assertThat(this.environment.localSeriesRepository(), sameInstance(repository));
    }

    public void testFirstLocalSeriesRepositoryNotNull() {
        assertThat(this.environment.localSeriesRepository(), notNullValue());
    }

    public void testReturnsSameLocalSeriesRepositoryEachCall() {
        final SeriesDatabase repository1 = this.environment.localSeriesRepository();
        final SeriesDatabase repository2 = this.environment.localSeriesRepository();

        assertThat(repository1, sameInstance(repository2));
    }

    public void testSettingLocalSeriesRepositoryToNullMustInstantiateANewLocalSeriesRepository() {
        final SeriesDatabase oldRepository = this.environment.localSeriesRepository();

        this.environment.setLocalSeriesRepositoryTo(null);

        assertThat(this.environment.localSeriesRepository(), notNullValue());
        assertThat(this.environment.localSeriesRepository(), not(sameInstance(oldRepository)));
    }
}
