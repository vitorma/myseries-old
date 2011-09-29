package br.edu.ufcg.aweseries.test.acceptance.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.test.util.ChuckSeries;
import br.edu.ufcg.aweseries.thetvdb.series.Series;

import com.jayway.android.robotium.solo.Solo;

public class AppDriver {

    private static Map<String, String> seriesNameToId = new HashMap<String, String>();
    {
        seriesNameToId.put(ChuckSeries.name, ChuckSeries.id); 
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

    private SeriesProvider seriesProvider() {
        return App.environment().getSeriesProvider();
    }

    public void follow(String seriesName) {
        Series series = retrieveSeriesNamed(seriesName);

        seriesProvider().follow(series);
        this.saveReferenceToSeries(seriesName, series);

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

    private void saveReferenceToSeries(String seriesName, Series series) {
        this.series.put(seriesName, series);
    }
}
