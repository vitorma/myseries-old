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

    }

    public String getPosterPath(Series series) {
        return this.imageRepository.getPosterOf(series);
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

            this.imageRepository.saveSeriesPoster(series, poster);
        } catch (Exception e) {
            //TODO Log
        }
    }

    public String getPosterOf(SearchResult result) {
        Series resultAsSeries = result.toSeries();
        String localPoster = this.getPosterPath(resultAsSeries);
        if (localPoster == null) {
            String posterUrl = result.poster();

            if (Strings.isNullOrBlank(posterUrl)) {
                return null;
            }

            return posterUrl;
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
