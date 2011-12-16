package br.edu.ufcg.aweseries;

import java.util.List;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.thetvdb.Language;

public interface SeriesSource {

    public List<Series> searchFor(String seriesName, Language language);
}
