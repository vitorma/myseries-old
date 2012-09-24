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
    public void saveSeriesPoster(int seriesId, Bitmap file) {
        if (file == null && this.seriesPosters.get(seriesId) != null) {return;}

        this.seriesPosters.put(seriesId, file);

        if (file == null) {return;}

        this.threadExecutor.execute(this.saveSeriesPosterInSourceRepository(seriesId, file));
    }

    private Runnable saveSeriesPosterInSourceRepository(final int seriesId, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.saveSeriesPoster(seriesId, file);
            }
        };
    }

    @Override
    public void saveEpisodeImage(int episodeId, Bitmap file) {
        if (file == null) {return;}

        this.threadExecutor.execute(this.saveEpisodeImageInSourceRepository(episodeId, file));
    }

    private Runnable saveEpisodeImageInSourceRepository(final int episodeId, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.saveEpisodeImage(episodeId, file);
            }
        };
    }

    @Override
    public void deleteAllSeriesImages(int seriesId) {
        if (this.seriesPosters.indexOfKey(seriesId) == -1) {return;}

        this.seriesPosters.remove(seriesId);
        this.threadExecutor.execute(this.deleteAllSeriesImagesInSourceRepository(seriesId));
    }

    private Runnable deleteAllSeriesImagesInSourceRepository(final int seriesId) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.deleteAllSeriesImages(seriesId);
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
