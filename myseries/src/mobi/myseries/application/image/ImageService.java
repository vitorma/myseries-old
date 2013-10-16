package mobi.myseries.application.image;

import java.io.BufferedInputStream;
import java.io.InputStream;

import mobi.myseries.application.Communications;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.Validate;
import mobi.myseries.shared.imageprocessing.BitmapResizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

// TODO(Gabriel) Log the operations of this service.
public class ImageService {
    private final int mySchedulePosterWidth;
    private final int mySchedulePosterHeight;

    private final ImageServiceRepository imageRepository;
    private final Communications communications;

    private final ListenerSet<EpisodeImageDownloadListener> episodeImageDownloadListeners;
    private final int mySeriesPosterWidth;
    private final int mySeriesPosterHeight;

    public ImageService(
            ImageServiceRepository imageRepository,
            Communications communications,
            int mySeriesPosterWidth,
            int mySeriesPosterHeight,
            int mySchedulePosterWidth,
            int mySchedulePosterHeight) {
        Validate.isNonNull(imageRepository, "imageRepository");
        Validate.isNonNull(communications, "communications");

        this.imageRepository = imageRepository;
        this.communications = communications;

        this.mySeriesPosterWidth = mySeriesPosterWidth;
        this.mySeriesPosterHeight = mySeriesPosterHeight;
        this.mySchedulePosterWidth = mySchedulePosterWidth;
        this.mySchedulePosterHeight = mySchedulePosterHeight;

        this.episodeImageDownloadListeners = new ListenerSet<EpisodeImageDownloadListener>();
    }

    public Bitmap getPosterOf(Series series) {
        return this.imageRepository.getPosterOf(series);
    }

    public Bitmap getSmallPosterOf(Series series) {
        return this.imageRepository.getSmallPosterOf(series);
    }

    public Bitmap getImageOf(Episode episode) {
        return this.imageRepository.getImageOf(episode);
    }

    public void removeAllImagesOf(Series series) {
        this.imageRepository.deleteAllImagesOf(series);
    }

    public void downloadAndSavePosterOf(Series series) {
        Validate.isNonNull(series, "series");

        if (Strings.isNullOrBlank(series.posterUrl())) {
            return;
        }

        try {
            InputStream posterStream = new BufferedInputStream(this.getStream(series.posterUrl()));

            Bitmap poster = BitmapFactory.decodeStream(posterStream);
            Bitmap smallPoster = new BitmapResizer(poster).toSize(this.mySchedulePosterWidth, this.mySchedulePosterHeight);

            this.imageRepository.saveSeriesPoster(series, poster);
            this.imageRepository.saveSmallSeriesPoster(series, smallPoster);
        } catch (Exception e) {
            //TODO Log
        }
    }

    // TODO(Gabriel) Should we pass the seriesSearchListener as a parameter, to avoid the users not registering their listeners?
    public void downloadImageOf(Episode episode) {
        Validate.isNonNull(episode, "episode");

        if (Strings.isNullOrBlank(episode.screenUrl())) {
            return;
        }

        new EpisodeImageDownload(episode).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        private final Episode episode;

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
                InputStream screenStream = new BufferedInputStream(getStream(episode.screenUrl()));
                Bitmap screen = BitmapFactory.decodeStream(screenStream);

                ImageService.this.imageRepository.saveEpisodeImage(this.episode, screen);
            } catch (Exception e) {
                //TODO Log
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageService.this.notifyListenersOfFinishDownloadingImageOf(this.episode);
        }
    }

    public Bitmap getBannerOf(Series series) {
        return this.imageRepository.getBannerOf(series);
    }

    /* Auxiliary */

    private InputStream getStream(String url) throws ConnectionFailedException {
        return this.communications.streamFor(url);
    }

    public Bitmap getCachedSmallPosterOf(Series series) {
        return this.imageRepository.getCachedSmallPosterOf(series);
    }

    public Bitmap getCachedPosterOf(Series series) {
        return this.imageRepository.getCachedPosterOf(series);
    }
}
