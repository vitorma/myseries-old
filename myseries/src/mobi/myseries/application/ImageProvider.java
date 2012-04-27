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

package mobi.myseries.application;

import java.util.LinkedList;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.ExternalStorageNotAvailableException;
import mobi.myseries.domain.repository.ImageIoException;
import mobi.myseries.domain.repository.ImageRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.Validate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public final class ImageProvider {

    private class DownloadEpisodeTask extends AsyncTask<Void, Void, Void> {
        private Episode episode;
        private Failure failure;

        public DownloadEpisodeTask(Episode episode) {
            Validate.isNonNull(episode, "episode");
            this.episode = episode;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //TODO: improve the treatment of these exceptions
            Bitmap fetchedImage = null;

            try {
                fetchedImage = ImageProvider.this.imageSource.fetchEpisodeImage(this.episode.imageFileName());
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
                ImageProvider.this.imageRepository.insertEpisodeImage(this.episode.id(), fetchedImage);
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
        protected void onCancelled() {
            switch (this.failure) {
                case CONNECTION_FAILED:
                    ImageProvider.this
                            .notifyListenersOfConnectionFailureWhileDownloadingImageOf(this.episode);
                    break;
                case EXTERNAL_STORAGE_UNAVAILABLE:
                    ImageProvider.this.notifyListenersOfFailureWhileSavingImageOf(this.episode);
                    break;
                case IMAGE_IO:
                    ImageProvider.this.notifyListenersOfFailureWhileSavingImageOf(this.episode);
                    break;
                case UNKNOWN:
                    //TODO: PANIC!
                    throw new RuntimeException();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageProvider.this.notifyListenersOfDownloadImageOf(this.episode);
        }

        @Override
        protected void onPreExecute() {
            ImageProvider.this.notifyListenersOfStartDownloadingImageOf(this.episode);
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
        protected Void doInBackground(Void... params) {
            //TODO: improve the treatment of these exception
            Bitmap fetchedPoster = null;

            try {
                fetchedPoster = ImageProvider.this.imageSource.fetchSeriesPoster(this.series.posterFileName());
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
                ImageProvider.this.imageRepository.insertSeriesPoster(this.series.id(), fetchedPoster);
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

        @Override
        protected void onPostExecute(Void result) {
            ImageProvider.this.notifyListenersOfDownloadPosterOf(this.series);
        }

        @Override
        protected void onPreExecute() {
            ImageProvider.this.notifyListenersOfStartDownloadingPosterOf(this.series);
        }
    }

    private enum Failure {
        CONNECTION_FAILED, EXTERNAL_STORAGE_UNAVAILABLE, IMAGE_IO, UNKNOWN
    }

    public static ImageProvider newInstance(ImageSource imageSource, ImageRepository imageRepository) {
        return new ImageProvider(imageSource, imageRepository);
    }

    private List<EpisodeImageDownloadListener> episodeImageDownloadListeners;
    private ImageRepository imageRepository;
    private ImageSource imageSource;
    private List<PosterDownloadListener> posterDownloadListeners;

    private ImageProvider(ImageSource imageSource, ImageRepository imageRepository) {
        this.imageSource = imageSource;
        this.imageRepository = imageRepository;
        this.posterDownloadListeners = new LinkedList<PosterDownloadListener>();
        this.episodeImageDownloadListeners = new LinkedList<EpisodeImageDownloadListener>();
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

    public void downloadImageOf(Episode episode) {
        new DownloadEpisodeTask(episode).execute();
    };

    public void downloadPosterOf(Series series) {
        new DownloadPosterTask(series).execute();
    }

    private Bitmap genericPosterImage() {
        return BitmapFactory.decodeResource(App.environment().context().getResources(),
                R.drawable.small_poster_clapperboard);
    }

    public Bitmap getImageOf(Episode episode) {
        Validate.isNonNull(episode, "episode");

        if (episode.imageFileName() != null && !episode.imageFileName().equals("")) {
            Bitmap image;

            try {
                image = this.imageRepository.getEpisodeImage(episode.id());
            } catch (ExternalStorageNotAvailableException e) {
                //TODO Auto-generated catch block
                return null;
            }

            return image;
        }

        return null;
    }

    public Bitmap getPosterOf(Series series) {
        Validate.isNonNull(series, "series");

        if (series.posterFileName() == null || Strings.isBlank(series.posterFileName())) {
            return this.genericPosterImage();
        }

        try {
            Bitmap poster = this.imageRepository.getSeriesPoster(series.id());
            return poster != null ? poster : this.genericPosterImage();
        } catch (ExternalStorageNotAvailableException e) {
            return this.genericPosterImage();
        }
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

    public boolean register(EpisodeImageDownloadListener listener) {
        Validate.isNonNull(listener, "listener");

        for (EpisodeImageDownloadListener l : this.episodeImageDownloadListeners) {
            if (l == listener) {
                return false;
            }
        }

        return this.episodeImageDownloadListeners.add(listener);
    }

    public boolean register(PosterDownloadListener listener) {
        Validate.isNonNull(listener, "listener");

        for (PosterDownloadListener l : this.posterDownloadListeners) {
            if (l == listener) {
                return false;
            }
        }

        return this.posterDownloadListeners.add(listener);
    }

    public void removeImageOf(Episode episode) {
        try {
            this.imageRepository.deleteEpisodeImage(episode.id());
        } catch (ImageIoException e) {
            //TODO: Just ignore it if not found, do something if sdcard was removed
        } catch (ExternalStorageNotAvailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void removePosterOf(Series series) {
        try {
            this.imageRepository.deleteSeriesPoster(series.id());
        } catch (ImageIoException e) {
            //TODO: Just ignore it if not found, do something if sdcard was removed
        } catch (ExternalStorageNotAvailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
