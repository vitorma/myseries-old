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

import java.util.LinkedList;
import java.util.List;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.ImageRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public final class ImageService {
    private ImageRepository imageRepository;
    private ImageSource imageSource;
    private List<PosterDownloadListener> posterDownloadListeners;
    private List<EpisodeImageDownloadListener> episodeImageDownloadListeners;

    public ImageService(ImageSource imageSource, ImageRepository imageRepository) {
        Validate.isNonNull(imageSource, "imageSource");
        Validate.isNonNull(imageRepository, "imageRepository");

        this.imageSource = imageSource;
        this.imageRepository = imageRepository;
        this.posterDownloadListeners = new LinkedList<PosterDownloadListener>();
        this.episodeImageDownloadListeners = new LinkedList<EpisodeImageDownloadListener>();
    }

    /* Current interface */

    public Bitmap getPosterOf(Series series) {
        Validate.isNonNull(series, "series");

        if (series.posterFileName() != null && !Strings.isBlank(series.posterFileName())) {
            return this.imageRepository.getSeriesPoster(series.id());
        }

        return null;
    }

    public Bitmap getImageOf(Episode episode) {//TODO Let this method be performed asynchronously (?)
        Validate.isNonNull(episode, "episode");

        if (episode.imageFileName() != null && !episode.imageFileName().equals("")) {
            return this.imageRepository.getEpisodeImage(episode.id());
        }

        return null;
    }

    public void saveSeriesPoster(Series series, Bitmap poster) {
        Validate.isNonNull(series, "series");

        this.imageRepository.saveSeriesPoster(series.id(), poster);
    }

    public void saveEpisodeImage(Episode episode, Bitmap poster) {
        Validate.isNonNull(episode, "episode");

        this.imageRepository.saveEpisodeImage(episode.id(), poster);
    }

    public void removeAllImagesOf(Series series) {
        Validate.isNonNull(series, "series");

        this.imageRepository.deleteAllImagesOfSeries(series.id());
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

            ImageService.this.imageRepository.saveSeriesPoster(this.series.id(), fetchedPoster);

            return null;
        }

        @Override
        protected void onCancelled() {
            switch (this.failure) {
                case CONNECTION_FAILED:
                    ImageService.this.notifyListenersOfConnectionFailureWhileDownloadingPosterOf(this.series);
                    break;
                case EXTERNAL_STORAGE_UNAVAILABLE:
                    ImageService.this.notifyListenersOfFailureWhileSavingPosterOf(this.series);
                    break;
                case IMAGE_IO:
                    ImageService.this.notifyListenersOfFailureWhileSavingPosterOf(this.series);
                    break;
                case UNKNOWN:
                    // TODO: PANIC!
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageService.this.notifyListenersOfDownloadPosterOf(this.series);
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

            ImageService.this.imageRepository.saveEpisodeImage(this.episode.id(), fetchedImage);

            return null;
        }

        @Override
        protected void onCancelled() {
            switch (this.failure) {
                case CONNECTION_FAILED:
                    ImageService.this
                            .notifyListenersOfConnectionFailureWhileDownloadingImageOf(this.episode);
                    break;
                case EXTERNAL_STORAGE_UNAVAILABLE:
                    ImageService.this.notifyListenersOfFailureWhileSavingImageOf(this.episode);
                    break;
                case IMAGE_IO:
                    ImageService.this.notifyListenersOfFailureWhileSavingImageOf(this.episode);
                    break;
                case UNKNOWN:
                    // TODO: PANIC!
                    throw new RuntimeException();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageService.this.notifyListenersOfDownloadImageOf(this.episode);
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

    // TODO (Cleber) Check if currently all these notifications are being used:
    // if true, use ListenerSet
    // if false, remove those unused

    public boolean register(PosterDownloadListener listener) {
        Validate.isNonNull(listener, "listener");

        for (PosterDownloadListener l : this.posterDownloadListeners) {
            if (l == listener) {
                return false;
            }
        }

        return this.posterDownloadListeners.add(listener);
    }

    public boolean register(EpisodeImageDownloadListener listener) {
        Validate.isNonNull(listener, "listener");

        for (EpisodeImageDownloadListener l : this.episodeImageDownloadListeners) {
            if (l == listener) {
                return false;
            }
        }

        return this.episodeImageDownloadListeners.add(listener);
    }

    public boolean deregister(PosterDownloadListener listener) {
        Validate.isNonNull(listener, "listener");

        for (int i = 0; i < this.posterDownloadListeners.size(); ++i) {
            if (this.posterDownloadListeners.get(i) == listener) {
                this.posterDownloadListeners.remove(i);
                return true;
            }
        }

        return false;
    }

    public boolean deregister(EpisodeImageDownloadListener listener) {
        Validate.isNonNull(listener, "listener");

        for (int i = 0; i < this.episodeImageDownloadListeners.size(); ++i) {
            if (this.episodeImageDownloadListeners.get(i) == listener) {
                this.episodeImageDownloadListeners.remove(i);
                return true;
            }
        }

        return false;
    }

    public void notifyListenersOfConnectionFailureWhileDownloadingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener l : this.episodeImageDownloadListeners) {
            l.onConnectionFailureWhileDownloadingImageOf(episode);
        }
    }

    private void notifyListenersOfConnectionFailureWhileDownloadingPosterOf(Series series) {
        for (PosterDownloadListener listener : this.posterDownloadListeners) {
            listener.onConnectionFailureWhileDownloadingPosterOf(series);
        }
    }

    public void notifyListenersOfDownloadImageOf(Episode episode) {
        for (EpisodeImageDownloadListener listener : this.episodeImageDownloadListeners) {
            listener.onDownloadImageOf(episode);
        }
    }

    private void notifyListenersOfDownloadPosterOf(Series series) {
        for (PosterDownloadListener listener : this.posterDownloadListeners) {
            listener.onDownloadPosterOf(series);
        }
    }

    public void notifyListenersOfFailureWhileSavingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener l : this.episodeImageDownloadListeners) {
            l.onFailureWhileSavingImageOf(episode);
        }

    }

    private void notifyListenersOfFailureWhileSavingPosterOf(Series series) {
        for (PosterDownloadListener listener : this.posterDownloadListeners) {
            listener.onFailureWhileSavingPosterOf(series);
        }
    }

    public void notifyListenersOfStartDownloadingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener l : this.episodeImageDownloadListeners) {
            l.onStartDownloadingImageOf(episode);
        }
    }

    public void notifyListenersOfStartDownloadingPosterOf(Series series) {
        for (PosterDownloadListener listener : this.posterDownloadListeners) {
            listener.onStartDownloadingPosterOf(series);
        }
    }
}
