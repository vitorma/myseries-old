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

package mobi.myseries.domain.source;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import mobi.myseries.domain.model.Series;

public interface SeriesSource {
    public List<Series> searchFor(String seriesName, String languageAbbreviation)
            throws InvalidSearchCriteriaException, ParsingFailedException, ConnectionFailedException, ConnectionTimeoutException;

    public Series fetchSeries(int seriesId, String languageAbbreviation)
            throws ParsingFailedException, ConnectionFailedException, ConnectionTimeoutException, SeriesNotFoundException;

    public List<Series> fetchAllSeries(int[] seriesIds, String languageAbbreviation)
            throws ParsingFailedException, ConnectionFailedException, ConnectionTimeoutException, SeriesNotFoundException;

    public boolean fetchUpdateMetadataSince(long dateInMiliseconds)
            throws ConnectionFailedException, ConnectionTimeoutException, ParsingFailedException,
                   UpdateMetadataUnavailableException;

    public Collection<Integer> seriesUpdateMetadata();
    public Map<Integer, String> posterUpdateMetadata();

    public String fetchSeriesPosterPath(int seriesId);
}
