/*
 *   EnvironmentTest.java
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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import android.content.Context;
import br.edu.ufcg.aweseries.Environment;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

public class EnvironmentTest {

    private Environment environment;

    @Before
    public void setUp() {
        Context contextMock = mock(Context.class);

        this.environment = Environment.newEnvironment(contextMock);
    }

    @After
    public void tearDown() {
        this.environment = null;
    }

    // Series Provider ---------------------------------------------------------
    @Test(expected = IllegalArgumentException.class)
    public void contextMustNotBeNull() {
        Environment.newEnvironment(null);
    }

    // Series Provider ---------------------------------------------------------
    @Ignore //TODO: Implement Logger and remove this annotation
    @Test
    public void testChangeSeriesProvider() {
        final SeriesProvider sp = SeriesProvider.newInstance(
        		this.environment.theTVDB(), this.environment.seriesRepositoryFactory());

        this.environment.setSeriesProvider(sp);

        assertThat(this.environment.seriesProvider(),
                sameInstance(sp));
    }

    @Ignore
    @Test
    public void testFirstSeriesProviderNotNull() {
        assertThat(this.environment.seriesProvider(), notNullValue());
    }

    @Ignore
    @Test
    public void testReturnsSameSeriesProviderEachCall() {
        final SeriesProvider sp1 = this.environment.seriesProvider();
        final SeriesProvider sp2 = this.environment.seriesProvider();

        assertThat(sp1, sameInstance(sp2));
    }

    @Ignore
    @Test
    public void testSettingSeriesProviderToNullMustInstantiateANewSeriesProvider() {
        final SeriesProvider oldSP = this.environment.seriesProvider();
        this.environment.setSeriesProvider(null);

        assertThat(this.environment.seriesProvider(),
                notNullValue());
        assertThat(this.environment.seriesProvider(),
                not(sameInstance(oldSP)));
    }

    // TheTVDB -----------------------------------------------------------------
    @Test
    public void setTheTVDB() {
        final TheTVDB db = mock(TheTVDB.class);

        this.environment.setTheTVDBTo(db);
        assertThat(this.environment.theTVDB(), sameInstance(db));
    }

    @Test
    public void firstTheTVDBNotNull() {
        assertThat(this.environment.theTVDB(), notNullValue());
    }

    @Test
    public void testReturnsSameTheTVDBEachCall() {
        final TheTVDB db1 = this.environment.theTVDB();
        final TheTVDB db2 = this.environment.theTVDB();

        assertThat(db1, sameInstance(db2));
    }

    @Test
    public void testSettingTheTVDBToNullMustInstantiateANewTheTVDB() {
        final TheTVDB oldDB = this.environment.theTVDB();

        this.environment.setTheTVDBTo(null);

        assertThat(this.environment.theTVDB(), notNullValue());
        assertThat(this.environment.theTVDB(), not(sameInstance(oldDB)));
    }

    // LocalSeriesRepository ---------------------------------------------------
    // The tests for localSeriesRepository are at
    // aweseries-test/.../unit/EnvironmentLocalSeriesRepositoryTest
}
