package br.edu.ufcg.aweseries.repository;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesSet;

public class SeriesCache extends SeriesChainedRepository {
    private static final Context context = App.environment().context();

    private SeriesSet currentSeriesSet;
    private ExecutorService threadPoolExecutor;

    public SeriesCache() {
        super.chainResponsibilityTo(new SeriesDatabase(context));

        this.currentSeriesSet = new SeriesSet();
        this.currentSeriesSet.addAll(this.nextRepository().getAll());

        this.threadPoolExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void chainResponsibilityTo(Repository<Series> nextRepository) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(Series series) {
        this.currentSeriesSet.add(series);
        this.threadPoolExecutor.execute(this.createResponsibilityOfInsertSeriesForNextRepository(series));
    }

    private Runnable createResponsibilityOfInsertSeriesForNextRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.nextRepository().insert(series);
            }
        };
    }

    @Override
    public void update(Series series) {
        this.threadPoolExecutor.execute(this.createResponsibilityOfUpdateSeriesForNextRepository(series));
    }

    private Runnable createResponsibilityOfUpdateSeriesForNextRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.nextRepository().update(series);
            }
        };
    }

    @Override
    public void delete(Series series) {
        this.currentSeriesSet.remove(series);
        this.threadPoolExecutor.execute(this.createResponsibilityOfDeleteSeriesForNextRepository(series));
    }

    private Runnable createResponsibilityOfDeleteSeriesForNextRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.nextRepository().delete(series);
            }
        };
    }

    @Override
    public void clear() {
        this.currentSeriesSet.clear();
        this.threadPoolExecutor.execute(this.createResponsibilityOfDeleteAllSeriesForNextRepository());
    }

    private Runnable createResponsibilityOfDeleteAllSeriesForNextRepository() {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.nextRepository().clear();
            }
        };
    }

    @Override
    public boolean contains(Series series) {
        return this.currentSeriesSet.contains(series);
    }

    @Override
    public Series get(String seriesId) {
        return this.currentSeriesSet.get(seriesId);
    }

    @Override
    public Collection<Series> getAll() {
        return this.currentSeriesSet.getAll();
    }
}
