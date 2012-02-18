/*
 *   SeriesSet.java
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

package br.edu.ufcg.aweseries.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.edu.ufcg.aweseries.util.Validate;

public class SeriesSet implements Iterable<Series> {
    private Map<Integer, Series> series;

    public SeriesSet() {
        this.series = new HashMap<Integer, Series>();
    }

    public int size() {
        return this.series.size();
    }

    public boolean isEmpty() {
        return this.series.isEmpty();
    }

    public boolean contains(Series series) {
        Validate.isNonNull(series, "series");

        return this.series.containsKey(series.id());
    }

    public Series get(int seriesId) {
        Validate.isTrue(this.series.containsKey(seriesId), "series with id %d doesn't belong to this set", seriesId);

        return this.series.get(seriesId);
    }

    public Collection<Series> getAll() {
        return Collections.unmodifiableCollection(this.series.values());
    }

    public void add(Series series) {
        Validate.isTrue(!this.contains(series), "series with id %d already belongs to this set", series.id());

        this.series.put(series.id(), series);
    }

    public void addAll(Collection<Series> collection) {
        Validate.isNonNull(collection, "collection");

        for (Series s : collection) {
            this.add(s);
        }
    }

    public void remove(Series series) {
        Validate.isTrue(this.contains(series), "series with id %d doesn't belong to this set", series.id());

        this.series.remove(series.id());
    }

    public void clear() {
        this.series.clear();
    }

    @Override
    public Iterator<Series> iterator() {
        return this.series.values().iterator();
    }
}
