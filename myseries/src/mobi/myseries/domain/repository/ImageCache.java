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
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

//TODO Implement this class and save the planet!
public class ImageCache implements ImageRepository {
    private ImageRepository sourceRepository;
    private ExecutorService threadExecutor;
    private SparseArray<Bitmap> seriesPosters;
    private SeriesRepository seriesRepository;

    public ImageCache(ImageRepository sourceRepository, SeriesRepository seriesRepository) {
        this.sourceRepository = sourceRepository;
        this.seriesRepository = seriesRepository;
        this.threadExecutor = Executors.newSingleThreadExecutor();

        this.seriesPosters = new SparseArray<Bitmap>();
        for (Series s :this.seriesRepository.getAll()) {
            try {
                this.seriesPosters.put(s.id(), this.sourceRepository.getSeriesPoster(s.id()));
            } catch (ExternalStorageNotAvailableException e) {
                Log.d("ImageCache", e.getStackTrace().toString());
            }
        }
    }

    @Override
    public void insertSeriesPoster(int seriesId, Bitmap file) throws ExternalStorageNotAvailableException {
        // TODO Auto-generated method stub

    }

    private Runnable insertSeriesInSourceRepository(final int seriesId, final Bitmap file) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    ImageCache.this.sourceRepository.insertSeriesPoster(seriesId, file);
                } catch (ExternalStorageNotAvailableException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void insertEpisodeImage(int episodeId, Bitmap file) throws ExternalStorageNotAvailableException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateSeriesPoster(int seriesId, Bitmap file) throws ExternalStorageNotAvailableException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateEpisodeImage(int episodeId, Bitmap file) throws ExternalStorageNotAvailableException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteImagesOfSeries(int seriesId) throws ExternalStorageNotAvailableException {
        // TODO Auto-generated method stub

    }

    private void deleteEpisodeImage(int episodeId) throws ExternalStorageNotAvailableException {
        // TODO Auto-generated method stub

    }

    @Override
    public Bitmap getSeriesPoster(int seriesId) throws ExternalStorageNotAvailableException {
        return this.seriesPosters.get(seriesId);
    }

    @Override
    public Bitmap getEpisodeImage(int episodeId) throws ExternalStorageNotAvailableException {
        // TODO Auto-generated method stub
        return null;
    }
}
