/*
 *   ImageCache.java
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

package mobi.myseries.domain.repository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.util.SparseArray;

public class ImageCache implements ImageRepository {
    private ImageRepository sourceRepository;
    private ExecutorService threadExecutor;
    private SparseArray<Bitmap> seriesPosters;
    private SeriesRepository seriesRepository;

    public ImageCache(ImageRepository sourceRepository, SeriesRepository seriesRepository) {
        Validate.isNonNull(sourceRepository, "sourceRepository");
        Validate.isNonNull(seriesRepository, "seriesRepository");

        this.sourceRepository = sourceRepository;
        this.seriesRepository = seriesRepository;
        this.threadExecutor = Executors.newSingleThreadExecutor();
        this.seriesPosters = new SparseArray<Bitmap>();

        this.loadPostersFromSourceRepository();
    }

    private void loadPostersFromSourceRepository() {
        for (Series s :this.seriesRepository.getAll()) {
            this.seriesPosters.put(s.id(), this.sourceRepository.getSeriesPoster(s.id()));
        }
    }

    @Override
    public void insertSeriesPoster(int seriesId, Bitmap file) {
        Validate.isNonNull(file, "file");

        if (this.seriesPosters.indexOfKey(seriesId) != -1) {return;}

        this.seriesPosters.put(seriesId, file);
        this.threadExecutor.execute(this.insertSeriesPosterIntoSourceRepository(seriesId, file));
    }

    private Runnable insertSeriesPosterIntoSourceRepository(final int seriesId, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.insertSeriesPoster(seriesId, file);
            }
        };
    }

    @Override
    public void insertEpisodeImage(int episodeId, Bitmap file) {
        Validate.isNonNull(file, "file");

        this.threadExecutor.execute(this.insertEpisodeImageIntoSourceRepository(episodeId, file));
    }

    private Runnable insertEpisodeImageIntoSourceRepository(final int episodeId, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.insertEpisodeImage(episodeId, file);
            }
        };
    }

    @Override
    public void updateSeriesPoster(int seriesId, Bitmap file) {
        Validate.isNonNull(file, "file");

        if (this.seriesPosters.indexOfKey(seriesId) == -1) {return;}

        this.seriesPosters.put(seriesId, file);
        this.threadExecutor.execute(this.updateSeriesPosterInSourceRepository(seriesId, file));
    }

    private Runnable updateSeriesPosterInSourceRepository(final int seriesId, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.updateSeriesPoster(seriesId, file);
            }
        };
    }

    @Override
    public void updateEpisodeImage(int episodeId, Bitmap file) {
        Validate.isNonNull(file, "file");

        this.updateEpisodeImageInSourceRepository(episodeId, file);
    }

    private Runnable updateEpisodeImageInSourceRepository(final int episodeId, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.updateEpisodeImage(episodeId, file);
            }
        };
    }

    @Override
    public void deleteImagesOfSeries(int seriesId) {
        if (this.seriesPosters.indexOfKey(seriesId) == -1) {return;}

        this.seriesPosters.remove(seriesId);
        this.threadExecutor.execute(this.deleteImagesOfSeriesInSourceRepository(seriesId));
    }

    private Runnable deleteImagesOfSeriesInSourceRepository(final int seriesId) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.deleteImagesOfSeries(seriesId);
            }
        };
    }

    @Override
    public Bitmap getSeriesPoster(int seriesId) {
        return this.seriesPosters.get(seriesId);
    }

    @Override
    public Bitmap getEpisodeImage(int episodeId) {
        return this.sourceRepository.getEpisodeImage(episodeId);
    }
}
