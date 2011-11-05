package br.edu.ufcg.aweseries.repository;

import java.util.Collection;

import br.edu.ufcg.aweseries.model.Series;

public abstract class SeriesChainedRepository implements Repository<Series> {
    private Repository<Series> nextRepository;

    public Repository<Series> nextRepository() {
        return this.nextRepository;
    }

    public void chainResponsibilityTo(Repository<Series> nextRepository) {
        if (nextRepository == null) {
            throw new IllegalArgumentException("nextRepository shouldn't be null");
        }

        this.nextRepository = nextRepository;
    }

    public abstract void insert(Series series);

    public abstract void update(Series series);

    public abstract void delete(Series series);

    public abstract void clear();

    public abstract boolean contains(String seriesId);

    public abstract Series get(String seriesId);

    public abstract Collection<Series> getAll();
}
