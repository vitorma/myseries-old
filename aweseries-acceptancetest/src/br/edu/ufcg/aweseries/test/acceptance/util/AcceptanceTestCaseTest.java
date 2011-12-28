/*
 *   AcceptanceTestCaseTest.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package br.edu.ufcg.aweseries.test.acceptance.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.gui.SeriesListActivity;

public class AcceptanceTestCaseTest extends AcceptanceTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();

        assertThat(this.solo(), notNullValue());
        assertThat(this.driver(), notNullValue());
    }

    public void testUserFollowsNoSeries() {
        SeriesProvider provider = App.environment().seriesProvider();

        assertThat(provider.followedSeries().size(), equalTo(0));
    }

    public void testSoloIsNotNull() {
        assertThat(this.solo(), notNullValue());
    }

    public void testDriverIsNotNull() {
        assertThat(this.driver(), notNullValue());
    }

    public void testSoloRunsMySeries() {
        assertThat(this.solo().getCurrentActivity(), instanceOf(SeriesListActivity.class));
    }
}
