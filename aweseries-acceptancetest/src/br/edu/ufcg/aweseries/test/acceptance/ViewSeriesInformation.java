/*
 *   ViewSeriesInformation.java
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

package br.edu.ufcg.aweseries.test.acceptance;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.AssertionFailedError;
import android.test.ActivityInstrumentationTestCase2;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.gui.SeriesListActivity;
import br.edu.ufcg.aweseries.series_source.TheTVDB;
import br.edu.ufcg.aweseries.test.acceptance.util.AppDriver;
import br.edu.ufcg.aweseries.test.acceptance.util.TestStreamFactory;
import br.edu.ufcg.aweseries.test.util.SampleSeries;

import com.jayway.android.robotium.solo.Solo;

public class ViewSeriesInformation extends
        ActivityInstrumentationTestCase2<SeriesListActivity> {

    private Solo solo;
    private AppDriver driver;
    
    public ViewSeriesInformation() {
        super(SeriesListActivity.class);
    }

    protected Solo solo() {
        return this.solo;
    }

    protected AppDriver driver() {
        return this.driver;
    }

    @Override
    public void setUp() {
        this.setUpTestStreamFactory();
        this.clearUserData();
        this.setUpTestTools();
    }

    private void setUpTestStreamFactory() {
        SampleSeries.injectInstrumentation(getInstrumentation());
        App.environment().setTheTVDBTo(new TheTVDB(new TestStreamFactory()));
    }

    private void clearUserData() {
        App.environment().seriesProvider().wipeFollowedSeries();
    }

    private void setUpTestTools() {
        this.solo = new Solo(getInstrumentation(), getActivity());
        this.driver = new AppDriver(this.solo);
    }

    @Override
    public void tearDown() throws Exception {
        this.clearUserData();

        try {
            this.solo.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        getActivity().finish();
        super.tearDown();

        this.clearUserData();
    }

    // Tests -------------------------------------------------------------------

    // XXX: this test is used as an workaround to the fact that everytime the tests are run, the
    // first TestCase fails. Remove it as soon as possible.
    public void testAAANothing() {
        String seriesName = "Chuck";
        this.driver().follow(seriesName);
        try {
            this.driver().viewDetailsOf(seriesName);
        } catch (AssertionFailedError e) {}
    }

    public void testGetToSeriesInformationFromFollowedSeries() {
        // Given
        this.driver().follow("Chuck");
        
        // When
        this.driver.viewDetailsOf("Chuck");

        // Then
        assertThat(this.solo().searchText("Chuck"), equalTo(true));
    }
    
    public void testSeriesInformationContent() {
        // Given
        this.driver().follow("Chuck");

        String[] fields = {
                           "Chuck",           // name
                           "Continuing",      // status
                                              "First aired:",
                           "Friday",          "Air day:",
                                              "Air time:",
                           "60 minutes",      "Runtime:",
                           "NBC",             "Network:",
                           "Drama", "Action", "Genre:",
                           "Zachary Levi",    "Actors:",
                           "ace computer geek at Buy More" // overview
        };
        
        // When
        this.driver.viewDetailsOf("Chuck");

        // Then
        // fields must be visible
        for (String field : fields) {
            assertThat("Field "+ field + " was not found",
                       this.solo().searchText(field), equalTo(true));
        }
    }
}
