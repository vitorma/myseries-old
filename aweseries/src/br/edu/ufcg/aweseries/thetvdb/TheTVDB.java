/*
 *   TheTVDB.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.thetvdb;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesParser;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesSearchParser;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.stream.TheTVDBStreamFactory;

public class TheTVDB {
    private final StreamFactory streamFactory;

    public TheTVDB(String apiKey) {
        this(new TheTVDBStreamFactory(apiKey));
    }

    public TheTVDB(StreamFactory streamFactory) {
        if (streamFactory == null) {
            throw new IllegalArgumentException("streamFactory should not be null");
        }

        this.streamFactory = streamFactory;
    }

    public List<Series> search(String seriesName) {
        try {
            return new SeriesSearchParser(this.streamFactory).parse(seriesName);
        } catch (Exception e) {
            //TODO A better exception handling
            return null;
        }
    }

    public Series getSeries(String seriesId) {
        try {
            return new SeriesParser(this.streamFactory).parse(seriesId);
        } catch (Exception e) {
            //TODO A better exception handling
            return null;
        }
    }

    public List<Series> getAllSeries(List<String> seriesIds) {
        List<Series> result = new ArrayList<Series>();
        for (String seriesId : seriesIds) {
            Series series = this.getSeries(seriesId);
            //TODO Return an appropriated exception
            if (series == null) {
                return null;
            }
            result.add(series);
        }
        return result;
    }
}
