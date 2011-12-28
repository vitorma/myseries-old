/*
 *   SeriesProviderIntegrationTest.java
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


package br.edu.ufcg.aweseries.test.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import android.test.InstrumentationTestCase;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.acceptance.util.TestStreamFactory;
import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

/**
 * Test SeriesProvider API.
 */
public class SeriesProviderIntegrationTest extends InstrumentationTestCase {

    @Override
    public void setUp() {
        SampleSeries.injectInstrumentation(getInstrumentation());
        App.environment().setTheTVDBTo(new TheTVDB(new TestStreamFactory()));
        App.environment().seriesProvider().wipeFollowedSeries();
    }

    @Override
    public void tearDown() {
        this.seriesProvider().wipeFollowedSeries();
    }

    private SeriesProvider seriesProvider() {
        return App.environment().seriesProvider();
    }

    private Series testSeries(String name) {
        if (name.equals("Chuck")) {
            return SampleSeries.CHUCK.series();
        } else if (name.equals("House")) {
            return SampleSeries.HOUSE.series();
        }

        return null;
    }

    public void testNoSeriesAreFollowedInTheBeggining() {
        assertThat(this.seriesProvider().followedSeries().size(), equalTo(0));
    }

    public void failing_testFollowingASeriesMakesItAppearInFollowedSeries() {
        Series series = this.testSeries("Chuck");

        this.seriesProvider().follow(series);

        assertThat(this.seriesProvider().followedSeries().size(), equalTo(1));
//        assertThat(this.seriesProvider().followedSeries().get(0), equalTo(series));
    }

    public void failing_testFollowingASeriesTwiceMakesItAppearOnlyOnceInFollowedSeries() {
        Series series = this.testSeries("Chuck");

        this.seriesProvider().follow(series);
        this.seriesProvider().follow(series);

        assertThat(this.seriesProvider().followedSeries().size(), equalTo(1));
//        assertThat(this.seriesProvider().followedSeries().get(0), equalTo(series));
    }

    public void failing_testFollowedSeriesAreReturnedOrderedByTheirName() {
        Series series1 = this.testSeries("Chuck");
        Series series2 = this.testSeries("House");

        this.seriesProvider().follow(series1);
        this.seriesProvider().follow(series2);

        assertThat(this.seriesProvider().followedSeries().size(), equalTo(2));
//        assertThat(this.seriesProvider().followedSeries().get(0), equalTo(series1));
//        assertThat(this.seriesProvider().followedSeries().get(1), equalTo(series2));
    }

    public void testFollowedSeriesAreWiped() {
        // given
        Series series1 = this.testSeries("Chuck");
        Series series2 = this.testSeries("House");

        this.seriesProvider().follow(series1);
        this.seriesProvider().follow(series2);

        // when
        this.seriesProvider().wipeFollowedSeries();

        // then
        assertThat(this.seriesProvider().followedSeries().size(), equalTo(0));
    }

    public void testNullFollowedSeriesThrowsException() {
        try {
            this.seriesProvider().follows(null);
            fail("Should have thrown a IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void failing_testFollowedSeriesAreSeenAsFollowed() {
        Series series1 = this.testSeries("Chuck");

        this.seriesProvider().follow(series1);

        assertThat(this.seriesProvider().followedSeries().size(), equalTo(1));
        assertThat(this.seriesProvider().follows(series1), equalTo(true));
    }

    public void testNotFollowedSeriesArentSeenAsFollowed() {
        Series series1 = this.testSeries("Chuck");

        assertThat(this.seriesProvider().followedSeries().size(), equalTo(0));
        assertThat(this.seriesProvider().follows(series1), equalTo(false));
    }

    public void failing_testUnfollowedSeriesArentSeenAsFollowed() {
        // given
        Series series1 = this.testSeries("Chuck");
        this.seriesProvider().follow(series1);

        // when
        this.seriesProvider().unfollow(series1);

        // then
        assertThat(this.seriesProvider().followedSeries().size(), equalTo(0));
        assertThat(this.seriesProvider().follows(series1), equalTo(false));
    }
}
