package br.edu.ufcg.aweseries;

import java.util.List;

import br.edu.ufcg.aweseries.model.Series;

public interface SeriesSource {

    /**
     * This method never returns null.
     *
     * @throws IllegalArgumentException when either seriesName or languageAbbreviation are null
     */
    public List<Series> searchFor(String seriesName, String languageAbbreviation);

    /**
     * If there is no series with the given seriesId, throws SeriesNotFoundException.
     *
     * @throws IllegalArgumentException when either seriesId or languageAbbreviation are null
     */
    public Series fetchSeries(String seriesId, String languageAbbreviation);

    /**
     * If there is no series with any of the given seriesId, throws SeriesNotFoundException.
     *
     * @throws IllegalArgumentException when either seriesIds or languageAbbreviation are null
     */
    public List<Series> fetchAllSeries(List<String> seriesIds, String languageAbbreviation);
}
