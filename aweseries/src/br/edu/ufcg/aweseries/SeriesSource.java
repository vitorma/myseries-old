package br.edu.ufcg.aweseries;

import java.util.List;

import br.edu.ufcg.aweseries.model.Series;

public interface SeriesSource {

    public List<Series> searchFor(String seriesName, String languageAbbreviation);
}
