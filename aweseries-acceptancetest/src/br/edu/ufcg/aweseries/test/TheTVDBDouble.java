/*
 *   TheTVDBDouble.java
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
    @Override
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
            if (series.name().toLowerCase().contains(searchedName.toLowerCase())) {
                results.add(series);
            }
        }

        return results;
    }

    // Fetch Series
    @Override
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
            if (series.id().equals(seriesId)) {
                return series;
            }
        }

        throw new SeriesNotFoundException();
    }

    // Fetch All Series
    @Override
    public List<Series> fetchAllSeries(List<String> seriesIds, String languageAbbreviation) {
        return this.fetchAllSeries(seriesIds, Language.from(languageAbbreviation));
    }

    private List<Series> fetchAllSeries(List<String> seriesIds, Language language) {
        if (seriesIds == null) {
            throw new IllegalArgumentException("seriesIds should not be null");
        }

        List<Series> results = new ArrayList<Series>();

        for (String id : seriesIds) {
            results.add(this.fetchSeries(id, language));
        }

        return results;
    }
}
