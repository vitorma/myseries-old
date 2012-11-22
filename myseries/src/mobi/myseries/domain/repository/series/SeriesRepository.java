/*
 *   SeriesRepository.java
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

package mobi.myseries.domain.repository.series;

import java.util.Collection;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;

public interface SeriesRepository {
    public void insert(Series series);
    public void update(Series series);
    public void updateAll(Collection<Series> series);
    public void update(Episode episode);
    public void updateAllEpisodes(Collection<Episode> episodes);
    public void delete(Series series);
    public void deleteAll(Collection<Series> seriesCollection);
    public void clear();
    public boolean contains(Series series);
    public Series get(int seriesId);
    public Collection<Series> getAll();
}
