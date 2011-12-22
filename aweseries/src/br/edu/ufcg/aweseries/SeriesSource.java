package br.edu.ufcg.aweseries;

import java.util.List;

import br.edu.ufcg.aweseries.model.Series;

public interface SeriesSource {

    /**
     * This method never returns null.
     */
    public List<Series> searchFor(String seriesName, String languageAbbreviation);

    /**
     * If there is no series with the given seriesId, throws SeriesNotFoundException.
     */
    public Series fetchSeries(String seriesId, String languageAbbreviation);

    /**
     * If there is no series with any of the given seriesId, throws SeriesNotFoundException.
     */
    public List<Series> fetchAllSeries(List<String> seriesIds, String languageAbbreviation);
}
