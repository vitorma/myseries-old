/*
 *   SeriesCachedRepository.java
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


package br.edu.ufcg.aweseries.series_repository;

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
        if (sourceRepository == null)
            throw new IllegalArgumentException("sourceRepository should not be null");

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
    public void updateAll(Collection<Series> seriesCollection) {
        this.seriesSet.clear();
        this.seriesSet.addAll(seriesCollection);
        this.threadExecutor.execute(this.updateAllSeriesInSourceRepository(seriesCollection));
    }

    private Runnable updateAllSeriesInSourceRepository(final Collection<Series> seriesCollection) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCachedRepository.this.sourceRepository.updateAll(seriesCollection);
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
    public Series get(int seriesId) {
        return this.seriesSet.get(seriesId);
    }

    @Override
    public Collection<Series> getAll() {
        return this.seriesSet.getAll();
    }
}
