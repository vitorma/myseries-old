package br.edu.ufcg.aweseries.test.acceptance.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.util.SampleSeries;

import com.jayway.android.robotium.solo.Solo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppDriver {

    // TODO: it should iterate all over the SeriesSample samples, storing their data
    private static Map<String, String> seriesNameToId = new HashMap<String, String>();
    {
        seriesNameToId.put(SampleSeries.CHUCK.name(), SampleSeries.CHUCK.id()); 
    }

    private Solo solo;
    private Map<String, Series> series;

    public AppDriver(Solo robotiumSolo) {
        if (robotiumSolo == null) {
            throw new IllegalArgumentException("robotiumSolo should not be null");
        }
        this.solo = robotiumSolo;

        this.series = new HashMap<String, Series>();
    }

    // Full Actions ------------------------------------------------------------
    public void follow(String seriesName) {
        this.validateSeriesName(seriesName);

        Series series = retrieveSeriesNamed(seriesName);

        seriesProvider().follow(series);
        this.saveReferenceTo(series, seriesName);

        // XXX: This is a workarond because there is no way to navigate through the acivities in
        // order to start following an activity. It should be resolved as soon as there is a way
        // to do it through user interaction
        this.restartCurrentActivity();
    }

    private void restartCurrentActivity() {
        Activity currentActivity = this.solo.getCurrentActivity();
        Intent currentActivityIntent = currentActivity.getIntent();

        currentActivity.finish();
        currentActivity.startActivity(currentActivityIntent);
    }

    private Series retrieveSeriesNamed(String seriesName) {
        return seriesProvider().getSeries(seriesNameToId.get(seriesName));
    }

    private void saveReferenceTo(Series series, String seriesName) {
        this.series.put(seriesName, series);
    }
    
    private Series seriesReferencedAs(String seriesName) {
        return this.series.get(seriesName);
    }

    // Navigation --------------------------------------------------------------
    public void viewMyFollowedSeries() {
        if (!"MySeries".equals(this.solo.getCurrentActivity().getClass().getSimpleName())) {
            this.solo.goBackToActivity("MySeries");
        }
    }
    public void viewDetailsOf(String seriesName) {
        this.validateSeriesName(seriesName);

        this.viewMyFollowedSeries();
        this.solo.clickOnText(seriesReferencedAs(seriesName).getName());
    }

    public void viewSeasonsOf(String seriesName) {
        this.validateSeriesName(seriesName);

        this.viewDetailsOf(seriesName);
        this.solo.clickOnText("Seasons");
    }

    // Verification ------------------------------------------------------------
    public SeriesAccessor assertThatSeries(String seriesName) {
        return new SeriesAccessor(this.retrieveSeriesNamed(seriesName));
    }

    public class SeriesAccessor {
        private Series series;

        public SeriesAccessor(Series series) {
            this.series = series;
        }

        public TextAsserter name() {
            return asserterTo(this.series.getName()); 
        }

        public TextAsserter status() {
            return asserterTo(this.series.getStatus()); 
        }

        private TextAsserter asserterTo(String text) {
            return new TextAsserter(text);
        }
    }

    public class TextAsserter {
        private String text;

        public TextAsserter(String text) {
            this.text = text;
        }

        public void isPresent() {
            assertThat(solo.searchText(text), equalTo(true));
        }
    }

    // Private tools -----------------------------------------------------------
    private SeriesProvider seriesProvider() {
        return App.environment().getSeriesProvider();
    }

    private void validateSeriesName(String seriesName) {
        if (seriesName == null) {
            throw new IllegalArgumentException("seriesName should not be null");
        }
        if (seriesName.trim().isEmpty()) {
            throw new IllegalArgumentException("seriesName should not be null");
        }
    }
}
