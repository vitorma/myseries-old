package br.edu.ufcg.aweseries.data;

import java.util.Collection;

import br.edu.ufcg.aweseries.model.Series;

public abstract class SeriesChainedRepository implements Repository<Series> {
    private SeriesChainedRepository nextRepository;

    public SeriesChainedRepository nextRepository() {
        return this.nextRepository;
    }

    public void chainResponsibilityTo(SeriesChainedRepository nextRepository) {
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
