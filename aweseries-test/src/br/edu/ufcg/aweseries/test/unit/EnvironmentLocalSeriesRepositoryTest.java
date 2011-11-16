/*
 *   EnvironmentLocalSeriesRepositoryTest.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

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
