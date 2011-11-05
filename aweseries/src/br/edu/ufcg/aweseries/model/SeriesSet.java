package br.edu.ufcg.aweseries.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SeriesSet implements Iterable<Series> {
    private Map<String, Series> map;

    public SeriesSet() {
        this.map = new HashMap<String, Series>();
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean contains(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series shouldn't be null");
        }

        return this.map.containsKey(series.getId());
    }

    public Series get(String seriesId) {
        Series series = this.map.get(seriesId);

        if (series == null) {
            throw new IllegalArgumentException("series with id " + seriesId + " doesn't belong to this set");
        }

        return series;
    }

    public Collection<Series> getAll() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    public void add(Series series) {
        if (this.contains(series)) {
            throw new IllegalArgumentException("series " + series + " already belongs to this set");
        }

        this.map.put(series.getId(), series);
    }

    public void addAll(Collection<Series> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("collection shouldn't be null");
        }

        for (Series s : collection) {
            this.add(s);
        }
    }

    public void remove(Series series) {
        if (!this.contains(series)) {
            throw new IllegalArgumentException("series " + series + " doesn't belong to this set");
        }

        this.map.remove(series.getId());
    }

    public void clear() {
        this.map.clear();
    }

    @Override
    public Iterator<Series> iterator() {
        return this.map.values().iterator();
    }
}
