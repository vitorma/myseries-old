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
    private ListenerSet<EpisodeImageDownloadListener> episodeImageDownloadListeners;

    public ImageService(ImageSource imageSource, ImageServiceRepository imageRepository) {
        Validate.isNonNull(imageSource, "imageSource");
        Validate.isNonNull(imageRepository, "imageRepository");

        this.imageSource = imageSource;
        this.imageRepository = imageRepository;
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

    public void downloadAndSavePosterOf(Series series) {
        Validate.isNonNull(series, "series");

        if (Strings.isNullOrBlank(series.posterFileName())) {
            return;
        }

        try {
            Bitmap fetchedPoster = this.imageSource.fetchSeriesPoster(series.posterFileName());
            this.imageRepository.saveSeriesPoster(series, fetchedPoster);
        } catch (ConnectionFailedException e) {
        } catch (ConnectionTimeoutException e) {
        } catch (ImageNotFoundException e) {}
    }

    // TODO(Gabriel) Should we pass the listener as a parameter, to avoid the
    // users not registering their listeners?
    public void downloadImageOf(Episode episode) {
        Validate.isNonNull(episode, "episode");

        if (Strings.isNullOrBlank(episode.imageFileName())) {return;}

        new EpisodeImageDownload(episode).execute();
    }

    public boolean register(EpisodeImageDownloadListener listener) {
        return this.episodeImageDownloadListeners.register(listener);
    }

    public boolean deregister(EpisodeImageDownloadListener listener) {
        return this.episodeImageDownloadListeners.deregister(listener);
    }

    private void notifyListenersOfFinishDownloadingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener listener : this.episodeImageDownloadListeners) {
            listener.onFinishDownloadingImageOf(episode);
        }
    }

    private void notifyListenersOfStartDownloadingImageOf(Episode episode) {
        for (EpisodeImageDownloadListener l : this.episodeImageDownloadListeners) {
            l.onStartDownloadingImageOf(episode);
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
            try {
                Bitmap fetchedImage = ImageService.this.imageSource.fetchEpisodeImage(this.episode.imageFileName());

                ImageService.this.imageRepository.saveEpisodeImage(this.episode, fetchedImage);
            } catch (ConnectionFailedException e) {
            } catch (ConnectionTimeoutException e) {
            } catch (ImageNotFoundException e) {}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageService.this.notifyListenersOfFinishDownloadingImageOf(this.episode);
        }
    }
}
