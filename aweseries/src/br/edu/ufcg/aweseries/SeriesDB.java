package br.edu.ufcg.aweseries;

import java.util.ArrayList;
import java.util.Collections;

import br.edu.ufcg.aweseries.thetvdb.series.Series;

public class SeriesDB {
    
    private final ArrayList<Series> savedSeries;
    
    public SeriesDB() {
        this.savedSeries = new ArrayList<Series>();
    }

    public void saveSeries(Series seriesToBeSaved) {
        if (seriesToBeSaved == null) {
            throw new IllegalArgumentException(
                    "seriesToBeSaved should not be null");
        }
        
        this.savedSeries.add(seriesToBeSaved);
    }

    public Iterable<? super Series> savedSeries() {
        return Collections.unmodifiableCollection(this.savedSeries);
    }

    /*
     * Retrieve a saved series. If there isn't any series with the given
     * seriesId saved, returns null.
     */
    public Series retrieveSeries(String seriesId) {
        for (Series s : this.savedSeries) {
            if (s.getId() == seriesId) {
                return s;
            }
        }
        return null;
    }

}
