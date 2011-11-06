package br.edu.ufcg.aweseries.repository;

import java.util.Collection;

import br.edu.ufcg.aweseries.model.Series;

public interface Repository<T> {

    public abstract void insert(T t);

    public abstract void update(T t);

    public abstract void delete(T t);

    public abstract void clear();

    public abstract boolean contains(Series series);

    public abstract Series get(String id);

    public abstract Collection<T> getAll();
}
