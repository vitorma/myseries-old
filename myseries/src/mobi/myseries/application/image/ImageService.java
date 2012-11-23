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
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ImageNotFoundException;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.os.AsyncTask;

// TODO(Gabriel) Log the operations of this service.
public final class ImageService {
    private ImageServiceRepository imageRepository;
    private ImageSource imageSource;
    private ListenerSet<SeriesPosterDownloadListener> posterDownloadListeners;
    private ListenerSet<EpisodeImageDownloadListener> episodeImageDownloadListeners;

    public ImageService(ImageSource imageSource, ImageServiceRepository imageRepository) {
        Validate.isNonNull(imageSource, "imageSource");
        Validate.isNonNull(imageRepository, "imageRepository");

        this.imageSource = imageSource;
        this.imageRepository = imageRepository;
        this.posterDownloadListeners = new ListenerSet<SeriesPosterDownloadListener>();
        this.episodeImageDownloadListeners = new ListenerSet<EpisodeImageDownloadListener>();
    }

    public Bitmap getPosterOf(Series series) {
        return this.imageRepository.getPosterOf(series);
    }

    public Bitmap getImageOf(Episode episode) {
        return this.imageRepository.getImageOf(episode);
    }

    public void removeAllImagesOf(Series series) {
        this.imageRepository.deleteAllImagesOf(series);
    }

    // TODO(Cleber, Gabriel) Download of series posters should be synchronous.
    public void downloadPosterOf(Series series) {
        Validate.isNonNull(series, "series");

        if (Strings.isNullOrBlank(series.posterFileName())) {return;}

        new SeriesPosterDownload(series).execute();
    }

    public void downloadImageOf(Episode episode) {
        Validate.isNonNull(episode, "episode");

        if (Strings.isNullOrBlank(episode.imageFileName())) {return;}

        new EpisodeImageDownload(episode).execute();
    };

    // TODO(Cleber, Gabriel) remove this. Download of series posters should be synchronous.
    public boolean register(SeriesPosterDownloadListener listener) {
        return this.posterDownloadListeners.register(listener);
    }

    public boolean register(EpisodeImageDownloadListener listener) {
        return this.episodeImageDownloadListeners.register(listener);
    }

    // TODO(Cleber, Gabriel) remove this. Download of series posters should be synchronous.
    public boolean deregister(SeriesPosterDownloadListener listener) {
        return this.posterDownloadListeners.deregister(listener);
    }

    public boolean deregister(EpisodeImageDownloadListener listener) {
        return this.episodeImageDownloadListeners.deregister(listener);
    }

    // TODO(Cleber, Gabriel) remove this. Download of series posters should be synchronous.
    private void notifyListenersOfFinishDownloadingPosterOf(Series series) {
        for (SeriesPosterDownloadListener listener : this.posterDownloadListeners) {
            listener.onFinishDownloadingPosterOf(series);
        }
    }

    private void notifyListenersOfFinishDownloadingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener listener : this.episodeImageDownloadListeners) {
            listener.onFinishDownloadingImageOf(episode);
        }
    }

    // TODO(Cleber, Gabriel) remove this. Download of series posters should be synchronous.
    private void notifyListenersOfStartDownloadingPosterOf(Series series) {
        for (SeriesPosterDownloadListener listener : this.posterDownloadListeners) {
            listener.onStartDownloadingPosterOf(series);
        }
    }

    private void notifyListenersOfStartDownloadingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener l : this.episodeImageDownloadListeners) {
            l.onStartDownloadingImageOf(episode);
        }
    }

     // TODO(Cleber, Gabriel) remove this. Download of series posters should be synchronous.
    private class SeriesPosterDownload extends AsyncTask<Void, Void, Void> {
        private Series series;

        private SeriesPosterDownload(Series series) {
            this.series = series;
        }

        @Override
        protected void onPreExecute() {
            ImageService.this.notifyListenersOfStartDownloadingPosterOf(this.series);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap fetchedPoster = null;

            try {
                fetchedPoster = ImageService.this.imageSource.fetchSeriesPoster(this.series.posterFileName());
            } catch (ConnectionFailedException e) {
                return null;
            } catch (ConnectionTimeoutException e) {
                return null;
            } catch (ImageNotFoundException e) {
                return null;
            }

            ImageService.this.imageRepository.saveSeriesPoster(this.series, fetchedPoster);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageService.this.notifyListenersOfFinishDownloadingPosterOf(this.series);
        }
    }

    private class EpisodeImageDownload extends AsyncTask<Void, Void, Void> {
        private Episode episode;

        private EpisodeImageDownload(Episode episode) {
            this.episode = episode;
        }

        @Override
        protected void onPreExecute() {
            ImageService.this.notifyListenersOfStartDownloadingImageOf(this.episode);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap fetchedImage = null;

            try {
                fetchedImage = ImageService.this.imageSource.fetchEpisodeImage(this.episode.imageFileName());
            } catch (ConnectionFailedException e) {
                return null;
            } catch (ConnectionTimeoutException e) {
                return null;
            } catch (ImageNotFoundException e) {
                return null;
            }

            ImageService.this.imageRepository.saveEpisodeImage(this.episode, fetchedImage);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageService.this.notifyListenersOfFinishDownloadingImageOf(this.episode);
        }
    }
}
