package br.edu.ufcg.aweseries.repository;

import java.util.Collection;

import br.edu.ufcg.aweseries.model.Series;

public interface SeriesRepository {

    public abstract void insert(Series series);

    public abstract void update(Series series);

    public abstract void delete(Series series);

    public abstract void clear();

    public abstract boolean contains(Series series);

    public abstract Series get(String id);

    public abstract Collection<Series> getAll();
}
