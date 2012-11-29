package mobi.myseries.application.update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;
import android.util.Log;

/*
 * TODO (Reul): add a 30s timeout to each update.
 */

class Updater {
    private LocalizationProvider localizationProvider;
    private ImageService imageService;
    private SeriesSource seriesSource;

    Updater(SeriesSource seriesSource, LocalizationProvider localizationProvider,
            ImageService imageService) {
        this.seriesSource = seriesSource;
        this.localizationProvider = localizationProvider;
        this.imageService = imageService;
    }

    UpdateResult updateDataOf(final Series series) {
        Log.d(getClass().getName(),
                "Updating series: " + series.name());

        Log.d(getClass().getName(), "Last updated: " + series.lastUpdate());

        Log.d(getClass().getName(), "Updating " + series.name());

        Exception error = null;

        try {

            ExecutorService executor = Executors.newSingleThreadExecutor();

            DownloadSeriesTask downloadTask =
                    new DownloadSeriesTask(seriesSource, localizationProvider, series);

            executor.submit(downloadTask);
            executor.shutdown();

            if (!executor.awaitTermination(UpdatePolicy.updateTimeout(), TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
                return new UpdateResult().withError(new UpdateTimeoutException());

            } else if (!downloadTask.successful()) {
                return new UpdateResult().withError(downloadTask.error());

            }

            series.mergeWith(downloadTask.downloadedSeries());

            Log.d(getClass().getName(), "Data of " + series.name() + " updated.");

        } catch (InterruptedException e) {
            //TODO (Reul): Something to do here?

            e.printStackTrace();
            error = e;
        }

        return new UpdateResult().withError(error);

    }

    void updatePosterOf(final Series series) {
        Log.d(getClass().getName(), "Downloading poster of " + series.name());
        imageService.downloadAndSavePosterOf(series);

        Log.d(getClass().getName(),
                "Poster of " + series.name() + " updated. (" + series.posterFileName() + ") ");
    }

    boolean fetchUpdateMetadataSince(long dateInMiliseconds)
            throws ConnectionFailedException, ConnectionTimeoutException,
            ParsingFailedException, UpdateMetadataUnavailableException {
        return seriesSource.fetchUpdateMetadataSince(dateInMiliseconds);
    }

    private static class DownloadSeriesTask implements Runnable {
        private Series series;
        private Series downloadedSeries;
        private Exception error;
        private SeriesSource seriesSource;
        private LocalizationProvider localizationProvider;

        public DownloadSeriesTask(SeriesSource seriesSource,
                LocalizationProvider localizationProvider, Series series) {
            this.series = series;
            this.seriesSource = seriesSource;
            this.localizationProvider = localizationProvider;
        }

        @Override
        public void run() {
            try {
                downloadedSeries = seriesSource.fetchSeries(series.id(),
                        localizationProvider.language());
            } catch (Exception e) {
                e.printStackTrace();
                this.error = e;
            }
        }

        public Series downloadedSeries() {
            return this.downloadedSeries;
        }

        public Exception error() {
            return this.error;
        }

        public boolean successful() {
            return this.error == null;
        }
    }

}
