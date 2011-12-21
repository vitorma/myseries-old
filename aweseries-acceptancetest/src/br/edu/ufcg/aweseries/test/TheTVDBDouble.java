package br.edu.ufcg.aweseries.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.aweseries.SeriesNotFoundException;
import br.edu.ufcg.aweseries.SeriesSource;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.thetvdb.Language;

public class TheTVDBDouble implements SeriesSource {

    private DefaultSeriesFactory seriesFactory;
    private Map<Language, Set<Series>> languageSeries;

    public TheTVDBDouble() {
        this.seriesFactory = new DefaultSeriesFactory();

        this.languageSeries = new HashMap<Language, Set<Series>>();
        this.languageSeries.put(Language.EN, new HashSet<Series>());
    }

    // Create Series
    public void createSeries(String languageAbbreviation, String... attributes) {
        this.createSeries(Language.from(languageAbbreviation), attributes);
    }

    private void createSeries(Language language, String... attributes) {
        Series newSeries = this.seriesFactory.createSeries(attributes);

        this.saveSeries(language, newSeries);
    }

    private void saveSeries(Language language, Series series) {
        if (!this.languageSeries.containsKey(language)) {
            this.languageSeries.put(language, new HashSet<Series>());
        }
        this.languageSeries.get(language).add(series);
    }

    // Search for Series
    public List<Series> searchFor(String seriesName, String languageAbbreviation) {
        return this.searchFor(seriesName, Language.from(languageAbbreviation));
    }

    private List<Series> searchFor(String seriesName, Language language) {
        if (seriesName == null) {
            throw new IllegalArgumentException("seriesName should not be null");
        }

        List<Series> results = new ArrayList<Series>();

        results.addAll(this.resultsIn(language, seriesName));

        if (!language.equals(Language.EN)) {
            results.addAll(this.resultsIn(Language.EN, seriesName));
        }

        return Collections.unmodifiableList(results);
    }

    /**
     * This method never returns null. It either returns the result set or an empty set.
     */
    private Set<Series> resultsIn(Language language, String searchedName) {
        if (!this.languageSeries.containsKey(language)) {
            return Collections.emptySet();
        }
        return this.matchingResultsFrom(this.languageSeries.get(language), searchedName);
    }

    /**
     * This method never returns null. It either returns the result set or an empty set.
     */
    private Set<Series> matchingResultsFrom(Set<Series> seriesSet, String searchedName) {
        Set<Series> results = new HashSet<Series>();

        for (Series series : seriesSet) {
            if (series.getName().toLowerCase().contains(searchedName.toLowerCase())) {
                results.add(series);
            }
        }

        return results;
    }

    // Fetch Series
    public Series fetchSeries(String seriesId, String languageAbbreviation) {
        return this.fetchSeries(seriesId, Language.from(languageAbbreviation));
    }

    private Series fetchSeries(String seriesId, Language language) {
        if (seriesId == null) {
            throw new IllegalArgumentException("seriesId should not be null");
        }

        Set<Series> source = this.languageSeries.get((this.languageSeries.containsKey(language))
                                                     ? language 
                                                     : Language.EN);

        for (Series series : source) {
            if (series.getId().equals(seriesId)) {
                return series;
            }
        }

        throw new SeriesNotFoundException();
    }
}
