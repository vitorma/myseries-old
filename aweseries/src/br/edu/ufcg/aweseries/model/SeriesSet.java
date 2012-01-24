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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SeriesSet implements Iterable<Series> {
    private Set<DomainObjectListener<SeriesSet>> listeners;
    private Map<String, Series> map;

    public SeriesSet() {
        this.map = new HashMap<String, Series>();
        this.listeners = new HashSet<DomainObjectListener<SeriesSet>>(); 
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean contains(Series series) {
        if (series == null)
            throw new IllegalArgumentException("series shouldn't be null");

        return this.map.containsKey(String.valueOf(series.id()));
    }

    public Series get(String seriesId) {
        Series series = this.map.get(seriesId);

        if (series == null)
            throw new IllegalArgumentException("series with id " + seriesId + " doesn't belong to this set");

        return series;
    }

    public Collection<Series> getAll() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    public void add(Series series) {
        if (this.contains(series))
            throw new IllegalArgumentException("series " + series.id() + " already belongs to this set");

        this.map.put(String.valueOf(series.id()), series);
    }

    public void addAll(Collection<Series> collection) {
        if (collection == null)
            throw new IllegalArgumentException("collection shouldn't be null");

        for (Series s : collection) {
            this.add(s);
        }
    }

    public void remove(Series series) {
        if (!this.contains(series))
            throw new IllegalArgumentException("series " + series.id() + " doesn't belong to this set");

        this.map.remove(String.valueOf(series.id()));
    }

    public void clear() {
        this.map.clear();
    }

    @Override
    public Iterator<Series> iterator() {
        return this.map.values().iterator();
    }

    public boolean addListener(DomainObjectListener<SeriesSet> listener) {
        return this.listeners.add(listener);
    }

    public boolean removeListener(DomainObjectListener<SeriesSet> listener) {
        return this.listeners.remove(listener);
    }

    public void notifyListeners() {
        for (DomainObjectListener<SeriesSet> listener : this.listeners) {
            listener.onUpdate(this);            
        }
    }

}
