/*
 *   ImageProvider.java
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

package br.edu.ufcg.aweseries;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.series_repository.ImageRepository;
import br.edu.ufcg.aweseries.series_repository.exceptions.ExternalStorageNotAvailableException;
import br.edu.ufcg.aweseries.util.Validate;

public final class ImageProvider {
    private List<PosterDownloadListener> listeners;
    private ImageRepository imageRepository;
    public ImageSource imageSource;

    private class DownloadImageTask extends AsyncTask<Series, Void, Void> {
        private Series series;

        @Override
        protected Void doInBackground(Series... params) {
            this.series = params[0];

            //TODO: this verification shouldn't be here
            //TODO: Catch exception of download failure?
            if (this.series.posterFileName() != null && !this.series.posterFileName().equals("")) {

                Bitmap fetchedPoster = ImageProvider.this.imageSource.fetchSeriesPoster(this.series
                        .posterFileName());
                try {
                    ImageProvider.this.imageRepository.insertSeriesPoster(this.series.id(),
                            fetchedPoster);
                } catch (ExternalStorageNotAvailableException e) {
                    // TODO: handle exception
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageProvider.this.notifyListenersOfDownloadPosterOf(this.series);
        }
    };

    public static ImageProvider
            newInstance(ImageSource imageSource, ImageRepository imageRepository) {
        return new ImageProvider(imageSource, imageRepository);
    }

    private ImageProvider(ImageSource imageSource, ImageRepository imageRepository) {
        this.imageSource = imageSource;
        this.imageRepository = imageRepository;
        this.listeners = new LinkedList<PosterDownloadListener>();
    }

    public void downloadImageOf(Series series) {
        new DownloadImageTask().execute(series);
    }

    private Bitmap genericPosterImage() {
        return BitmapFactory.decodeResource(App.environment().context().getResources(),
                R.drawable.small_poster_clapperboard);
    }

    public Bitmap getPosterOf(Series series) {
        Validate.isNonNull(series, "series");

        if (series.posterFileName() != null && !series.posterFileName().equals("")) {
            Bitmap poster = this.imageRepository.getSeriesPoster(series.id());

            if (poster != null) {
                return poster;
            }
        }

        return this.genericPosterImage();
    }

    public void notifyListenersOfDownloadPosterOf(Series series) {
        for (PosterDownloadListener listener : this.listeners) {
            listener.onDownloadPosterOf(series);
        }
    }

    public boolean register(PosterDownloadListener listener) {
        Validate.isNonNull(listener, "listener");

        for (PosterDownloadListener l : this.listeners) {
            if (l == listener) {
                return false;
            }
        }

        return this.listeners.add(listener);
    }

    public boolean deregister(PosterDownloadListener listener) {
        Validate.isNonNull(listener, "listener");

        for (int i = 0; i < this.listeners.size(); ++i) {
            if (this.listeners.get(i) == listener) {
                this.listeners.remove(i);
                return true;
            }
        }

        return false;
    }

}
