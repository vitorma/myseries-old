/*
 *   ImageService.java
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

package mobi.myseries.application.image;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.ImageRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public final class ImageService {
    private ImageRepository imageRepository;
    private ImageSource imageSource;
    private ListenerSet<PosterDownloadListener> posterDownloadListeners;
    private ListenerSet<EpisodeImageDownloadListener> episodeImageDownloadListeners;

    public ImageService(ImageSource imageSource, ImageRepository imageRepository) {
        Validate.isNonNull(imageSource, "imageSource");
        Validate.isNonNull(imageRepository, "imageRepository");

        this.imageSource = imageSource;
        this.imageRepository = imageRepository;
        this.posterDownloadListeners = new ListenerSet<PosterDownloadListener>();
        this.episodeImageDownloadListeners = new ListenerSet<EpisodeImageDownloadListener>();
    }

    /* Current interface */

    public Bitmap getPosterOf(Series series) {
        return this.imageRepository.getPosterOf(series);
    }

    public Bitmap getImageOf(Episode episode) {
        return this.imageRepository.getImageOf(episode);
    }

    public void removeAllImagesOf(Series series) {
        this.imageRepository.deleteAllImagesOf(series);
    }

    public void downloadPosterOf(Series series) {
        new DownloadPosterTask(series).execute();
    }

    public void downloadImageOf(Episode episode) {
        new DownloadEpisodeTask(episode).execute();
    };

    /* Download poster task */

    private class DownloadPosterTask extends AsyncTask<Void, Void, Void> {
        private Failure failure;
        private Series series;

        public DownloadPosterTask(Series series) {
            super();
            Validate.isNonNull(series, "series");

            this.series = series;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO: improve the treatment of these exception
            Bitmap fetchedPoster = null;

            try {
                fetchedPoster = ImageService.this.imageSource.fetchSeriesPoster(this.series.posterFileName());
            } catch (ConnectionFailedException e) {
                e.printStackTrace();
                this.cancel(true);
                this.failure = Failure.CONNECTION_FAILED;
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                this.failure = Failure.UNKNOWN;
            }

            ImageService.this.imageRepository.saveSeriesPoster(this.series, fetchedPoster);

            return null;
        }

        @Override
        protected void onCancelled() {
            switch (this.failure) {
                case CONNECTION_FAILED:
                    break;
                case EXTERNAL_STORAGE_UNAVAILABLE:
                    break;
                case IMAGE_IO:
                    break;
                case UNKNOWN:
                    // TODO: PANIC!
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageService.this.notifyListenersOfFinishDownloadingPosterOf(this.series);
        }

        @Override
        protected void onPreExecute() {
            ImageService.this.notifyListenersOfStartDownloadingPosterOf(this.series);
        }
    }

    /* Download episode image task */

    private class DownloadEpisodeTask extends AsyncTask<Void, Void, Void> {
        private Episode episode;
        private Failure failure;

        public DownloadEpisodeTask(Episode episode) {
            Validate.isNonNull(episode, "episode");
            this.episode = episode;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO: improve the treatment of these exceptions
            Bitmap fetchedImage = null;

            try {
                fetchedImage = ImageService.this.imageSource.fetchEpisodeImage(this.episode.imageFileName());
            } catch (ConnectionFailedException e) {
                e.printStackTrace();
                this.cancel(true);
                this.failure = Failure.CONNECTION_FAILED;
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                this.failure = Failure.UNKNOWN;
            }

            ImageService.this.imageRepository.saveEpisodeImage(this.episode, fetchedImage);

            return null;
        }

        @Override
        protected void onCancelled() {
            switch (this.failure) {
                case CONNECTION_FAILED:
                    break;
                case EXTERNAL_STORAGE_UNAVAILABLE:
                    break;
                case IMAGE_IO:
                    break;
                case UNKNOWN:
                    // TODO: PANIC!
                    throw new RuntimeException();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageService.this.notifyListenersOfFinishDownloadingImageOf(this.episode);
        }

        @Override
        protected void onPreExecute() {
            ImageService.this.notifyListenersOfStartDownloadingImageOf(this.episode);
        }
    }

    /* Failure (used by the tasks above) */

    private enum Failure {
        CONNECTION_FAILED, EXTERNAL_STORAGE_UNAVAILABLE, IMAGE_IO, UNKNOWN
    }

    /* Listeners */

    public boolean register(PosterDownloadListener listener) {
        return this.posterDownloadListeners.register(listener);
    }

    public boolean register(EpisodeImageDownloadListener listener) {
        return this.episodeImageDownloadListeners.register(listener);
    }

    public boolean deregister(PosterDownloadListener listener) {
        return this.posterDownloadListeners.deregister(listener);
    }

    public boolean deregister(EpisodeImageDownloadListener listener) {
        return this.episodeImageDownloadListeners.deregister(listener);
    }

    private void notifyListenersOfFinishDownloadingPosterOf(Series series) {
        for (PosterDownloadListener listener : this.posterDownloadListeners) {
            listener.onFinishDownloadingPosterOf(series);
        }
    }

    public void notifyListenersOfFinishDownloadingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener listener : this.episodeImageDownloadListeners) {
            listener.onFinishDownloadingImageOf(episode);
        }
    }

    public void notifyListenersOfStartDownloadingPosterOf(Series series) {
        for (PosterDownloadListener listener : this.posterDownloadListeners) {
            listener.onStartDownloadingPosterOf(series);
        }
    }

    public void notifyListenersOfStartDownloadingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener l : this.episodeImageDownloadListeners) {
            l.onStartDownloadingImageOf(episode);
        }
    }
}
