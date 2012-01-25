/*
 *   TheTVDB.java
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


package br.edu.ufcg.aweseries.thetvdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.ufcg.aweseries.SeriesNotFoundException;
import br.edu.ufcg.aweseries.SeriesSource;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesParser;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesSearchParser;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.stream.TheTVDBStreamFactory;

public class TheTVDB implements SeriesSource {
    private final StreamFactory streamFactory;

    public TheTVDB(String apiKey) {
        this(new TheTVDBStreamFactory(apiKey));
    }

    public TheTVDB(StreamFactory streamFactory) {
        if (streamFactory == null)
            throw new IllegalArgumentException("streamFactory should not be null");

        this.streamFactory = streamFactory;
    }

    //SeriesSource methods----------------------------------------------------------------------------------------------

    @Override
    public List<Series> searchFor(String seriesName, String language) {
        if (seriesName == null)
            throw new IllegalArgumentException("seriesName should not be null");

        if (language == null)
            throw new IllegalArgumentException("language should not be null");

        try {
            return new SeriesSearchParser(this.streamFactory).parse(seriesName, this.getSupported(language));
        } catch (Exception e) {
            //TODO A better exception handling
            return Collections.emptyList();
        }
    }

    @Override
    public Series fetchSeries(int seriesId, String language) {

        if (language == null)
            throw new IllegalArgumentException("language should not be null");

        try {
            return new SeriesParser(this.streamFactory).parse(seriesId, this.getSupported(language));
        } catch (Exception e) {
            //TODO A better exception handling
            throw new SeriesNotFoundException(e);
        }
    }

    @Override//TODO Here, int[] is better than List<Integer>, because the subtle NPE thrown when an id is null
    public List<Series> fetchAllSeries(List<Integer> seriesIds, String language) {
        if (seriesIds == null)
            throw new IllegalArgumentException("seriesIds should not be null");

        List<Series> result = new ArrayList<Series>();

        for (Integer seriesId : seriesIds) {
            //TODO Check the language only once
            Series series = this.fetchSeries(seriesId, language);
            result.add(series);
        }

        return result;
    }

    //Auxiliary---------------------------------------------------------------------------------------------------------

    private String getSupported(String language) {
        try {
            return Language.from(language).abbreviation();
        } catch (Exception e) {
            return "en";
        }
    }
}
