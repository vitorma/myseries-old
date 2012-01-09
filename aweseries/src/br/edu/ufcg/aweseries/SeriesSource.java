/*
 *   SeriesSource.java
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

package br.edu.ufcg.aweseries;

import java.util.List;

import br.edu.ufcg.aweseries.model.Series;

public interface SeriesSource {

    /**
     * @throws IllegalArgumentException when either seriesName or languageAbbreviation are null
     */
    public List<Series> searchFor(String seriesName, String languageAbbreviation);

    /**
     * @throws IllegalArgumentException when either seriesId or languageAbbreviation are null
     * @throws SeriesNotFoundException if there is no series with the given seriesId
     */
    public Series fetchSeries(String seriesId, String languageAbbreviation);

    /**
     * @throws IllegalArgumentException when either seriesIds or languageAbbreviation are null
     * @throws SeriesNotFoundException if there is no series with any of the given seriesId
     */
    public List<Series> fetchAllSeries(List<String> seriesIds, String languageAbbreviation);
}
