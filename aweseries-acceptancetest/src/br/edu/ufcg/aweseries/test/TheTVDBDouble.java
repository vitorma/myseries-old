package br.edu.ufcg.aweseries.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public void createSeries(String languageAbbreviation, String... attributes) {
        if (languageAbbreviation == null) {
            throw new IllegalArgumentException("language should not be null");
        }

        Language language = Language.from(languageAbbreviation);

        Series newSeries = this.seriesFactory.createSeries(attributes);
        this.saveSeries(language, newSeries);
    }

    private void saveSeries(Language language, Series series) {
        if (!this.languageSeries.containsKey(language)) {
            this.languageSeries.put(language, new HashSet<Series>());
        }
        this.languageSeries.get(language).add(series);
    }

    public List<Series> searchFor(String seriesName, String languageAbbreviation) {
        if (seriesName == null) {
            throw new IllegalArgumentException("seriesName should not be null");
        }
        if (languageAbbreviation == null) {
            throw new IllegalArgumentException("language should not be null");
        }

        Language language = Language.from(languageAbbreviation);

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

    public Series fetchSeries(String seriesId, String languageAbbreviation) {
        if (seriesId == null) {
            throw new IllegalArgumentException("seriesId should not be null");
        }
        Language language = Language.from(languageAbbreviation);

        Set<Series> source = this.languageSeries.get((this.languageSeries.containsKey(language))
                                                     ? language 
                                                     : Language.EN);

        for (Series series : source) {
            if (series.getId().equals(seriesId)) {
                return series;
            }
        }

        return null;
    }
}
