/*
 *   SeriesCache.java
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

package mobi.myseries.domain.repository.series;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.util.SparseArray;

public class SeriesCache implements SeriesRepository {
    private SeriesRepository sourceRepository;
    private SeriesSet seriesSet;
    private ExecutorService threadExecutor;

    public SeriesCache(SeriesRepository sourceRepository) {
        Validate.isNonNull(sourceRepository, "sourceRepository");

        this.sourceRepository = sourceRepository;
        this.seriesSet = new SeriesSet().includingAll(this.sourceRepository.getAll());
        this.threadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void insert(Series series) {
        Validate.isNonNull(series, "series");

        if (this.seriesSet.contains(series)) {return;}

        this.seriesSet.including(series);
        this.threadExecutor.execute(this.insertSeriesInSourceRepository(series));
    }

    private Runnable insertSeriesInSourceRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.sourceRepository.insert(series);
            }
        };
    }

    @Override
    public void update(Series series) {
        Validate.isNonNull(series, "series");

        if (!this.seriesSet.contains(series)) {return;}

        this.seriesSet.excluding(series).including(series);
        this.threadExecutor.execute(this.updateSeriesInSourceRepository(series));
    }

    private Runnable updateSeriesInSourceRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.sourceRepository.update(series);
            }
        };
    }

    @Override
    public void update(Episode episode) {
        Validate.isNonNull(episode, "series");

        if (!this.seriesSet.contains(episode)) {return;}
        this.threadExecutor.execute(this.updateEpisodeInSourceRepository(episode));
    }

    private Runnable updateEpisodeInSourceRepository(final Episode episode) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.sourceRepository.update(episode);
            }
        };
    }

    @Override
    public void updateAll(Collection<Series> seriesCollection) {
        Validate.isNonNull(seriesCollection, "seriesCollection");

        if (!this.seriesSet.containsAll(seriesCollection)) {return;}
        this.seriesSet.excludingAll(seriesCollection).includingAll(seriesCollection);
        this.threadExecutor.execute(this.updateAllSeriesInSourceRepository(seriesCollection));
    }

    private Runnable updateAllSeriesInSourceRepository(final Collection<Series> seriesCollection) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.sourceRepository.updateAll(seriesCollection);
            }
        };
    }

    @Override
    public void updateAllEpisodes(Collection<Episode> episodeCollection) {
        Validate.isNonNull(episodeCollection, "episodeCollection");

        if (!this.seriesSet.containsAllEpisodes(episodeCollection)) {return;}
        this.threadExecutor.execute(this.updateAllEpisodesInSourceRepository(episodeCollection));
    }

    private Runnable updateAllEpisodesInSourceRepository(final Collection<Episode> episodeCollection) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.sourceRepository.updateAllEpisodes(episodeCollection);
            }
        };
    }

    @Override
    public void delete(Series series) {
        Validate.isNonNull(series, "series");

        if (!this.seriesSet.contains(series)) {return;}

        this.seriesSet.excluding(series);
        this.threadExecutor.execute(this.deleteSeriesFromSourceRepository(series));
    }

    private Runnable deleteSeriesFromSourceRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.sourceRepository.delete(series);
            }
        };
    }

    @Override
    public void deleteAll(Collection<Series> seriesCollection) {
        Validate.isNonNull(seriesCollection, "seriesCollection");

        if (!this.seriesSet.containsAll(seriesCollection)) {return;}

        this.seriesSet.excludingAll(seriesCollection);
        this.threadExecutor.execute(this.deleteAllSeriesFromSourceRepository(seriesCollection));
    }

    private Runnable deleteAllSeriesFromSourceRepository(final Collection<Series> seriesCollection) {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.sourceRepository.deleteAll(seriesCollection);
            }
        };
    }

    @Override
    public void clear() {
        this.seriesSet.clear();
        this.threadExecutor.execute(this.clearSourceRepository());
    }

    private Runnable clearSourceRepository() {
        return new Runnable() {
            @Override
            public void run() {
                SeriesCache.this.sourceRepository.clear();
            }
        };
    }

    @Override
    public boolean contains(Series series) {
        Validate.isNonNull(series, "series");

        return this.seriesSet.contains(series);
    }

    @Override
    public Series get(int seriesId) {
        return this.seriesSet.get(seriesId);
    }

    @Override
    public Collection<Series> getAll() {
        return this.seriesSet.all();
    }

    //------------------------------------------------------------------------------------------------------------------

    private static class SeriesSet {
        private SparseArray<Series> series;

        private SeriesSet() {
            this.series = new SparseArray<Series>();
        }

        private boolean contains(Series series) {
             return this.get(series.id()) != null;
        }

        private boolean contains(Episode e) {
            return this.get(e.seriesId()).season(e.seasonNumber()).episode(e.number()) != null;
        }

        private boolean containsAll(Collection<Series> seriesCollection) {
            for (Series s : seriesCollection) {
                if (!this.contains(s)) {
                    return false;
                }
            }

            return true;
        }

        private boolean containsAllEpisodes(Collection<Episode> episodeCollection) {
            for (Episode e : episodeCollection) {
                if (!this.contains(e)) {
                    return false;
                }
            }

            return true;
        }

        private Series get(int seriesId) {
            return this.series.get(seriesId);
        }

        private Collection<Series> all() {
            LinkedList<Series> all = new LinkedList<Series>();

            for (int i = 0; i < this.series.size(); i++) {
                int id = this.series.keyAt(i);
                all.add(this.get(id));
            }

            return all;
        }

        private SeriesSet including(Series series) {
            this.series.put(series.id(), series);
            return this;
        }

        private SeriesSet includingAll(Collection<Series> seriesCollection) {
            for (Series s : seriesCollection) {
                this.including(s);
            }
            return this;
        }

        private SeriesSet excluding(Series series) {
            this.series.remove(series.id());
            return this;
        }

        private SeriesSet excludingAll(Collection<Series> seriesCollection) {
            for (Series s : seriesCollection) {
                this.excluding(s);
            }
            return this;
        }

        private SeriesSet clear() {
            this.series.clear();
            return this;
        }
    }

    @Override
    public void exportTo(String backupFilePath) throws IOException {
        this.sourceRepository.exportTo(backupFilePath);
        
    }

    @Override
    public void restoreFrom(String backupFilePath) throws IOException, InvalidDBSourceFileException {
        this.sourceRepository.restoreFrom(backupFilePath);
        this.seriesSet = new SeriesSet().includingAll(this.sourceRepository.getAll());
        
    }
}
