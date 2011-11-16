package br.edu.ufcg.aweseries.repository;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesSet;

public class SeriesCachedRepository implements SeriesRepository {
    private SeriesRepository sourceRepository;
    private SeriesSet seriesSet;
    private ExecutorService threadExecutor;

    public SeriesCachedRepository(SeriesRepository sourceRepository) {
        if (sourceRepository == null) {
            throw new IllegalArgumentException("sourceRepository should not be null");
        }

        this.sourceRepository = sourceRepository;

        this.seriesSet = new SeriesSet();
        this.seriesSet.addAll(this.sourceRepository.getAll());

        this.threadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void insert(Series series) {
        this.seriesSet.add(series);
        this.threadExecutor.execute(this.insertSeriesInSourceRepository(series));
    }

    private Runnable insertSeriesInSourceRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCachedRepository.this.sourceRepository.insert(series);
            }
        };
    }

    @Override
    public void update(Series series) {
        this.seriesSet.remove(series);
        this.seriesSet.add(series);
        this.threadExecutor.execute(this.updateSeriesInSourceRepository(series));
    }

    private Runnable updateSeriesInSourceRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCachedRepository.this.sourceRepository.update(series);
            }
        };
    }

    @Override
    public void delete(Series series) {
        this.seriesSet.remove(series);
        this.threadExecutor.execute(this.deleteSeriesFromSourceRepository(series));
    }

    private Runnable deleteSeriesFromSourceRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCachedRepository.this.sourceRepository.delete(series);
            }
        };
    }

    @Override
    public void clear() {
        this.seriesSet.clear();
        this.threadExecutor.execute(this.deleteAllSeriesFromSourceRepository());
    }

    private Runnable deleteAllSeriesFromSourceRepository() {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCachedRepository.this.sourceRepository.clear();
            }
        };
    }

    @Override
    public boolean contains(Series series) {
        return this.seriesSet.contains(series);
    }

    @Override
    public Series get(String seriesId) {
        return this.seriesSet.get(seriesId);
    }

    @Override
    public Collection<Series> getAll() {
        return this.seriesSet.getAll();
    }
}
