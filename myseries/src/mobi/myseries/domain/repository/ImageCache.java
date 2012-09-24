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

import mobi.myseries.domain.model.Episode;
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
        for (Series series : this.seriesRepository.getAll()) {
            this.seriesPosters.put(series.id(), this.sourceRepository.getPosterOf(series));
        }
    }

    @Override
    public void saveSeriesPoster(Series series, Bitmap file) {
        Validate.isNonNull(series, "series");

        if (file == null && this.seriesPosters.get(series.id()) != null) {return;}

        this.seriesPosters.put(series.id(), file);

        if (file == null) {return;}

        this.threadExecutor.execute(this.saveSeriesPosterInSourceRepository(series, file));
    }

    private Runnable saveSeriesPosterInSourceRepository(final Series series, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.saveSeriesPoster(series, file);
            }
        };
    }

    @Override
    public void saveEpisodeImage(Episode episode, Bitmap file) {
        Validate.isNonNull(episode, "episode");

        if (file == null) {return;}

        this.threadExecutor.execute(this.saveEpisodeImageInSourceRepository(episode, file));
    }

    private Runnable saveEpisodeImageInSourceRepository(final Episode episode, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.saveEpisodeImage(episode, file);
            }
        };
    }

    @Override
    public void deleteAllImagesOf(Series series) {
        Validate.isNonNull(series, "series");

        if (this.seriesPosters.indexOfKey(series.id()) == -1) {return;}

        this.seriesPosters.remove(series.id());
        this.threadExecutor.execute(this.deleteAllImagesOfSeriesInSourceRepository(series));
    }

    private Runnable deleteAllImagesOfSeriesInSourceRepository(final Series series) {
        return new Runnable() {
            @Override
            public void run() {
                ImageCache.this.sourceRepository.deleteAllImagesOf(series);
            }
        };
    }

    @Override
    public Bitmap getPosterOf(Series series) {
        Validate.isNonNull(series, "series");

        return this.seriesPosters.get(series.id());
    }

    @Override
    public Bitmap getImageOf(Episode episode) {
        return this.sourceRepository.getImageOf(episode);
    }
}
