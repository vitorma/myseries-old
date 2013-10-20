package mobi.myseries.application.image;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import mobi.myseries.application.Communications;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.SearchResult;
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

    private final ImageServiceRepository imageRepository;
    private final Communications communications;

    private final int mySeriesPosterWidth;
    private final int mySeriesPosterHeight;
    private final int mySchedulePosterWidth;
    private final int mySchedulePosterHeight;

    private final ListenerSet<EpisodeImageDownloadListener> episodeImageDownloadListeners;

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
            InputStream posterStream = this.getStream(series.posterUrl());

            Bitmap originalPoster = BitmapFactory.decodeStream(posterStream);
            BitmapResizer posterResizer = new BitmapResizer(originalPoster);

            // TODO(Gabriel) for some reason, resizing made these posters terrible.
            Bitmap poster = originalPoster; //posterResizer.toSize(this.mySeriesPosterWidth, this.mySeriesPosterHeight);
            Bitmap smallPoster = posterResizer.toSize(this.mySchedulePosterWidth, this.mySchedulePosterHeight);

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
                InputStream screenStream = getStream(episode.screenUrl());
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

    public Bitmap getCachedSmallPosterOf(Series series) {
        return this.imageRepository.getCachedSmallPosterOf(series);
    }

    public Bitmap getCachedPosterOf(Series series) {
        return this.imageRepository.getCachedPosterOf(series);
    }

    /* For search */

    public Bitmap getCachedPosterOf(SearchResult series) {
        return this.imageRepository.getCachedPosterOf(series.toSeries());
    }

    public Bitmap getPosterOf(SearchResult result) throws ConnectionFailedException, NetworkUnavailableException {
        Series resultAsSeries = result.toSeries();
        Bitmap localPoster = this.getPosterOf(resultAsSeries);
        if (localPoster == null) {
            String posterUrl = result.poster();

            if (Strings.isNullOrBlank(posterUrl)) {
                return null;
            }

            Bitmap ephemeralPoster = this.imageRepository.getEphemeralSeriesPosterOf(resultAsSeries);
            if (ephemeralPoster == null) {
                Bitmap downloadedPoster = BitmapFactory.decodeStream(this.getStream(posterUrl));

                this.imageRepository.saveEphemeralSeriesPoster(resultAsSeries, downloadedPoster);
                ephemeralPoster = this.imageRepository.getEphemeralSeriesPosterOf(resultAsSeries);
            }

            return ephemeralPoster;
        } else {
            return localPoster;   
        }
    }

    /* Auxiliary */

    private InputStream getStream(String url) throws ConnectionFailedException, NetworkUnavailableException {
        return new BufferedInputStream(new FlushedInputStream(this.communications.streamFor(url)));
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it
     * reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = this.in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = this.read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
