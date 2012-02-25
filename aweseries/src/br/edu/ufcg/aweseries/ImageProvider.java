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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.series_repository.ImageIoException;
import br.edu.ufcg.aweseries.series_repository.ImageRepository;
import br.edu.ufcg.aweseries.series_source.ConnectionFailedException;
import br.edu.ufcg.aweseries.series_source.ImageSource;
import br.edu.ufcg.aweseries.series_repository.exceptions.ExternalStorageNotAvailableException;
import br.edu.ufcg.aweseries.util.Validate;

public final class ImageProvider {
    private List<PosterDownloadListener> listeners;
    private ImageRepository imageRepository;
    public ImageSource imageSource;

    private enum Failure {
        CONNECTION_FAILED, IMAGE_IO, EXTERNAL_STORAGE_UNAVAILABLE, UNKNOWN
    };

    //TODO implement
    private class DownloadEpisodeTask extends AsyncTask<Episode, Void, Void> {
        @Override
        protected Void doInBackground(Episode... params) {
            return null;
        }
    }

    private class DownloadPosterTask extends AsyncTask<Void, Void, Void> {
        private Failure failure;
        private Series series;

        public DownloadPosterTask(Series series) {
            super();
            Validate.isNonNull(series, "series");

            this.series = series;
        }

        @Override
        protected void onPreExecute() {
            ImageProvider.this.notifyListenersOfStartDownloadingPosterOf(this.series);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //TODO: improve the treatment of these exception

            Bitmap fetchedPoster = null;

            try {

                fetchedPoster = ImageProvider.this.imageSource.fetchSeriesPoster(this.series
                        .posterFileName());

            } catch (ConnectionFailedException e) {
                e.printStackTrace();
                this.cancel(true);
                this.failure = Failure.CONNECTION_FAILED;
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                this.failure = Failure.UNKNOWN;
            }

            try {

                ImageProvider.this.imageRepository.insertSeriesPoster(this.series.id(),
                        fetchedPoster);

            } catch (ImageIoException e) {
                //TODO notify someone?

                e.printStackTrace();
                this.failure = Failure.IMAGE_IO;
                this.cancel(true);
                return null;

            } catch (ExternalStorageNotAvailableException e) {

                //TODO notify someone?
                e.printStackTrace();
                this.failure = Failure.EXTERNAL_STORAGE_UNAVAILABLE;
                this.cancel(true);
                return null;

            } catch (Exception e) {
                e.printStackTrace();
                this.failure = Failure.UNKNOWN;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageProvider.this.notifyListenersOfDownloadPosterOf(this.series);
        }

        @Override
        protected void onCancelled() {
            switch (this.failure) {
                case CONNECTION_FAILED:
                    ImageProvider.this
                            .notifyListenersOfConnectionFailureWhileDownloadingPosterOf(this.series);
                    break;

                case EXTERNAL_STORAGE_UNAVAILABLE:
                    ImageProvider.this.notifyListenersOfFailureWhileSavingPosterOf(this.series);
                    break;

                case IMAGE_IO:
                    ImageProvider.this.notifyListenersOfFailureWhileSavingPosterOf(this.series);
                    break;

                case UNKNOWN:
                    //TODO: PANIC!
            }
        }
    };

    public static ImageProvider newInstance(ImageSource imageSource, ImageRepository imageRepository) {
        return new ImageProvider(imageSource, imageRepository);
    }

    public void notifyListenersOfStartDownloadingPosterOf(Series series) {
        for (PosterDownloadListener listener : this.listeners) {
            listener.onStartDownloadingPosterOf(series);
        }
    }

    private void notifyListenersOfFailureWhileSavingPosterOf(Series series) {
        for (PosterDownloadListener listener : this.listeners) {
            listener.onFailureWhileSavingPosterOf(series);
        }
    }

    private void notifyListenersOfConnectionFailureWhileDownloadingPosterOf(Series series) {
        for (PosterDownloadListener listener : this.listeners) {
            listener.onConnectionFailureWhileDownloadingPosterOf(series);
        }
    }

    private ImageProvider(ImageSource imageSource, ImageRepository imageRepository) {
        this.imageSource = imageSource;
        this.imageRepository = imageRepository;
        this.listeners = new LinkedList<PosterDownloadListener>();
    }

    public void downloadImageOf(Series series) {
        new DownloadPosterTask(series).execute();
    }

    private Bitmap genericPosterImage() {
        return BitmapFactory.decodeResource(App.environment().context().getResources(),
                R.drawable.small_poster_clapperboard);
    }

    public void removePosterOf(Series series) {
        try {
            this.imageRepository.deleteSeriesPoster(series.id());
        } catch (ImageIoException e) {
            //TODO: Just ignore it if not found, do something if sdcard was removed
        }
    }

    public Bitmap getPosterOf(Series series) {
        Validate.isNonNull(series, "series");

        if (series.posterFileName() != null && !series.posterFileName().equals("")) {
            Bitmap poster = this.imageRepository.getSeriesPoster(series.id());

            if (poster != null) {
                return poster;
            }

            else {
                this.downloadImageOf(series);
            }

        }

        return this.genericPosterImage();
    }

    private void notifyListenersOfDownloadPosterOf(Series series) {
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
